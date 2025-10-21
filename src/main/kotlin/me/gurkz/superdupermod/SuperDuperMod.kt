/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SuperDuperMod : ModInitializer {
    private const val MOD_ID: String = "superdupermod"
    private val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        LOGGER.info("hi from super duper mod")
    }
}