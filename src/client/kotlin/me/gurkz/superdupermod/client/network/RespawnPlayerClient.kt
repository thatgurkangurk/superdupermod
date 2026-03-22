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