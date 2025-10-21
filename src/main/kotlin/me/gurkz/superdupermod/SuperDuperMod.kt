/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod

import net.fabricmc.api.ModInitializer
import net.fabricmc.loader.api.FabricLoader
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SuperDuperMod : ModInitializer {
    private const val MOD_ID: String = "superdupermod"
    private val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)
    val VERSION: String = FabricLoader.getInstance().getModContainer(MOD_ID).map { container -> container.metadata.version.friendlyString }.orElse("unknown version")

    override fun onInitialize() {
        LOGGER.info("hi from super duper mod version $VERSION")
    }
}