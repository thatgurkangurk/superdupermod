/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.util

import it.unimi.dsi.fastutil.ints.IntArrayList
import net.minecraft.core.component.DataComponents
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.phys.Vec3

object FireworkUtil {
    fun createColour(red: Int, green: Int, blue: Int): Int {
        return (red shl 16) or (green shl 8) or blue
    }

    fun summonFirework(pos: Vec3, level: ServerLevel, colour: Int, fadeColour: Int, flightDuration: Int) {
        val explosion = FireworkExplosion(
            FireworkExplosion.Shape.LARGE_BALL,
            IntArrayList(intArrayOf(colour)),
            IntArrayList(intArrayOf(fadeColour)),
            true,
            true
        )

        val fireworks = Fireworks(
            Mth.clamp(flightDuration, 0, 3),
            listOf(explosion),
        )

        val stack = ItemStack(Items.FIREWORK_ROCKET)
        stack.set(DataComponents.FIREWORKS, fireworks)

        val rocket = FireworkRocketEntity(
            level,
            pos.x,
            pos.y,
            pos.z,
            stack
        )

        level.addFreshEntity(rocket)
    }
}