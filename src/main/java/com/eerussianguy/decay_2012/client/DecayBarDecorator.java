package com.eerussianguy.decay_2012.client;

import com.eerussianguy.decay_2012.Decay2012;
import com.eerussianguy.decay_2012.DecayConfig;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.client.IItemDecorator;
import net.neoforged.neoforge.client.event.RegisterItemDecorationsEvent;

public enum DecayBarDecorator implements IItemDecorator
{
    INSTANCE;

    @Override
    public boolean render(GuiGraphics graphics, Font font, ItemStack stack, int x, int y)
    {
        Decay2012.ifFood(stack, food -> {
            graphics.pose().pushPose();

            if (DecayConfig.CLIENT.enableFoodDecayRender.get())
            {
                final int color = Decay2012.getDecayBarColor(food);
                graphics.fill(RenderType.guiOverlay(), x + 2, y + 13, x + 13, y + 14, 0xFF00000);
                graphics.fill(RenderType.guiOverlay(), x + 2, y + 13, x + 2 + Decay2012.getDecayBarWidth(food), y + 14, color | 0xFF000000);
            }
            if (DecayConfig.CLIENT.enableFoodWeightRender.get())
            {
                graphics.fill(RenderType.guiOverlay(), x + 2, y + 14, x + 13, y + 15, 0xFF00000);
                graphics.fill(RenderType.guiOverlay(), x + 2, y + 14, x + 2 + Decay2012.getWeightBarWidth(stack), y + 15, 0xFFFFFFFF);
            }

            graphics.pose().popPose();
        });
        return Decay2012.isFood(stack);
    }

    public static void onItemDecorations(RegisterItemDecorationsEvent event)
    {
        for (Item item : BuiltInRegistries.ITEM)
        {
            event.register(item, INSTANCE);
        }
    }
}
