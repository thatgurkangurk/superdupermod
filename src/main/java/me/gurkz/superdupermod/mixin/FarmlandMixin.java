/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.mixin;

import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.Entity;
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
        me.gurkz.superdupermod.kotlin.mixin.FarmlandMixin.disableTramplingWithFeatherFalling(level, entity, ci);
    }
}
