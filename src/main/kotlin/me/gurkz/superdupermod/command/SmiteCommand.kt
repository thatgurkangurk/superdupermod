/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.entity.EntityType
import net.minecraft.world.entity.LightningBolt
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.serverWorld
import net.minecraft.commands.arguments.EntityArgument

object SmiteCommand {
    private fun smite(player: ServerPlayer) {
        val world = player.serverWorld
        val lightningBolt = LightningBolt(EntityType.LIGHTNING_BOLT, world)

        lightningBolt.setPos(player.pos)

        world.addFreshEntity(lightningBolt)
    }

    fun register() {
        command("smite") {
            requires(KPermissions.require("superdupermod.command.smite", 4))
            argument("target", EntityArgument.player()) runs {
                val target = EntityArgument.getPlayer(this, "target")

                smite(player = target)
            }
        }
    }
}