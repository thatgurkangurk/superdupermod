/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.data

import com.mojang.serialization.Codec
import me.gurkz.superdupermod.SuperDuperMod
import net.fabricmc.fabric.api.attachment.v1.AttachmentRegistry
import net.fabricmc.fabric.api.attachment.v1.AttachmentSyncPredicate
import net.fabricmc.fabric.api.attachment.v1.AttachmentType
import net.minecraft.network.codec.ByteBufCodecs

object DataAttachments {
    val NEXT_PET_TIME: AttachmentType<Long> = AttachmentRegistry.create(SuperDuperMod.id("next_pet_time")) { builder ->
        builder.initializer { 0L }.persistent(Codec.LONG).syncWith(ByteBufCodecs.LONG, AttachmentSyncPredicate.all())
    }
}