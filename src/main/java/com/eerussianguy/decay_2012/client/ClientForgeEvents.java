package com.eerussianguy.decay_2012.client;

import java.util.List;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import com.eerussianguy.decay_2012.Decay2012;
import com.eerussianguy.decay_2012.DecayConfig;
import net.dries007.tfc.util.Helpers;

public class ClientForgeEvents
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(ClientForgeEvents::onTooltip);
    }

    private static void onTooltip(ItemTooltipEvent event)
    {
        final List<Component> tooltip = event.getToolTip();
        final ItemStack item = event.getItemStack();

        Decay2012.ifFood(item, food -> {
            final float max = (float) DecayConfig.CLIENT.maxOunces.get();
            final float currentOz = max * item.getCount() / item.getMaxStackSize();
            tooltip.add(Helpers.literal(format(currentOz) + " / " + format(max) + getWeightName()));
        });

    }

    private static String getWeightName()
    {
        return DecayConfig.CLIENT.enableEuropeanMode.get() ? " g." : " oz.";
    }

    private static String format(float value)
    {
        return String.format("%.1f", value);
    }
}
