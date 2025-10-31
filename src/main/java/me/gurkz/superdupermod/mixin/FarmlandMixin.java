/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.FarmBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmBlock.class)
public abstract class FarmlandMixin {

    @Inject(
            method = "fallOn",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableTramplingWithFeatherFalling(
            Level level, BlockState blockState, BlockPos blockPos, Entity entity, double fallDistance, CallbackInfo ci
    ) {
        if (!(entity instanceof LivingEntity livingEntity)) return;

        ItemStack boots = livingEntity.getItemBySlot(EquipmentSlot.FEET);

        // Fetch the Feather Falling enchantment holder properly via the registry
        Holder.Reference<Enchantment> featherHolder = level
                .registryAccess()
                .lookupOrThrow(Registries.ENCHANTMENT)
                .getOrThrow(Enchantments.FEATHER_FALLING);

        // Get the level of Feather Falling on the boots
        int featherLevel = EnchantmentHelper.getItemEnchantmentLevel(featherHolder, boots);

        // Cancel trampling if Feather Falling or Slow Falling is active
        if (featherLevel > 0 || livingEntity.hasEffect(MobEffects.SLOW_FALLING)) {
            ci.cancel();
        }
    }
}
