package com.eerussianguy.decay_2012;

import java.util.function.Consumer;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.loading.FMLEnvironment;

import com.eerussianguy.decay_2012.client.ClientForgeEvents;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.common.capabilities.food.FoodHandler;
import net.dries007.tfc.common.capabilities.food.IFood;
import net.dries007.tfc.config.FoodExpiryTooltipStyle;
import net.dries007.tfc.config.TFCConfig;
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
            if (!DecayConfig.CLIENT.enableTFCDecayDisplay.get())
            {
                TFCConfig.CLIENT.foodExpiryTooltipStyle.set(FoodExpiryTooltipStyle.NONE); // evil laughter
            }
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
        return Math.round(13f / stack.getMaxStackSize() * stack.getCount());
    }

    public static float getPercentDecayed(IFood food, boolean isClient)
    {
        final long creation = food.getCreationDate();
        if (creation == FoodHandler.UNKNOWN_CREATION_DATE) return 0f;
        if (food.isRotten()) return 1f;
        return (float) (Calendars.get(isClient).getTicks() - creation) / (food.getRottenDate() - creation);
    }

    public static int getDecayBarColor(IFood food)
    {
        return getPercentDecayed(food, true) < 0.1f ? 0x00ff00 : 0xff0000;
    }

    public static int getDecayBarWidth(IFood food)
    {
        return Math.round(13f * Math.max(0.1f, getPercentDecayed(food, true)));
    }

    public static boolean isModifiable(IFood food)
    {
        return food.getCreationDate() != FoodHandler.UNKNOWN_CREATION_DATE && food.getRottenDate() != FoodHandler.ROTTEN_DATE && food.getRottenDate() != FoodHandler.NEVER_DECAY_DATE;
    }

}
