/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.network.packet

import kotlinx.serialization.Serializable
import me.gurkz.superdupermod.SuperDuperMod
import net.silkmc.silk.network.packet.s2cPacket

@Serializable
data class RespawnPlayerPacket(val requester: String) {
    companion object {
        val S2C = s2cPacket<RespawnPlayerPacket>(SuperDuperMod.id("packet/respawn_player_s2c"))
    }
}
