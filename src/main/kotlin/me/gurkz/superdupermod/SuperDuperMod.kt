/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod

import me.gurkz.superdupermod.SuperDuperMod.VERSION
import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.fabric.FabricLamp
import revxrsal.commands.fabric.actor.FabricCommandActor

// i expect this to be in its own file later
class SuperDuperModCommand {
    @Command("superdupermod")
    @Description("shows info about super duper mod")
    fun superDuperMod(actor: FabricCommandActor) {
        actor.reply("hello from super duper mod version ${VERSION}!")
    }
}

object SuperDuperMod : ModInitializer {
    private const val MOD_ID: String = "superdupermod"
    private val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    val VERSION: String = FabricLoader.getInstance().getModContainer(MOD_ID).map { container -> container.metadata.version.friendlyString }.orElse("unknown version")

    override fun onInitialize() {
        LOGGER.info("hi from super duper mod version $VERSION")
        val lamp = FabricLamp.builder().build()
        lamp.register(SuperDuperModCommand())
    }
}