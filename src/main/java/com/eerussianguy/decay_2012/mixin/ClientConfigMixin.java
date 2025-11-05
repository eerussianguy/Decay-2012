package com.eerussianguy.decay_2012.mixin;

import java.util.function.Supplier;
import com.eerussianguy.decay_2012.DecayConfig;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import net.dries007.tfc.config.ClientConfig;
import net.dries007.tfc.config.ConfigBuilder;
import net.dries007.tfc.config.FoodExpiryTooltipStyle;

@Mixin(ClientConfig.class)
public class ClientConfigMixin
{
    @Shadow(remap = false)
    @Mutable
    @Final
    public Supplier<FoodExpiryTooltipStyle> foodExpiryTooltipStyle;

    @Inject(method = "<init>", at = @At("TAIL"), remap = false)
    private void inject$init(ConfigBuilder innerBuilder, CallbackInfo ci)
    {
        foodExpiryTooltipStyle = DecayConfig.CLIENT.foodExpiryTooltipStyle;
    }
}
