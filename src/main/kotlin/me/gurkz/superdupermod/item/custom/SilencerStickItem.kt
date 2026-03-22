/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.item.custom

import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.Item
import net.minecraft.world.item.ItemStack

class SilencerStickItem(properties: Properties) : Item(properties) {
    private fun silenceEntity(
        stack: ItemStack,
        entity: LivingEntity,
        user: Player,
        usedHand: InteractionHand
    ): InteractionResult {
        val level = user.level()

        if (!level.isClientSide) {
            val serverLevel = level as ServerLevel
            entity.isSilent = true

            entity.addEffect(MobEffectInstance(MobEffects.GLOWING, 3 * 20, 0, false, false))

            val pos = entity.position()
            serverLevel.playSound(
                null,
                pos.x, pos.y, pos.z,
                SoundEvents.AMETHYST_BLOCK_RESONATE,
                SoundSource.AMBIENT,
                0.8f,
                2.0f
            )

            if (entity is Mob) {
                entity.setPersistenceRequired()
            }

            stack.hurtAndBreak(1, user, usedHand)

            return InteractionResult.SUCCESS_SERVER
        }

        return InteractionResult.SUCCESS
    }

    override fun interactLivingEntity(
        stack: ItemStack,
        player: Player,
        interactionTarget: LivingEntity,
        usedHand: InteractionHand
    ): InteractionResult {
        if (interactionTarget is Player) {
            return InteractionResult.PASS
        }

        return this.silenceEntity(stack, interactionTarget, player, usedHand)
    }
}