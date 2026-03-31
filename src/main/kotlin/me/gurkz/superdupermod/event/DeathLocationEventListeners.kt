/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.event

import eu.pb4.playerdata.api.PlayerDataApi
import me.gurkz.superdupermod.data.DataStorages
import me.gurkz.superdupermod.data.DeathLocationData
import me.gurkz.superdupermod.data.Location
import me.gurkz.superdupermod.data.teleportPlayerHere
import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.logging.logger
import net.silkmc.silk.core.text.literalText

object DeathLocationEventListeners {
    fun register() {
        command("lastdeath") {
            requires(KPermissions.require("superdupermod.command.lastdeath", true))

            runs {
                val player = source.playerOrException
                val data = PlayerDataApi.getCustomDataFor(player, DataStorages.DEATH_LOCATION)

                if (data == null) {
                    source.sendFailure(literalText("could not find a death location") {
                        color = ChatFormatting.RED.color
                    })
                    return@runs
                }

                data.location.teleportPlayerHere(player)
                    .onFailure { error ->
                        val errorMessage = error.message ?: "an unknown error occurred."
                        source.sendFailure(Component.literal(errorMessage))
                    }
                    .onSuccess {
                        source.sendSuccess({ Component.literal("teleporting to your last location") }, true)
                    }
            }
        }
    }

    @JvmStatic
    fun handleDeath(entity: ServerPlayer) {
        logger().info("player died")
        val location = Location(
            entity.pos,
            entity.camera.headLookAngle,
            entity.level().dimension().identifier()
        )

        PlayerDataApi.setCustomDataFor(entity, DataStorages.DEATH_LOCATION, DeathLocationData(location))
    }
}