/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.event

import eu.pb4.playerdata.api.PlayerDataApi
import me.gurkz.superdupermod.data.DataStorages
import me.gurkz.superdupermod.data.Location
import me.gurkz.superdupermod.data.OfflineTpData
import net.fabricmc.fabric.api.entity.event.v1.ServerPlayerEvents
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.entity.serverWorld
import net.silkmc.silk.core.logging.logger

object OfflineTpEventListeners {
    fun register() {
        ServerPlayerEvents.LEAVE.register { player ->
            val level = player.serverWorld
            val server = level.server

            if (server == null) {
                logger().warn("no server")
                return@register
            }

            logger().debug("saving data from offline tp")

            val offlineTpData = OfflineTpData(
                Location(
                    player.pos,
                    player.camera.headLookAngle,
                    level.dimension().identifier()
                )
            )

            PlayerDataApi.setCustomDataFor(player, DataStorages.OFFLINE_TP, offlineTpData)
        }
    }
}