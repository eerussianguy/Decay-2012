package com.eerussianguy.decay_2012.client;

import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import com.eerussianguy.decay_2012.DecayConfig;
import net.dries007.tfc.config.FoodExpiryTooltipStyle;
import net.dries007.tfc.config.TFCConfig;

public class ClientModEvents
{
    public static void init()
    {
        final IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        bus.addListener(ClientModEvents::setup);
    }

    private static void setup(FMLClientSetupEvent event)
    {
        if (!DecayConfig.CLIENT.enableTFCDecayDisplay.get())
        {
            TFCConfig.CLIENT.foodExpiryTooltipStyle.set(FoodExpiryTooltipStyle.NONE); // evil laughter
        }
    }
}
