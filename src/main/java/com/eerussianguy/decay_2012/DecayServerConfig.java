package com.eerussianguy.decay_2012;

import java.util.function.Function;

import net.neoforged.neoforge.common.ModConfigSpec;

import static net.dries007.tfc.TerraFirmaCraft.MOD_ID;

public class DecayServerConfig
{
    public final ModConfigSpec.BooleanValue enableCuttingDecay;

    public DecayServerConfig(ModConfigSpec.Builder innerBuilder)
    {
        Function<String, ModConfigSpec.Builder> builder = name -> innerBuilder.translation(MOD_ID + ".config.client." + name);

        innerBuilder.push("general");

        enableCuttingDecay = builder.apply("enableCuttingDecay").comment("Allow cutting decay off of food.").define("enableCuttingDecay", true);

        innerBuilder.pop();
    }
}
