package com.eerussianguy.decay_2012;

import java.util.function.Function;

import net.minecraftforge.common.ForgeConfigSpec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class DecayServerConfig
{
    public final ForgeConfigSpec.BooleanValue enableCuttingDecay;

    public DecayServerConfig(ForgeConfigSpec.Builder innerBuilder)
    {
        Function<String, ForgeConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        innerBuilder.push("general");

        enableCuttingDecay = builder.apply("enableCuttingDecay").comment("Allow cutting decay off of food.").define("enableCuttingDecay", true);

        innerBuilder.pop();
    }
}
