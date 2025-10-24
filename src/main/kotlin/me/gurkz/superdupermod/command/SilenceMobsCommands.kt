/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.command

import me.gurkz.superdupermod.SuperDuperMod.CONFIG_HANDLER
import revxrsal.commands.annotation.Command
import revxrsal.commands.annotation.Description
import revxrsal.commands.fabric.actor.FabricCommandActor
import revxrsal.commands.fabric.annotation.CommandPermission

@Target(AnnotationTarget.VALUE_PARAMETER)
annotation class SilenceMobName()

object SilenceMobsCommands {
    @Command("superdupermod silencemobs names list")
    @Description("lists names that trigger mob silencing")
    fun listNames(actor: FabricCommandActor) {
        val config = CONFIG_HANDLER.get()

        val names = config.silenceMobs.validNames.joinToString(", ")

        actor.reply("current names are: $names")
    }

    @Command("superdupermod silencemobs names add")
    @Description("adds a name that triggers mob silencing")
    @CommandPermission("superdupermod.command.silencemobs.names.add", vanilla = 4)
    fun addName(actor: FabricCommandActor, nameToAdd: String) {
        val config = CONFIG_HANDLER.get()

        config.silenceMobs.validNames += nameToAdd

        CONFIG_HANDLER.save()

        actor.reply("added $nameToAdd")
    }

    @Command("superdupermod silencemobs names remove")
    @Description("removes a name that triggers mob silencing")
    @CommandPermission("superdupermod.command.silencemobs.names.remove", vanilla = 4)
    fun removeName(actor: FabricCommandActor, @SilenceMobName nameToRemove: String) {
        val config = CONFIG_HANDLER.get()

        config.silenceMobs.validNames -= nameToRemove

        CONFIG_HANDLER.save()

        actor.reply("removed $nameToRemove")
    }
}