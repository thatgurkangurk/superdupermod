/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.config

import me.gurkz.superdupermod.annotation.Comment

data class SilenceMobsConfig (
    @Comment("names to trigger silencing uwu")
    var validNames: List<String> = listOf("silence me"),
)

data class SuperDuperModConfig(
    var silenceMobs: SilenceMobsConfig = SilenceMobsConfig()
)
