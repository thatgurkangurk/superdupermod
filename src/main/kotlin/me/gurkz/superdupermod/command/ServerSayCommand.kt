/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import com.mojang.brigadier.arguments.StringArgumentType
import me.gurkz.superdupermod.permission.KPermissions
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.broadcastText
import net.silkmc.silk.core.text.literalText

object ServerSayCommand {
    fun register() {
        command("superdupermod") {
            literal("server-say") {
                requires(KPermissions.require("superdupermod.command.server-say", 4))

                argument("message", StringArgumentType.greedyString()) runs {
                    val message = StringArgumentType.getString(this, "message")

                    val text = literalText {
                        text("[Server] ")
                        text(message)
                    }

                    source.server.broadcastText(text)
                }
            }
        }
    }
}