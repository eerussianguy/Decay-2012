package com.eerussianguy.decay_2012;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.ItemStackedOnOtherEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.eerussianguy.decay_2012.client.ClientForgeEvents;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

@Mod(Decay2012.MOD_ID)
public class Decay2012
{
    public static final String MOD_ID = "decay_2012";

    public Decay2012()
    {
        DecayConfig.init();

        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientForgeEvents.init();
        }
        final IEventBus bus = MinecraftForge.EVENT_BUS;
        bus.addListener(Decay2012::onItemStacked);
    }

    public static ResourceLocation identifier(String path)
    {
        return new ResourceLocation(Decay2012.MOD_ID, path);
    }

    public static boolean isFood(ItemStack stack)
    {
        return stack.getCapability(FoodCapability.CAPABILITY).map(food -> !food.isTransientNonDecaying()).orElse(false);
    }

    public static void ifFood(ItemStack stack, Consumer<IFood> consumer)
    {
        stack.getCapability(FoodCapability.CAPABILITY).filter(food -> !food.isTransientNonDecaying()).ifPresent(consumer);
    }

    public static int getWeightBarWidth(ItemStack stack)
    {
        return Math.round(13f * stack.getCount() / stack.getMaxStackSize());
    }

    public static float getPercentDecayed(IFood food, boolean isClient)
    {
        final long creation = food.getCreationDate();
        if (creation == FoodHandler.UNKNOWN_CREATION_DATE) return 0f;
        if (food.isRotten()) return 1f;
        final float actual = (float) (Calendars.get(isClient).getTicks() - creation) / (food.getRottenDate() - creation);
        return actual * actual; // quadratic easing. replicates behavior of the first decay not passing as fast.
    }

    public static int getDecayBarColor(IFood food)
    {
        return getPercentDecayed(food, true) < 0.1f ? 0x00ff00 : 0xff0000;
    }

    public static int getDecayBarWidth(IFood food)
    {
        final float pct = getPercentDecayed(food, true);
        if (pct < 0.1f)
        {
            // if less than 0.1, decrease linearly from full to empty
            return Math.round(13f * (0.1f - pct) * 10f);
        }
        else
        {
            // if more than 0.1, full bar again, but now red, and then decrease to nothing
            return Math.round(13f * (1f - (pct / 0.9f)));
        }
    }

    public static boolean isModifiable(IFood food)
    {
        return food.getCreationDate() != FoodHandler.UNKNOWN_CREATION_DATE && food.getRottenDate() != FoodHandler.ROTTEN_DATE && food.getRottenDate() != FoodHandler.NEVER_DECAY_DATE && !food.isTransientNonDecaying();
    }

    public static void onItemStacked(ItemStackedOnOtherEvent event)
    {
        final ItemStack other = event.getStackedOnItem();
        final ItemStack stack = event.getCarriedItem();
        final ClickAction action = event.getClickAction();
        final Slot slot = event.getSlot();
        final Player player = event.getPlayer();

        Decay2012.ifFood(stack, food -> {
            if (action == ClickAction.SECONDARY && slot.allowModification(player) && Helpers.isItem(other, TFCTags.Items.KNIVES) && DecayConfig.SERVER.enableCuttingDecay.get())
            {
                final int newCount = getCountAfterCutting(stack, food, player.level().isClientSide);
                if (newCount != -1)
                {
                    food.setCreationDate(FoodCapability.getRoundedCreationDate()); // reset the creation date for the food

                    Helpers.playSound(player.level(), player.blockPosition(), SoundEvents.SHEEP_SHEAR);

                    other.hurtAndBreak(1, player, p -> {}); // damage knife
                    stack.setCount(newCount); // set the count to the shrunken amount
                    event.setCanceled(true);
                }
            }
        });
    }

    /**
     * @return -1 if cutting would be pointless
     */
    public static int getCountAfterCutting(ItemStack stack, IFood food, boolean isClient)
    {
        final float pct = Decay2012.getPercentDecayed(food, isClient);
        final int count = stack.getCount();
        if (count > 1 && Decay2012.isModifiable(food) && pct > 0.1f && pct < 0.9f)
        {
            // classic behavior: 25% decayed == you lose 25% of the weight
            final int newCount = Mth.ceil((1 - pct) * count);
            if (newCount < count)
            {
                return newCount;
            }
        }
        return -1;
    }
}
