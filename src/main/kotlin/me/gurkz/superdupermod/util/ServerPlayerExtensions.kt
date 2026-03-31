package me.gurkz.superdupermod.util

import me.gurkz.superdupermod.mixin.ServerAccessorMixin
import net.minecraft.server.MinecraftServer
import net.minecraft.server.level.ServerPlayer

val ServerPlayer.server: MinecraftServer?
    get() = (this as ServerAccessorMixin).`superdupermod$getServer`()