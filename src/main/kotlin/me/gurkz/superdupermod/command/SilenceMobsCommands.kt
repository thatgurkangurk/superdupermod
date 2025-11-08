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
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText

object SilenceMobsCommands {
    fun registerCommands() {
        command("superdupermod") {
            literal("silencemobs") {
                literal("names") {
                    literal("list") runs {
                        val config = Configs.superDuperConfig.silenceMobs

                        val names = config.validNames.joinToString(", ")

                        source.sendSystemMessage(literalText("current names are: $names"))
                    }

                    literal("add") {
                        argument<String>("nameToAdd", StringArgumentType.greedyString()) { nameToAdd ->
                            runs {
                                val config = Configs.superDuperConfig.silenceMobs

                                config.validNames.add(nameToAdd())

                                Configs.superDuperConfig.save()

                                source.sendSystemMessage(literalText("added ${nameToAdd()}"))

                            }
                        }
                    }

                    literal("remove") {
                        argument<String>("nameToRemove", StringArgumentType.greedyString()) { nameToRemove ->
                            suggestList { Configs.superDuperConfig.silenceMobs.validNames }
                            runs {
                                val config = Configs.superDuperConfig.silenceMobs

                                if (!config.validNames.contains(nameToRemove())) {
                                    source.sendFailure(literalText("that name has not been added"))

                                } else {
                                    config.validNames.remove(nameToRemove())

                                    Configs.superDuperConfig.save()

                                    source.sendSystemMessage(literalText("removed ${nameToRemove()}"))
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}