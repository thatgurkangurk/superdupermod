/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.fzzyhmstrs.fzzy_config.util.FcText.description
import me.gurkz.superdupermod.command.SilenceMobsCommands
import me.gurkz.superdupermod.command.SmiteCommand
import me.gurkz.superdupermod.config.Configs
import me.gurkz.superdupermod.config.SuperDuperConfig
import me.gurkz.superdupermod.permission.KPermissions
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText
import net.silkmc.silk.core.text.sendText
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
        SilenceMobsCommands.registerCommands()
        SmiteCommand.register()
        Configs.superDuperConfig // reference it so it loads
    }

    private fun registerSuperDuperModCommand() {
        command("superdupermod") {
            description("shows info about super duper mod")

            literal("reload") {
                requires(KPermissions.require("superdupermod.command.reload", 4))
                runs {
                    Configs.superDuperConfig = ConfigApi.readOrCreateAndValidate(::SuperDuperConfig)
                    source.sendSystemMessage(literalText("reloaded config"))
                }
            }

            runs {
                val text = literalText {
                    color = 0xF21347
                    text("superdupermod version ")
                    text(VERSION) {
                        color = 0x4BD6CB
                    }
                }
                source.playerOrException.sendText(text)
            }
        }
    }
}