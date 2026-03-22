/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.client.network

import me.gurkz.superdupermod.network.RespawnPlayer
import net.minecraft.client.Minecraft

object RespawnPlayerClient {
    fun initClient() {
        RespawnPlayer.respawnPlayerPacketS2C.receiveOnClient { _, _ ->
            val mcClient = Minecraft.getInstance()

            val player = mcClient.player ?: return@receiveOnClient

            if (player.isDeadOrDying) {
                player.respawn()
            }
        }
    }
}