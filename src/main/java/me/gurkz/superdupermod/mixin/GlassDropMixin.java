/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.mixin;

import net.fabricmc.fabric.api.tag.convention.v2.ConventionalBlockTags;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.storage.loot.LootParams;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(BlockBehaviour.BlockStateBase.class)
public class GlassDropMixin {
    @Inject(method = "getDrops", at = @At("RETURN"), cancellable = true)
    private void forceGlassDrops(LootParams.Builder lootParams, CallbackInfoReturnable<List<ItemStack>> cir) {
        BlockBehaviour.BlockStateBase state = (BlockBehaviour.BlockStateBase) (Object) this;

        if (!state.is(ConventionalBlockTags.GLASS_BLOCKS) && !state.is(ConventionalBlockTags.GLASS_PANES)) {
            return;
        }

        List<ItemStack> currentDrops = cir.getReturnValue();

        if (currentDrops.isEmpty()) {
            cir.setReturnValue(List.of(new ItemStack(state.getBlock())));
        } else {
            List<ItemStack> newDrops = new ArrayList<>(currentDrops);
            newDrops.add(new ItemStack(state.getBlock()));
            cir.setReturnValue(newDrops);
        }
    }
}
