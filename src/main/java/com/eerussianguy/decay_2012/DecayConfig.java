package com.eerussianguy.decay_2012;

import java.util.function.Function;

import org.apache.commons.lang3.tuple.Pair;
import net.minecraftforge.common.ForgeConfigSpec;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;

import com.eerussianguy.decay_2012.client.DecayClientConfig;
import net.dries007.tfc.util.Helpers;

public class DecayConfig
{
    public static final DecayClientConfig CLIENT = register(ModConfig.Type.CLIENT, DecayClientConfig::new);
    public static final DecayServerConfig SERVER = register(ModConfig.Type.SERVER, DecayServerConfig::new);

    public static void init() {}

    private static <C> C register(ModConfig.Type type, Function<ForgeConfigSpec.Builder, C> factory)
    {
        Pair<C, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(factory);
        if (!Helpers.BOOTSTRAP_ENVIRONMENT) ModLoadingContext.get().registerConfig(type, specPair.getRight());
        return specPair.getLeft();
    }
}
