/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.config

import net.neoforged.neoforge.common.ModConfigSpec
import org.apache.commons.lang3.tuple.Pair

object SuperDuperConfig {
    class Server(builder: ModConfigSpec.Builder) {
        val petPettingCooldown: ModConfigSpec.LongValue

        init {
            builder.comment("Server config settings").push("server")

            petPettingCooldown = builder
                .comment("Cooldown between petting pets (in seconds)")
                .defineInRange("petPettingCooldown", 5L, 0L, Long.MAX_VALUE)

            builder.pop()
        }
    }

    val serverSpec: ModConfigSpec
    val SERVER: Server

    init {
        val serverSpecPair: Pair<Server, ModConfigSpec> = ModConfigSpec.Builder().configure(::Server)
        SERVER = serverSpecPair.left
        serverSpec = serverSpecPair.right
    }
}