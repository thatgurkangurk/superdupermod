/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import com.mojang.brigadier.context.CommandContext
import eu.pb4.playerdata.api.PlayerDataApi
import eu.pb4.playerdata.api.storage.JsonDataStorage
import me.gurkz.superdupermod.data.OfflineTpData
import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.core.registries.Registries
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.resources.ResourceKey
import net.silkmc.silk.commands.command
import kotlin.math.asin
import kotlin.math.atan2

object OfflineTpCommand {
    val DATA_STORAGE = JsonDataStorage("offline_tp", OfflineTpData::class.java)

    fun register() {
        command("offlinetp") {
            alias("otp")
            alias("offline-tp")
            requires(KPermissions.require("superdupermod.command.offline-tp", 2))
            argument("target", GameProfileArgument.gameProfile()) {
                runs {
                    return@runs executeOfflineTp(this)
                }
            }
        }
    }

    private fun executeOfflineTp(context: CommandContext<CommandSourceStack>) {
        val source = context.source
        val executingPlayer = source.player ?: run {
            source.sendFailure(Component.literal("you can only use this command in the game"))
            return
        }

        val profiles = GameProfileArgument.getGameProfiles(context, "target")
        val profile = profiles.firstOrNull() ?: run {
            source.sendFailure(Component.literal("that player was not found"))
            return
        }

        val uuid = profile.id
        val targetName = profile.name
        val server = source.server

        val playerStorage = PlayerDataApi.getCustomDataFor(server, uuid, DATA_STORAGE) ?: run {
            source.sendFailure(Component.literal("player data could not be found"))
            return
        }

        val dimensionIdentifier = playerStorage.dimension ?: Identifier.withDefaultNamespace("overworld")

        val level = server.getLevel(ResourceKey.create(Registries.DIMENSION, dimensionIdentifier))

        if (level == null) {
            source.sendFailure(Component.literal("the player was in the $dimensionIdentifier dimension, which was not found"))
            return
        }

        val look = playerStorage.cameraVector.normalize()
        val yaw = (Math.toDegrees(atan2(look.z, look.x)) - 90.0).toFloat()
        val pitch = (-Math.toDegrees(asin(look.y))).toFloat()

        executingPlayer.teleportTo(
            level,
            playerStorage.leavePosition.x,
            playerStorage.leavePosition.y,
            playerStorage.leavePosition.z,
            emptySet(),
            yaw,
            pitch,
            true
        )

        source.sendSuccess({ Component.literal("teleported to $targetName's last known location") }, true)
        return
    }
}