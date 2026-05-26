/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.network

import me.gurkz.superdupermod.SuperDuperMod.NET_CHANNEL
import me.gurkz.superdupermod.packet.RespawnPlayerPacket
import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.arguments.EntityArgument
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText

object RespawnPlayer {
    fun initServer() {
        NET_CHANNEL.registerClientboundDeferred(RespawnPlayerPacket::class.java)

        command("superdupermod") {
            literal("respawn") {
                requires(KPermissions.require("superdupermod.command.respawn", 4))
                argument("target", EntityArgument.player()) { player ->
                    runs {
                        val target = player().findSinglePlayer(source)

                        if (target.isAlive) {
                            val text = literalText("could not respawn ") {
                                color = ChatFormatting.RED.color
                                text(target.displayName)
                                text(", they are alive")
                            }

                            source.sendFailure(text)
                            return@runs
                        }

                        val requester = source.textName

                        NET_CHANNEL.serverHandle(target).send(RespawnPlayerPacket(requester))

                        val text = literalText("respawning ") {
                            text(target.displayName)
                        }

                        source.sendSuccess({ text }, true)
                    }
                }
            }
        }
    }
}