/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.client.network

import me.gurkz.superdupermod.network.packet.RespawnPlayerPacket

object RespawnPlayerClient {
    fun initClient() {
        RespawnPlayerPacket.S2C.receiveOnClient { _, context ->
            val player = context.client.player

            if (player?.isDeadOrDying == true) {
                player.respawn()
            }
        }
    }
}