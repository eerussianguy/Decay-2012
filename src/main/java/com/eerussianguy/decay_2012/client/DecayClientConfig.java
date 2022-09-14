package com.eerussianguy.decay_2012.client;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class DecayClientConfig
{
    public final ForgeConfigSpec.BooleanValue enableTFCDecayDisplay;
    public final ForgeConfigSpec.BooleanValue enableFoodShowsStackCount;
    public final ForgeConfigSpec.IntValue maxOunces;

    public DecayClientConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        innerBuilder.push("general");

        enableTFCDecayDisplay = builder.apply("enableTFCDecayDisplay").comment("If false, automatically hide the 'rotten in X days' tooltip.").define("enableTFCDecayDisplay", false);
        enableFoodShowsStackCount = builder.apply("enableFoodShowsStackCount").comment("If true, food stack counts will render behind the weight bar").define("enableFoodShowsStackCount", false);
        maxOunces = builder.apply("maxOunces").comment("The value that should be displayed as the maximum ounces a food item could weigh. By default, a full stack is 160 ounces.").defineInRange("maxOunces", 16, 1, Integer.MAX_VALUE);

        innerBuilder.pop();
    }
}
