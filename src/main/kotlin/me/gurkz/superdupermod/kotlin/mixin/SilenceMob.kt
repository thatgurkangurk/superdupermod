/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.kotlin.mixin

import me.gurkz.superdupermod.config.Configs
import net.minecraft.core.component.DataComponents
import net.minecraft.network.chat.Component
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundEvents
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionResult
import net.minecraft.world.effect.MobEffectInstance
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.entity.Mob
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable

object SilenceMob {
    @JvmStatic
    fun silenceEntity(stack: ItemStack, user: Player, entity: LivingEntity, cir: CallbackInfoReturnable<InteractionResult>) {
        val text = stack.get(DataComponents.CUSTOM_NAME)
        if (text == null || entity is Player || user.level().isClientSide || !entity.isAlive) {
            return
        }

        val config = Configs.superDuperConfig.silenceMobs

        if (!config.validNames.contains(text.string.lowercase())) {
            return
        }

        entity.isSilent = true
        entity.customName = Component.literal("silenced")
        entity.addEffect(MobEffectInstance(MobEffects.GLOWING, 3 * 20, 0, false, false))

        val world = user.level() as? ServerLevel ?: return
        val position = entity.position()
        world.playSound(
            null,
            position.x, position.y, position.z,
            SoundEvents.AMETHYST_BLOCK_RESONATE,
            SoundSource.AMBIENT,
            0.8f,
            2.0f
        )

        (entity as? Mob)?.setPersistenceRequired()

        stack.shrink(1)
        cir.returnValue = InteractionResult.SUCCESS
    }
}