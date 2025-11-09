/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import com.mojang.brigadier.arguments.StringArgumentType
import me.gurkz.superdupermod.config.Configs
import me.gurkz.superdupermod.permission.KPermissions
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import kotlin.String

object SilenceMobsCommands {
    private fun config() = Configs.superDuperConfig

    fun addName(name: String): Boolean = with(config()) {
        silenceMobs.validNames.add(name).also { if (it) save() }
    }

    fun removeName(name: String): Boolean = with(config()) {
        silenceMobs.validNames.remove(name).also { if (it) save() }
    }

    fun addNameCommand() = command("superdupermod") {
        literal("silencemobs").literal("names").literal("add").requires(KPermissions.require(
            "superdupermod.command.silencemobs.names.add",
            4
        )).argument<String>("nameToAdd", StringArgumentType.greedyString()) { nameToAdd -> runs {
            addName(nameToAdd())

            source.sendSystemMessage(literalText("added ${nameToAdd()}"))
        } }
    }

    fun removeNameCommand() = command("superdupermod") {
        literal("silencemobs").literal("names").literal("remove").requires(KPermissions.require(
            "superdupermod.command.silencemobs.names.remove",
            4
        )).argument<String>("nameToRemove", StringArgumentType.greedyString()) { nameToRemove ->
            suggestList { config().silenceMobs.validNames }
            runs {
                if (removeName(nameToRemove())) {
                    source.sendSystemMessage(literalText("removed ${nameToRemove()}"))
                } else {
                    source.sendFailure(literalText("that name has not been added"))
                }
            }
        }
    }

    fun registerCommands() = command("superdupermod") {
        literal("silencemobs").literal("names") {
            literal("list") runs {
                val names = config().silenceMobs.validNames.joinToString(", ")

                source.sendSystemMessage(literalText("current names are: $names"))
            }
        }
        addNameCommand()

        removeNameCommand()
    }
}