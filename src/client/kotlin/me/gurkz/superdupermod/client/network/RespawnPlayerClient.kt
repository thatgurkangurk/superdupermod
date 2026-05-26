/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.client.network

import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.packet.RespawnPlayerPacket

object RespawnPlayerClient {
    fun initClient() {
        SuperDuperMod.NET_CHANNEL.registerClientbound(RespawnPlayerPacket::class.java) { _, access ->
            val player = access.player()

            if (player.isDeadOrDying) {
                player.respawn()
            }
        }
    }
}