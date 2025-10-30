/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod

import me.fzzyhmstrs.fzzy_config.api.ConfigApi
import me.gurkz.superdupermod.SuperDuperMod.VERSION
import me.gurkz.superdupermod.command.SilenceMobName
import me.gurkz.superdupermod.command.SilenceMobsCommands
import me.gurkz.superdupermod.config.Configs
import me.gurkz.superdupermod.config.SuperDuperConfig
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.autocomplete.SuggestionProvider
import revxrsal.commands.fabric.FabricLamp
import revxrsal.commands.fabric.actor.FabricCommandActor
import revxrsal.commands.fabric.annotation.CommandPermission

// i expect this to be in its own file later
class SuperDuperModCommand {
    @Command("superdupermod")
    @Description("shows info about super duper mod")
    fun superDuperMod(actor: FabricCommandActor) {
        actor.reply("hello from super duper mod version ${VERSION}!")
    }

    @Command("superdupermod reload")
    @Description("reload superdupermod config")
    @CommandPermission("superdupermod.command.reload", vanilla = 4)
    fun reloadCommand(actor: FabricCommandActor) {
        Configs.superDuperConfig = ConfigApi.readOrCreateAndValidate(::SuperDuperConfig)
        actor.reply("reloaded config")
    }
}

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
        Configs.superDuperConfig // reference it so it loads
        val lamp = FabricLamp.builder()
            .suggestionProviders { providers ->
                providers.addProviderForAnnotation(SilenceMobName::class.java) { _ ->
                    SuggestionProvider { _ ->
                        Configs.superDuperConfig.silenceMobs.validNames.toList()
                    }
                }
            }
            .build()

        lamp.register(SuperDuperModCommand())
        lamp.register(SilenceMobsCommands)
    }
}