package com.eerussianguy.decay_2012;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.eerussianguy.decay_2012.client.ClientForgeEvents;
import com.eerussianguy.decay_2012.client.ClientModEvents;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
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
            ClientModEvents.init();
        }
    }

    public static ResourceLocation identifier(String path)
    {
        return new ResourceLocation(Decay2012.MOD_ID, path);
    }

    public static boolean isFood(ItemStack stack)
    {
        return stack.getCapability(FoodCapability.CAPABILITY).isPresent();
    }

    public static void ifFood(ItemStack stack, Consumer<IFood> consumer)
    {
        stack.getCapability(FoodCapability.CAPABILITY).ifPresent(consumer::accept);
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
        return food.getCreationDate() != FoodHandler.UNKNOWN_CREATION_DATE && food.getRottenDate() != FoodHandler.ROTTEN_DATE && food.getRottenDate() != FoodHandler.NEVER_DECAY_DATE;
    }

}
