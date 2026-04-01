/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.video

import me.gurkz.superdupermod.video.type.Frame
import me.gurkz.superdupermod.video.type.RegisteredVideo
import net.minecraft.core.Holder
import net.minecraft.network.chat.ClickEvent
import net.minecraft.network.chat.Component
import net.minecraft.network.protocol.game.ClientboundStopSoundPacket
import net.minecraft.resources.Identifier
import net.minecraft.server.dialog.ActionButton
import net.minecraft.server.dialog.CommonButtonData
import net.minecraft.server.dialog.CommonDialogData
import net.minecraft.server.dialog.DialogAction
import net.minecraft.server.dialog.NoticeDialog
import net.minecraft.server.dialog.action.StaticAction
import net.minecraft.server.dialog.body.DialogBody
import net.minecraft.server.dialog.body.PlainMessage
import net.minecraft.server.level.ServerPlayer
import net.minecraft.sounds.SoundEvent
import net.minecraft.sounds.SoundSource
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.logging.logger
import java.util.Optional
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

object VideoPlayer {
    private const val LINES_PER_PAGE = 50
    private val activePlaybacks = ConcurrentHashMap<UUID, PlaybackState>()

    // supports up to 256 width
    private val BLOCK_STRINGS = Array(256) { "■".repeat(it) }

    private val NEWLINE = Component.literal("\n")

    class PlaybackState(
        val player: ServerPlayer,
        val registeredVideo: RegisteredVideo,
        val currentFrameLines: MutableList<Component>,
        var frameIndex: Int = 0,
        var ticksInCurrentFrame: Int = 0
    )

    fun tick() {
        val iterator = activePlaybacks.entries.iterator()

        while (iterator.hasNext()) {
            val entry = iterator.next()
            val state = entry.value
            val player = state.player
            val video = state.registeredVideo.video

            if (player.isRemoved || player.hasDisconnected() || state.frameIndex >= video.frames.size) {
                iterator.remove()
                continue
            }

            val frame = video.frames[state.frameIndex]

            if (state.ticksInCurrentFrame == 0) {
                applyFrameInPlace(state.currentFrameLines, frame)
                sendFrame(player, state.currentFrameLines)
            }

            state.ticksInCurrentFrame++

            if (state.ticksInCurrentFrame >= frame.duration) {
                state.frameIndex++
                state.ticksInCurrentFrame = 0
            }
        }
    }

    fun start(player: ServerPlayer, videoId: Identifier): Boolean {
        val registeredVideo = VideoDialogueRegistry.videos[videoId] ?: return false

        val startFrame = MutableList<Component>(registeredVideo.video.height) { Component.empty() }
        activePlaybacks[player.uuid] = PlaybackState(player, registeredVideo,startFrame)

        registeredVideo.sound?.let { soundId ->
            logger().info("sound id with $soundId was found")
            val soundEvent = SoundEvent.createVariableRangeEvent(soundId)
            logger().info("sound event ${soundEvent.location}")

            // start sound on the RECORDS source
            player.connection.send(
                net.minecraft.network.protocol.game.ClientboundSoundPacket(
                    Holder.direct(soundEvent),
                    SoundSource.RECORDS,
                    player.pos.x,
                    player.pos.y,
                    player.pos.z,
                    1.0f,
                    1.0f,
                    player.random.nextLong()
                )
            )
        }

        return true
    }

    fun stop(player: ServerPlayer) {
        val state = activePlaybacks.remove(player.uuid)

        state?.registeredVideo?.sound?.let { soundId ->
            player.connection.send(ClientboundStopSoundPacket(soundId, SoundSource.RECORDS))
        }

        // force the client to close the dialogue instantly to not send a frozen frame
        player.closeContainer()
    }

    private fun applyFrameInPlace(lines: MutableList<Component>, frame: Frame) {
        frame.diffs.forEach { diff ->
            val actualIndex = diff.line

            while (lines.size <= actualIndex) {
                lines.add(Component.empty())
            }

            val lineComponent = Component.empty()

            if (diff.text.isNotEmpty()) {
                val text = diff.text
                val len = text.length
                var ptr = 0

                while (ptr < len) {
                    var runLength = 0
                    while (ptr < len && text[ptr] != ':') {
                        runLength = runLength * 10 + (text[ptr] - '0')
                        ptr++
                    }
                    ptr++

                    var colorHex = 0
                    while (ptr < len && text[ptr] != ',') {
                        val c = text[ptr]
                        val digit = when (c) {
                            in '0'..'9' -> c - '0'
                            in 'a'..'f' -> c - 'a' + 10
                            in 'A'..'F' -> c - 'A' + 10
                            else -> 0
                        }
                        colorHex = (colorHex shl 4) or digit
                        ptr++
                    }
                    ptr++

                    val blockStr = if (runLength in BLOCK_STRINGS.indices) {
                        BLOCK_STRINGS[runLength]
                    } else {
                        "■".repeat(runLength)
                    }

                    lineComponent.append(Component.literal(blockStr).withStyle { it.withColor(colorHex) })
                }
            }
            lines[actualIndex] = lineComponent
        }
    }

    private fun sendFrame(player: ServerPlayer, lines: List<Component>) {
        try {
            val dialogueBodies = mutableListOf<DialogBody>()
            var currentChunk = Component.empty()

            for (i in lines.indices) {
                currentChunk.append(lines[i])

                if (i % LINES_PER_PAGE == LINES_PER_PAGE - 1 || i == lines.lastIndex) {
                    dialogueBodies.add(PlainMessage(currentChunk, 1024))
                    currentChunk = Component.empty()
                } else {
                    currentChunk.append(NEWLINE)
                }
            }

            val dialogueData = CommonDialogData(
                Component.empty(),
                Optional.empty(),
                true,
                false,
                DialogAction.CLOSE,
                dialogueBodies,
                emptyList()
            )

            val action = StaticAction(ClickEvent.RunCommand("/videodialogue stop"))
            val actionButtonData = CommonButtonData(Component.literal("Close"), Optional.empty(), CommonButtonData.DEFAULT_WIDTH)
            val actionButton = ActionButton(actionButtonData, Optional.of(action))
            val dialogue = NoticeDialog(dialogueData, actionButton)

            player.openDialog(Holder.direct(dialogue))
        } catch (e: Exception) {
            e.printStackTrace()
            stop(player)
        }
    }
}