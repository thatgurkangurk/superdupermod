/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod

import me.gurkz.superdupermod.command.ServerSayCommand
import me.gurkz.superdupermod.command.SmiteCommand
import me.gurkz.superdupermod.command.SwingCommand
import me.gurkz.superdupermod.item.ModItems
import me.gurkz.superdupermod.network.RespawnPlayer
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.minecraft.resources.Identifier
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import org.slf4j.Logger
import org.slf4j.LoggerFactory


object SuperDuperMod : ModInitializer {
    const val MOD_ID: String = "superdupermod"
    private val loader: FabricLoader = FabricLoader.getInstance()
    private val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    val VERSION: String = loader.getModContainer(MOD_ID).map {
        container ->
            container.metadata.version.friendlyString
    }.orElse("unknown version")

    override fun onInitialize() {
        LOGGER.info("hi from super duper mod version $VERSION")
        registerSuperDuperModCommand()
        SmiteCommand.register()
        ServerSayCommand.register()
        SwingCommand.register()

        ModItems.initialise()

        RespawnPlayer.initServer()
    }

    private fun registerSuperDuperModCommand() {
        command("superdupermod") {
            runs {
                val text = literalText {
                    color = 0xF21347
                    text("superdupermod version ")
                    text(VERSION) {
                        color = 0x4BD6CB
                    }
                }
                source.sendSystemMessage(text)
            }
        }
    }

    fun id(path: String): Identifier {
        return Identifier.fromNamespaceAndPath(MOD_ID, path)
    }
}