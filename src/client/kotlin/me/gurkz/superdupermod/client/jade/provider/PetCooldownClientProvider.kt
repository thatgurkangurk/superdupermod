/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.client.jade.provider

import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.jade.provider.PetCooldownServerProvider
import net.minecraft.ChatFormatting
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.TamableAnimal
import snownee.jade.api.EntityAccessor
import snownee.jade.api.IEntityComponentProvider
import snownee.jade.api.ITooltip
import snownee.jade.api.config.IPluginConfig

object PetCooldownClientProvider : IEntityComponentProvider {
    override fun getUid(): Identifier = SuperDuperMod.id("pet_cooldown")

    override fun appendTooltip(tooltip: ITooltip, accessor: EntityAccessor, config: IPluginConfig) {
        val entity = accessor.entity as? TamableAnimal ?: return

        if (!entity.isTame) return

        val data = PetCooldownServerProvider.decodeFromData(accessor)
        if (data.isPresent) {
            val currentTime = System.currentTimeMillis()

            if (currentTime < data.get()) {
                val secondsLeft = (data.get() - currentTime) / 1000
                tooltip.add(Component.literal("Next pet in ${secondsLeft}s").withStyle(ChatFormatting.GRAY))
            } else {
                tooltip.add(Component.literal("Ready to pet! \u2764").withStyle(ChatFormatting.RED))
            }
        } else {
            tooltip.add(Component.literal("Ready to pet! \u2764").withStyle(ChatFormatting.RED))
        }
    }
}