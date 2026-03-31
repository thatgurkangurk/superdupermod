/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.data

import net.minecraft.core.registries.Registries
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.core.entity.serverWorld
import kotlin.math.asin
import kotlin.math.atan2

data class Location(
    val position: Vec3 = Vec3.ZERO,
    val cameraVector: Vec3 = Vec3.ZERO,
    val dimension: Identifier? = null
)

fun Location.teleportPlayerHere(player: ServerPlayer): Result<Unit> {
    val server = player.serverWorld.server ?: return Result.failure(Exception("server was not found"))
    val dimensionIdentifier = this.dimension ?: Identifier.withDefaultNamespace("overworld")
    val level = server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionIdentifier)) ?: return Result.failure(
        Exception("dimension $dimensionIdentifier was not found")
    )

    val look = this.cameraVector.normalize()
    val yaw = (Math.toDegrees(atan2(look.z, look.x)) - 90.0).toFloat()
    val pitch = (-Math.toDegrees(asin(look.y))).toFloat()

    player.teleportTo(
        level,
        this.position.x,
        this.position.y,
        this.position.z,
        emptySet(),
        yaw,
        pitch,
        true
    )

    return Result.success(Unit)
}