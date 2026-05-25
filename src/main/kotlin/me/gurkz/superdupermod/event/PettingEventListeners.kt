/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.event

import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.data.DataAttachments.NEXT_PET_TIME
import net.fabricmc.fabric.api.event.player.UseEntityCallback
import net.minecraft.core.particles.ParticleTypes
import net.minecraft.core.registries.Registries
import net.minecraft.server.level.ServerLevel
import net.minecraft.sounds.SoundSource
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.OwnableEntity
import net.minecraft.world.entity.TamableAnimal
import net.minecraft.world.entity.animal.feline.Cat
import net.minecraft.world.entity.animal.feline.CatSoundVariants
import net.minecraft.world.entity.animal.wolf.Wolf
import net.minecraft.world.entity.animal.wolf.WolfSoundVariants
import java.util.concurrent.TimeUnit

object PettingEventListeners {
    private fun handlePetPetting(entity: TamableAnimal, level: ServerLevel): InteractionResult {
        val currentTime = System.currentTimeMillis()
        val nextPetTime = entity.getAttached(NEXT_PET_TIME) ?: 0L

        if (currentTime >= nextPetTime) {
            entity.setAttached(NEXT_PET_TIME, currentTime + TimeUnit.SECONDS.toMillis(SuperDuperMod.CONFIG.petPettingCooldown()))

            level.sendParticles(
                ParticleTypes.HEART,
                entity.x, entity.y + 0.5, entity.z,
                3, 0.3, 0.3, 0.3, 0.0
            )

            val sound = if (entity is Wolf) {
                val registry = level.registryAccess().lookupOrThrow(Registries.WOLF_SOUND_VARIANT)
                val classicVariant = registry.getOrThrow(WolfSoundVariants.CLASSIC)

                classicVariant.value().adultSounds.whineSound
            } else {
                val registry = level.registryAccess().lookupOrThrow(Registries.CAT_SOUND_VARIANT)
                val classicVariant = registry.getOrThrow(CatSoundVariants.CLASSIC)

                classicVariant.value().adultSounds.purreowSound
            }
            level.playSound(null, entity.blockPosition(), sound.value(), SoundSource.NEUTRAL, 1.0f, 1.0f)

            return InteractionResult.SUCCESS
        }

        return InteractionResult.PASS
    }

    fun register() {
        UseEntityCallback.EVENT.register { player, level, hand, entity, _ ->
            if (hand != InteractionHand.MAIN_HAND || !player.isCrouching) {
                return@register InteractionResult.PASS
            }

            if (entity is OwnableEntity && (entity is Wolf || entity is Cat) && entity.isTame) {
                if (level.isClientSide) {
                    return@register InteractionResult.SUCCESS
                }

                return@register handlePetPetting(entity, level as ServerLevel)
            }

            InteractionResult.PASS
        }
    }
}