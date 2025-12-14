/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.util

import net.minecraft.resources.Identifier

@Deprecated("use net.minecraft.resources.Identifier instead")
object Identifier {
    fun of(namespace: String, path: String): Identifier = Identifier.fromNamespaceAndPath(namespace, path)
}