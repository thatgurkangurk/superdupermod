/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.util

import me.gurkz.superdupermod.mixin.ServerAccessorMixin
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

val ServerPlayer.server: MinecraftServer?
    get() = (this as ServerAccessorMixin).`superdupermod$getServer`()