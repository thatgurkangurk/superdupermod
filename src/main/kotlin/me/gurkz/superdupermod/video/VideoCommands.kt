/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.video

import com.mojang.brigadier.context.CommandContext
import net.fabricmc.fabric.api.command.v2.CommandRegistrationCallback
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.Commands
import net.minecraft.commands.arguments.IdentifierArgument
import net.minecraft.network.chat.Component

object VideoCommands {
    fun register() {
        CommandRegistrationCallback.EVENT.register { dispatcher, _, _ ->
            dispatcher.register(
                Commands.literal("videodialogue")
                    .then(Commands.literal("start")
                        .then(Commands.argument("video_id", IdentifierArgument.id())
                            .suggests { _, builder ->
                                VideoDialogueRegistry.videos.keys.forEach { id ->
                                    builder.suggest(id.toString())
                                }
                                builder.buildFuture()
                            }
                            .executes { context -> executeStart(context) }
                        )
                    )

                    .then(Commands.literal("stop")
                        .executes { context -> executeStop(context) }
                    )
            )
        }
    }

    private fun executeStart(context: CommandContext<CommandSourceStack>): Int {
        val source = context.source
        val player = source.playerOrException

        val videoId = IdentifierArgument.getId(context, "video_id")

        if (VideoPlayer.start(player, videoId)) {
            source.sendSuccess({ Component.literal("Now playing: $videoId") }, false)
            return 1
        } else {
            source.sendFailure(Component.literal("Video not found in registry: $videoId"))
            return 0
        }
    }

    private fun executeStop(context: CommandContext<CommandSourceStack>): Int {
        val player = context.source.playerOrException

        VideoPlayer.stop(player)

        return 1
    }
}