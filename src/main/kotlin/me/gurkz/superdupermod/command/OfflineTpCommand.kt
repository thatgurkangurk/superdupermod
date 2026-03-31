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
import me.gurkz.superdupermod.data.DataStorages
import me.gurkz.superdupermod.data.teleportPlayerHere
import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.GameProfileArgument
import net.minecraft.network.chat.Component
import net.silkmc.silk.commands.command

object OfflineTpCommand {
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
        val server = source.server

        val playerStorage = PlayerDataApi.getCustomDataFor(server, uuid, DataStorages.OFFLINE_TP) ?: run {
            source.sendFailure(Component.literal("player data could not be found"))
            return
        }

        playerStorage.location.teleportPlayerHere(executingPlayer)
            .onFailure { error ->
                val errorMessage = error.message ?: "an unknown error occurred."
                source.sendFailure(Component.literal(errorMessage))
            }
            .onSuccess {
                source.sendSuccess({ Component.literal("teleported to ${profile.name}'s last known location") }, true)
            }
        return
    }
}