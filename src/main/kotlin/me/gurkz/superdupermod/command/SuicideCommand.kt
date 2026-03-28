/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import it.unimi.dsi.fastutil.ints.IntArrayList
import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.core.component.DataComponents
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerLevel
import net.minecraft.util.Mth
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.entity.projectile.FireworkRocketEntity
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.component.FireworkExplosion
import net.minecraft.world.item.component.Fireworks
import net.minecraft.world.level.GameType
import net.minecraft.world.phys.Vec3
import net.silkmc.silk.commands.PermissionLevel
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.logging.logger
import net.silkmc.silk.core.text.literalText

object SuicideCommand {
    val suicideDamageType: ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, SuperDuperMod.id("suicide"))

    private fun createColor(red: Int, green: Int, blue: Int): Int {
        return (red shl 16) or (green shl 8) or blue
    }

    private fun summonFirework(pos: Vec3, level: ServerLevel) {
        try {
            val explosion = FireworkExplosion(
                FireworkExplosion.Shape.LARGE_BALL,
                IntArrayList(intArrayOf(
                    createColor(0, 255, 0)
                )),
                IntArrayList(intArrayOf(
                    createColor(255, 0, 0)
                )),
                true,
                true
            )

            val fireworks = Fireworks(
                Mth.clamp(2, 0, 3),
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

        } catch (e: IllegalArgumentException) {
            logger().error("something went wrong ${e.message}")
        }
    }

    fun register() {
        command("suicide") {
            requires(KPermissions.require("superdupermod.command.suicide", PermissionLevel.NONE.level))

            runs {
                val player = source.player

                if (player == null) {
                    source.sendFailure(literalText("only players can run /suicide"))
                    return@runs
                }

                if (player.gameMode() === GameType.CREATIVE) {
                    source.sendFailure(literalText("only players in survival can run /suicide"))
                    return@runs
                }

                val damageSource = DamageSource(
                    player.level().registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .get(suicideDamageType.identifier()).get()
                )

                val pos = player.pos

                summonFirework(pos, source.level)

                player.hurtServer(source.level, damageSource, 20.0f)
            }
        }
    }
}