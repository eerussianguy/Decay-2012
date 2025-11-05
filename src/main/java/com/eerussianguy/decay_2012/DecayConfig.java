package com.eerussianguy.decay_2012;

import net.neoforged.neoforge.common.ModConfigSpec;

import com.eerussianguy.decay_2012.client.DecayClientConfig;

public class DecayConfig
{
    public static final DecayClientConfig CLIENT;
    public static final ModConfigSpec CLIENT_SPEC;
    public static final DecayServerConfig SERVER;
    public static final ModConfigSpec SERVER_SPEC;

    static
    {
        final var client = new ModConfigSpec.Builder().configure(DecayClientConfig::new);
        CLIENT = client.getLeft();
        CLIENT_SPEC = client.getRight();
        final var server = new ModConfigSpec.Builder().configure(DecayServerConfig::new);
        SERVER = server.getLeft();
        SERVER_SPEC = server.getRight();
    }
}
