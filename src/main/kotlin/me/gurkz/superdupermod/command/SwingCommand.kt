/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import me.gurkz.superdupermod.permission.KPermissions
import net.minecraft.ChatFormatting
import net.minecraft.commands.CommandSourceStack
import net.minecraft.commands.arguments.EntityArgument
import net.minecraft.world.InteractionHand
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.silkmc.silk.commands.command
import net.silkmc.silk.core.text.literalText

/**
 * backport of minecraft 26.1 `/swing` command
 *
 * will be removed from superdupermod in 26.1
 */
object SwingCommand {
    private fun swing(source: CommandSourceStack, targets: Collection<Entity>, hand: InteractionHand) {
        var livingEntitiesCount = 0

        for (entity in targets) {
            if (entity is LivingEntity) {
                entity.swing(hand, true)
                livingEntitiesCount++
            }
        }

        if (livingEntitiesCount < 1) {
            val text = literalText {
                color = ChatFormatting.RED.color
                text("No entity was found")
            }

            source.sendFailure(text)
            return
        }

        if (livingEntitiesCount == 1) {
            val text = literalText("Made ") {
                text(targets.iterator().next().displayName!!)
                text(" swing an arm")
            }

            source.sendSuccess({ text }, true)
            return
        } else {
            val text = literalText("Made ${targets.size} entities swing their arms")

            source.sendSuccess({ text }, true)
            return
        }
    }

    fun register() {
        command("swing") {
            requires(KPermissions.require("superdupermod.command.swing", 4))
            argument("targets", EntityArgument.entities()) {

                literal("mainhand") runs {
                    val targets = EntityArgument.getEntities(this, "targets")

                    swing(this.source, targets, InteractionHand.MAIN_HAND)
                }

                literal("offhand") runs {
                    val targets = EntityArgument.getEntities(this, "targets")

                    swing(this.source, targets, InteractionHand.OFF_HAND)
                }

                runs {
                    val targets = EntityArgument.getEntities(this, "targets")

                    swing(this.source, targets, InteractionHand.MAIN_HAND)
                }
            }

            runs {
                val target = this.source.player

                if (target !== null) {
                    swing(this.source, listOf(target), InteractionHand.MAIN_HAND)
                }
            }
        }
    }
}