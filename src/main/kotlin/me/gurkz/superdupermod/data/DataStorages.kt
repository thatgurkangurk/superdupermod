/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.data

import eu.pb4.playerdata.api.PlayerDataApi
import eu.pb4.playerdata.api.storage.JsonDataStorage

object DataStorages {
    val DEATH_LOCATION = JsonDataStorage("death_location", DeathLocationData::class.java)

    fun register() {
        PlayerDataApi.register(DEATH_LOCATION)
    }
}