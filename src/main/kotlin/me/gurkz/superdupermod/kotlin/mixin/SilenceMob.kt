/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.kotlin.mixin

import me.gurkz.superdupermod.SuperDuperMod.CONFIG_HANDLER
import net.minecraft.component.DataComponentTypes
import net.minecraft.entity.LivingEntity
import net.minecraft.entity.effect.StatusEffectInstance
import net.minecraft.entity.effect.StatusEffects
import net.minecraft.entity.mob.MobEntity
import net.minecraft.entity.player.PlayerEntity
import net.minecraft.item.ItemStack
import net.minecraft.server.world.ServerWorld
import net.minecraft.sound.SoundCategory
import net.minecraft.sound.SoundEvents
import net.minecraft.text.Text
import net.minecraft.util.ActionResult
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable



object SilenceMob {
    @JvmStatic
    fun silenceEntity(stack: ItemStack, user: PlayerEntity, entity: LivingEntity, cir: CallbackInfoReturnable<ActionResult>) {
        val text = stack.get(DataComponentTypes.CUSTOM_NAME);
        if (text == null || entity is PlayerEntity || user.entityWorld.isClient || !entity.isAlive) {
            return
        }

        val config = CONFIG_HANDLER.get()

        if (!config.silenceMobs.validNames.contains(text.string.lowercase())) {
            return
        }

        entity.isSilent = true
        entity.customName = Text.literal("silenced")
        entity.addStatusEffect(StatusEffectInstance(StatusEffects.GLOWING, 3 * 20, 0, false, false))

        val world = user.entityWorld as? ServerWorld ?: return
        val position = entity.entityPos
        world.playSound(
            null,
            position.x, position.y, position.z,
            SoundEvents.BLOCK_AMETHYST_BLOCK_RESONATE,
            SoundCategory.AMBIENT,
            0.8f,
            2.0f
        )

        (entity as? MobEntity)?.setPersistent()

        stack.decrement(1)
        cir.returnValue = ActionResult.SUCCESS
    }
}