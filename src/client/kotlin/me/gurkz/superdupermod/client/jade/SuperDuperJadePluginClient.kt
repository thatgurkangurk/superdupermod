/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.client.jade

import me.gurkz.superdupermod.client.jade.provider.PetCooldownClientProvider
import net.minecraft.world.entity.TamableAnimal
import snownee.jade.api.IWailaClientRegistration
import snownee.jade.api.IWailaPlugin
import snownee.jade.api.WailaPlugin

@WailaPlugin
class SuperDuperJadePluginClient : IWailaPlugin {
    override fun registerClient(registration: IWailaClientRegistration) {
        registration.registerEntityComponent(PetCooldownClientProvider, TamableAnimal::class.java)
    }
}