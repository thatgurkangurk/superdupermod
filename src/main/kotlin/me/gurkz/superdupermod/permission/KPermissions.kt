/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.permission

import me.lucko.fabric.api.permissions.v0.Permissions
import net.minecraft.commands.CommandSourceStack
import java.util.Objects

object KPermissions {
    fun require(permission: String, defaultValue: Boolean): (CommandSourceStack) -> Boolean {
        Objects.requireNonNull(permission, "permission")
        return { player -> Permissions.check(player, permission, defaultValue) }
    }

    fun require(permission: String, defaultRequiredLevel: Int): (CommandSourceStack) -> Boolean {
        Objects.requireNonNull(permission, "permission")
        return { player -> Permissions.check(player, permission, defaultRequiredLevel) }
    }

    fun require(permission: String): (CommandSourceStack) -> Boolean {
        Objects.requireNonNull(permission, "permission")
        return { player -> Permissions.check(player, permission) }
    }
}
