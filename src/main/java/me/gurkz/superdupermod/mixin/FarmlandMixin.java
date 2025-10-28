/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.mixin;

import net.minecraft.block.FarmlandBlock;
import net.minecraft.block.BlockState;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.Enchantments;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.effect.StatusEffects;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FarmlandBlock.class)
public abstract class FarmlandMixin {
    @Inject(
            method = "onLandedUpon",
            at = @At("HEAD"),
            cancellable = true
    )
    private void disableTramplingWithFeatherFalling(
            World world, BlockState state, BlockPos pos, Entity entity, double fallDistance, CallbackInfo ci
    ) {
        if (entity instanceof LivingEntity livingEntity) {
            if (EnchantmentHelper.getEquipmentLevel(
                    world.getRegistryManager().getOrThrow(RegistryKeys.ENCHANTMENT)
                            .getOrThrow(Enchantments.FEATHER_FALLING), livingEntity
            ) > 0 || livingEntity.hasStatusEffect(StatusEffects.SLOW_FALLING)) {
                ci.cancel();
            }
        }
    }
}
