/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.mixin;

import me.gurkz.superdupermod.event.DeathLocationEventListeners;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.damagesource.DamageSource;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayer.class)
public abstract class PlayerDeathMixin {
    @Inject(method = "die", at = @At("HEAD"))
    void onPlayerDeath(DamageSource source, CallbackInfo ci) {
        ServerPlayer me = (ServerPlayer) (Object) this;

        DeathLocationEventListeners.handleDeath(me);
    }
}
