/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.network

import kotlinx.serialization.Serializable
import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.arguments.EntityArgument
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.network.packet.s2cPacket

object RespawnPlayer {

    @Serializable
    data class RespawnPlayerPacket(
        val requester: String
    )

    val respawnPlayerPacketS2C = s2cPacket<RespawnPlayerPacket>(
        SuperDuperMod.id("respawn_player_packet_s2c"),
    )

    fun initServer() {
        command("superdupermod") {
            literal("respawn") {
                requires(KPermissions.require("superdupermod.command.respawn", 4))
                argument("target", EntityArgument.player()) { player ->
                    runs {
                        val target = player().findSinglePlayer(source)

                        if (target.isAlive) {
                            val text = literalText("could not respawn ") {
                                color = ChatFormatting.RED.color
                                text(target.displayName!!)
                                text(", they are alive")
                            }

                            source.sendFailure(text)
                            return@runs
                        }

                        val requester = source.textName

                        respawnPlayerPacketS2C.send(RespawnPlayerPacket(requester), target)

                        val text = literalText("respawning ") {
                            text(target.displayName!!)
                        }

                        source.sendSuccess({ text }, true)
                    }
                }
            }
        }
    }
}