package com.eerussianguy.decay_2012.mixin;

import net.minecraft.util.Mth;
import net.minecraft.world.entity.SlotAccess;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ClickAction;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import com.eerussianguy.decay_2012.Decay2012;
import com.eerussianguy.decay_2012.DecayConfig;
import net.dries007.tfc.common.TFCTags;
import net.dries007.tfc.common.capabilities.food.FoodCapability;
import net.dries007.tfc.util.Helpers;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class ItemMixin
{
    @Inject(method = "overrideOtherStackedOnMe", at = @At("HEAD"), cancellable = true)
    private void inject$overrideOtherStackedOnMe(ItemStack stack, ItemStack other, Slot slot, ClickAction action, Player player, SlotAccess access, CallbackInfoReturnable<Boolean> cir)
    {
        Decay2012.ifFood(stack, food -> {
            if (action == ClickAction.SECONDARY && slot.allowModification(player) && Helpers.isItem(other, TFCTags.Items.KNIVES) && DecayConfig.CLIENT.enableCuttingDecay.get())
            {
                final boolean isClient = player.level.isClientSide;
                final float pct = Decay2012.getPercentDecayed(food, isClient);
                final int count = stack.getCount();
                if (count > 1 && Decay2012.isModifiable(food) && pct > 0.1f && pct < 0.9f)
                {
                    // classic behavior: 25% decayed == you lose 25% of the weight
                    final int newCount = Mth.ceil(pct * count);
                    if (newCount < count) // only if we will actually do something
                    {
                        food.setCreationDate(FoodCapability.getRoundedCreationDate()); // reset the creation date for the food

                        other.hurtAndBreak(1, player, p -> {}); // damage knife
                        stack.setCount(newCount); // set the count to the shrunken amount
                        cir.setReturnValue(true);
                    }
                }
            }
        });
    }
}
