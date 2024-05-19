package com.eerussianguy.decay_2012.client;

import java.util.List;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.eventbus.api.IEventBus;

import com.eerussianguy.decay_2012.Decay2012;
import com.eerussianguy.decay_2012.DecayConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

import net.dries007.tfc.client.ClientHelpers;

public class ClientForgeEvents
{
    public static void init()
    {
        final IEventBus bus = MinecraftForge.EVENT_BUS;

        bus.addListener(ClientForgeEvents::onTooltip);
        FMLJavaModLoadingContext.get().getModEventBus().addListener(DecayBarDecorator::onItemDecorations);
    }

    private static void onTooltip(ItemTooltipEvent event)
    {
        final List<Component> tooltip = event.getToolTip();
        final ItemStack item = event.getItemStack();

        Decay2012.ifFood(item, food -> {
            final float max = (float) DecayConfig.CLIENT.maxOunces.get();
            final float currentOz = max * item.getCount() / item.getMaxStackSize();
            tooltip.add(Component.literal(format(currentOz) + " / " + format(max) + getWeightName().getString()));

            if (ClientHelpers.hasShiftDown() && Decay2012.getCountAfterCutting(item, food, true) != -1)
            {
                tooltip.add(Component.translatable("decay_2012.tooltip.can_be_cut").withStyle(ChatFormatting.ITALIC).withStyle(ChatFormatting.GRAY));
            }
        });
    }

    private static Component getWeightName()
    {
        return DecayConfig.CLIENT.enableEuropeanMode.get() ? Component.translatable("decay_2012.tooltip.grams") : Component.translatable("decay_2012.tooltip.ounces");
    }

    private static String format(float value)
    {
        return String.format("%.1f", value);
    }
}
