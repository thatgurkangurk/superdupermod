/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import me.gurkz.superdupermod.permission.KPermissions
import me.gurkz.superdupermod.util.server
import net.minecraft.ChatFormatting
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.protocol.game.ClientboundSetTitleTextPacket
import net.minecraft.server.level.ServerPlayer
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.serverWorld
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
import java.util.UUID
import kotlin.time.Duration.Companion.seconds
import kotlin.time.TimeMark
import kotlin.time.TimeSource

enum class TpaType { TPA, TPA_HERE }

data class TpaRequest(val requesterUuid: UUID, val requestTime: TimeMark, val type: TpaType) {
    val isExpired: Boolean get() = requestTime.elapsedNow() > TpaCommands.TIMEOUT
}

object TpaCommands {
    private val pendingRequests = mutableMapOf<UUID, TpaRequest>()
    val TIMEOUT = 60.seconds

    private val ServerPlayer.serverList
        get() = this.server?.playerList

    private fun ServerPlayer.sendColorMsg(msg: String, textColor: Int?) {
        this.sendText(literalText(msg) { color = textColor })
    }

    fun register() {
        command("tpa") {
            requires(KPermissions.require("superdupermod.command.tpa", 1))
            argument("target", EntityArgument.player()) {
                runs { handleRequest(source.player, EntityArgument.getPlayer(this, "target"), TpaType.TPA) }
            }
        }

        command("tpahere") {
            requires(KPermissions.require("superdupermod.command.tpahere", 1))
            argument("target", EntityArgument.player()) {
                runs { handleRequest(source.player, EntityArgument.getPlayer(this, "target"), TpaType.TPA_HERE) }
            }
        }

        command("tpaaccept") {
            requires(KPermissions.require("superdupermod.command.tpaaccept", 1))
            runs { handleTpaAccept(source.player) }
        }

        command("tpadeny") {
            requires(KPermissions.require("superdupermod.command.tpadeny", 1))
            runs { handleTpaDeny(source.player) }
        }

        command("tpacancel") {
            requires(KPermissions.require("superdupermod.command.tpacancel", 1))
            runs { handleTpaCancel(source.player) }
        }
    }

    private fun handleRequest(requester: ServerPlayer?, target: ServerPlayer, type: TpaType): Int {
        if (requester == null) return 0

        if (requester.uuid == target.uuid) {
            requester.sendColorMsg("you can not tpa to yourself", ChatFormatting.RED.color)
            return 0
        }

        val existingEntry = pendingRequests.entries.find { it.value.requesterUuid == requester.uuid }
        if (existingEntry != null) {
            if (!existingEntry.value.isExpired) {
                requester.sendColorMsg("you already have a pending tpa request. use /tpacancel first.", ChatFormatting.RED.color)
                return 0
            }
            pendingRequests.remove(existingEntry.key)
        }

        pendingRequests[target.uuid] = TpaRequest(requester.uuid, TimeSource.Monotonic.markNow(), type)

        val isTpa = type == TpaType.TPA
        requester.sendColorMsg("${if (isTpa) "tpa" else "tpa here"} request sent to ${target.gameProfile.name}. they have ${TIMEOUT.inWholeSeconds} seconds to accept", ChatFormatting.AQUA.color)

        val messageToTarget = literalText {
            text(requester.displayName!!)
            text(" has requested ")
            color = ChatFormatting.AQUA.color
            if (isTpa) {
                text("to teleport to you")
            } else {
                text("you to teleport to them")
            }

            newLine()

            text("type ")

            text("/tpaaccept") {
                clickEvent = ClickEvent.SuggestCommand("/tpaaccept")
                bold = true
            }

            text(" or ")

            text("/tpadeny") {
                clickEvent = ClickEvent.SuggestCommand("/tpadeny")
                bold = true
            }
        }

        target.sendText(messageToTarget)

        return 1
    }

    private fun handleTpaAccept(target: ServerPlayer?): Int {
        if (target == null) return 0

        val request = pendingRequests.remove(target.uuid)

        if (request == null || request.isExpired) {
            target.sendColorMsg(if (request == null) "you don't have any pending teleport requests" else "that teleport request has timed out", ChatFormatting.RED.color)
            return 0
        }

        val requester = target.serverList?.getPlayer(request.requesterUuid)
        if (requester == null) {
            target.sendColorMsg("the player who requested the teleport is no longer online", ChatFormatting.RED.color)
            return 0
        }

        val isTpa = request.type == TpaType.TPA
        val (teleporter, destination) = if (isTpa) requester to target else target to requester

        teleporter.teleportTo(
            destination.serverWorld,
            destination.x,
            destination.y,
            destination.z,
            emptySet(),
            destination.yRot,
            destination.xRot,
            true
        )

        target.sendColorMsg("you accepted ${requester.displayName?.string}'s teleport ${if (isTpa) "request" else "here request"}", ChatFormatting.GREEN.color)
        requester.sendColorMsg("${requester.displayName?.string} accepted your request${if (isTpa) "!" else " and teleported to you!"}", ChatFormatting.GREEN.color)

        teleporter.connection.send(ClientboundSetTitleTextPacket(literalText("Teleported!") { color = ChatFormatting.GREEN.color }))

        return 1
    }

    private fun handleTpaDeny(target: ServerPlayer?): Int {
        if (target == null) return 0
        val request = pendingRequests.remove(target.uuid)

        if (request == null || request.isExpired) {
            target.sendColorMsg("you don't have any valid pending teleport requests", ChatFormatting.RED.color)
            return 0
        }

        target.serverList?.getPlayer(request.requesterUuid)?.sendColorMsg("${target.displayName?.string} denied your teleport request", ChatFormatting.RED.color)
        target.sendColorMsg("teleport request denied", ChatFormatting.RED.color)

        return 1
    }

    private fun handleTpaCancel(requester: ServerPlayer?): Int {
        if (requester == null) return 0

        val entry = pendingRequests.entries.find { it.value.requesterUuid == requester.uuid }
        if (entry != null) pendingRequests.remove(entry.key)

        if (entry == null || entry.value.isExpired) {
            requester.sendColorMsg("you don't have any active teleport requests to cancel", ChatFormatting.RED.color)
            return 0
        }

        requester.sendColorMsg("you cancelled your teleport request", ChatFormatting.AQUA.color)
        requester.serverList?.getPlayer(entry.key)?.sendColorMsg("${requester.displayName?.string} cancelled their teleport request", ChatFormatting.AQUA.color)

        return 1
    }
}