/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.permission.KPermissions
import me.gurkz.superdupermod.util.FireworkUtil
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.damagesource.DamageSource
import net.minecraft.world.damagesource.DamageType
import net.minecraft.world.level.GameType
import net.silkmc.silk.commands.PermissionLevel
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.entity.pos
import net.silkmc.silk.core.text.literalText

object SuicideCommand {
    val suicideDamageType: ResourceKey<DamageType> = ResourceKey.create(Registries.DAMAGE_TYPE, SuperDuperMod.id("suicide"))

    fun register() {
        command("suicide") {
            requires(KPermissions.require("superdupermod.command.suicide", PermissionLevel.NONE.level))

            runs {
                val player = source.player

                if (player == null) {
                    source.sendFailure(literalText("only players can run /suicide"))
                    return@runs
                }

                if (player.gameMode() === GameType.CREATIVE) {
                    source.sendFailure(literalText("only players in survival can run /suicide"))
                    return@runs
                }

                val damageSource = DamageSource(
                    player.level().registryAccess()
                        .lookupOrThrow(Registries.DAMAGE_TYPE)
                        .get(suicideDamageType.identifier()).get()
                )

                val pos = player.pos

                FireworkUtil.summonFirework(
                    pos,
                    source.level,
                    FireworkUtil.createColour(0, 255, 0),
                    FireworkUtil.createColour(255, 0, 0),
                    2
                )

                player.hurtServer(source.level, damageSource, 20.0f)
            }
        }
    }
}