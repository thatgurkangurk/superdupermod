/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.jade.provider

import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.data.DataAttachments
import net.minecraft.network.RegistryFriendlyByteBuf
import net.minecraft.network.codec.ByteBufCodecs
import net.minecraft.network.codec.StreamCodec
import net.minecraft.resources.Identifier
import snownee.jade.api.EntityAccessor
import snownee.jade.api.StreamServerDataProvider

object PetCooldownServerProvider : StreamServerDataProvider<EntityAccessor, Long> {
    override fun getUid(): Identifier = SuperDuperMod.id("pet_cooldown")

    override fun streamData(accessor: EntityAccessor): Long? {
        val entity = accessor.entity
        val nextPetTime = entity.getAttached(DataAttachments.NEXT_PET_TIME) ?: 0L

        if (nextPetTime > 0L) {
            return nextPetTime
        }

        return null
    }

    override fun streamCodec(): StreamCodec<RegistryFriendlyByteBuf, Long> {
        return ByteBufCodecs.LONG.cast()
    }
}