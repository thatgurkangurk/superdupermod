package me.gurkz.superdupermod.network

import kotlinx.serialization.Serializable
import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.network.chat.Component
import net.silkmc.silk.commands.command
import net.silkmc.silk.network.packet.s2cPacket

object RespawnPlayer {

    @Serializable
    data class RespawnPlayerPacket(
        val requester: String
    )

    val respawnPlayerPacketS2C = s2cPacket<RespawnPlayerPacket>(
        SuperDuperMod.id("respawn_player_packet_s2c"),
    )

    fun initServer() {
        command("superdupermod") {
            literal("respawn") {
                requires(KPermissions.require("superdupermod.command.respawn", 4))
                argument("target", EntityArgument.player()) { player ->
                    runs {
                        val target = player().findSinglePlayer(source)

                        val requester = source.textName

                        respawnPlayerPacketS2C.send(RespawnPlayerPacket(requester), target)

                        source.sendSystemMessage(Component.literal("respawning ${target.name.string}"))
                    }
                }
            }
        }
    }
}