package com.eerussianguy.decay_2012.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.decay_2012.Decay2012;
import com.eerussianguy.decay_2012.DecayConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiGraphics.class)
public abstract class GuiGraphicsMixin
{
    @Unique
    private ItemStack decay2012$currentStack = ItemStack.EMPTY;

    @Inject(method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    private void inject$renderGuiTop(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci)
    {
        decay2012$currentStack = stack;
    }

    @ModifyArg(
        method = "renderItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphics;drawString(Lnet/minecraft/client/gui/Font;Ljava/lang/String;IIIZ)I"
        )
    )
    private String inject$onDrawCount(String value)
    {
        if (Decay2012.isFood(decay2012$currentStack) && !DecayConfig.CLIENT.enableFoodShowsStackCount.get())
        {
            return "";
        }
        return value;
    }
}

