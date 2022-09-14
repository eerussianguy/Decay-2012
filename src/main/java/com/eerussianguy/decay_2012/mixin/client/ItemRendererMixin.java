package com.eerussianguy.decay_2012.mixin.client;

import net.minecraft.client.gui.Font;
import net.minecraft.client.renderer.entity.ItemRenderer;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.decay_2012.Decay2012;
import com.eerussianguy.decay_2012.DecayConfig;
import com.mojang.blaze3d.systems.RenderSystem;
import com.mojang.blaze3d.vertex.BufferBuilder;
import com.mojang.blaze3d.vertex.Tesselator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.gen.Invoker;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class ItemRendererMixin
{
    @Unique
    private ItemStack currentStack = ItemStack.EMPTY;

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At("HEAD"))
    private void inject$renderGuiTop(Font pFr, ItemStack stack, int pXPosition, int pYPosition, String pText, CallbackInfo ci)
    {
        currentStack = stack;
    }

    @ModifyArg(
        method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V",
        at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/Font;drawInBatch(Ljava/lang/String;FFIZLcom/mojang/math/Matrix4f;Lnet/minecraft/client/renderer/MultiBufferSource;ZII)I"
        )
    )
    private String inject$onDrawCount(String value)
    {
        if (Decay2012.isFood(currentStack) && !DecayConfig.CLIENT.enableFoodShowsStackCount.get())
        {
            return "";
        }
        return value;
    }

    @Inject(method = "renderGuiItemDecorations(Lnet/minecraft/client/gui/Font;Lnet/minecraft/world/item/ItemStack;IILjava/lang/String;)V", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;isBarVisible()Z"))
    private void inject$renderCustomDecorations(Font font, ItemStack stack, int x, int y, String text, CallbackInfo ci)
    {
        Decay2012.ifFood(stack, food -> {
            RenderSystem.disableDepthTest();
            RenderSystem.disableTexture();
            RenderSystem.disableBlend();
            final Tesselator tesselator = Tesselator.getInstance();
            final BufferBuilder buffer = tesselator.getBuilder();

            final int color = Decay2012.getDecayBarColor(food);
            this.invoke$fillRect(buffer, x + 2, y + 13, 13, 2, 0, 0, 0, 255);
            this.invoke$fillRect(buffer, x + 2, y + 13, Decay2012.getDecayBarWidth(food), 1, color >> 16 & 255, color >> 8 & 255, color & 255, 255);

            this.invoke$fillRect(buffer, x + 2, y + 15, 13, 2, 0, 0, 0, 255);
            this.invoke$fillRect(buffer, x + 2, y + 15, Decay2012.getWeightBarWidth(stack), 1, 255, 255, 255, 255);

            RenderSystem.enableBlend();
            RenderSystem.enableTexture();
            RenderSystem.enableDepthTest();

        });
    }

    @Invoker("fillRect")
    abstract void invoke$fillRect(BufferBuilder pRenderer, int pX, int pY, int pWidth, int pHeight, int pRed, int pGreen, int pBlue, int pAlpha);
}
