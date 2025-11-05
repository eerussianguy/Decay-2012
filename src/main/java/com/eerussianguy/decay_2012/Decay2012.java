package com.eerussianguy.decay_2012;

import java.util.function.Consumer;

import javax.annotation.Nullable;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.decay_2012.client.ClientForgeEvents;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.loading.FMLEnvironment;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.ItemStackedOnOtherEvent;

import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.component.food.FoodCapability;
import net.dries007.tfc.common.component.food.FoodComponent;
import net.dries007.tfc.common.component.food.IFood;
import net.dries007.tfc.util.Helpers;
import net.dries007.tfc.util.calendar.Calendars;

@Mod(Decay2012.MOD_ID)
public class Decay2012
{
    public static final String MOD_ID = "decay_2012";

    public Decay2012(ModContainer mod, IEventBus bus)
    {
        final IEventBus frogBus = NeoForge.EVENT_BUS;
        if (FMLEnvironment.dist == Dist.CLIENT)
        {
            ClientForgeEvents.init(frogBus, bus);
        }
        frogBus.addListener(Decay2012::onItemStacked);

        mod.registerConfig(ModConfig.Type.CLIENT, DecayConfig.CLIENT_SPEC);
        mod.registerConfig(ModConfig.Type.SERVER, DecayConfig.SERVER_SPEC);
    }

    public static ResourceLocation identifier(String path)
    {
        return ResourceLocation.fromNamespaceAndPath(Decay2012.MOD_ID, path);
    }

    public static boolean isFood(ItemStack stack)
    {
        return isRenderable(FoodCapability.get(stack));
    }

    public static void ifFood(ItemStack stack, Consumer<IFood> consumer)
    {
        final IFood cap = FoodCapability.get(stack);
        if (isRenderable(cap))
            consumer.accept(cap);
    }

//        return stack.getCapability(FoodCapability.CAPABILITY).map(food -> !food.isTransientNonDecaying() || food instanceof FoodHandler.Dynamic).orElse(false);
    private static boolean isRenderable(@Nullable IFood cap)
    {
        return cap != null && cap.getCreationDate() != FoodComponent.TRANSIENT_NEVER_DECAY_FLAG;
    }

    public static int getWeightBarWidth(ItemStack stack)
    {
        return Math.round(13f * stack.getCount() / stack.getMaxStackSize());
    }

    public static float getPercentDecayed(IFood food, boolean isClient)
    {
        final long creation = food.getCreationDate();
        if (isNonDecaying(creation)) return 0f;
        if (food.isRotten()) return 1f;
        final float actual = Math.max(Calendars.get(isClient).getTicks() - creation, 0f) / (food.getRottenDate() - creation);
        return Mth.clamp(actual * actual, 0f, 1f); // quadratic easing. replicates behavior of the first decay not passing as fast.
    }

    private static boolean isNonDecaying(long creation)
    {
        return creation == FoodComponent.TRANSIENT_NEVER_DECAY_FLAG || creation == FoodComponent.INVISIBLE_NEVER_DECAY_FLAG || creation == FoodComponent.NEVER_DECAY_FLAG;
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
        return !isNonDecaying(food.getCreationDate()) && !food.isRotten();
    }

    public static void onItemStacked(ItemStackedOnOtherEvent event)
    {
        final ItemStack stack = event.getStackedOnItem();
        final ItemStack other = event.getCarriedItem();
        final ClickAction action = event.getClickAction();
        final Slot slot = event.getSlot();
        final Player player = event.getPlayer();

        Decay2012.ifFood(stack, food -> {
            if (action == ClickAction.SECONDARY && slot.allowModification(player) && Helpers.isItem(other, TFCTags.Items.TOOLS_KNIFE) && DecayConfig.SERVER.enableCuttingDecay.get())
            {
                final int newCount = getCountAfterCutting(stack, food, player.level().isClientSide);
                if (newCount != -1)
                {
                    FoodCapability.setCreationDate(stack, FoodCapability.getRoundedCreationDate());

                    Helpers.playSound(player.level(), player.blockPosition(), SoundEvents.SHEEP_SHEAR);

                    if (player.level() instanceof ServerLevel server)
                        other.hurtAndBreak(1, server, player, p -> {}); // damage knife
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
