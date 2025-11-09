/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.config

import me.fzzyhmstrs.fzzy_config.annotations.Comment
import me.fzzyhmstrs.fzzy_config.annotations.ConvertFrom
import me.fzzyhmstrs.fzzy_config.annotations.Version
import me.fzzyhmstrs.fzzy_config.api.FileType
import me.fzzyhmstrs.fzzy_config.config.Config
import me.fzzyhmstrs.fzzy_config.config.ConfigSection
import me.gurkz.superdupermod.SuperDuperMod.MOD_ID
import me.gurkz.superdupermod.util.Identifier

@Version(1)
@ConvertFrom("superdupermod.toml")
internal class SuperDuperConfig : Config(Identifier.of(MOD_ID, "super_duper_config")) {
    var silenceMobs = SilenceMobsConfig()

    internal class SilenceMobsConfig : ConfigSection() {
        @Comment("names to trigger silencing an entity. this is case-insensitive.")
        var validNames: MutableList<String> = mutableListOf("silence me")
    }

    // not needed yet
    //    override fun update(deserializedVersion: Int) {
    //        super.update(deserializedVersion)
    //    }

    override fun fileType(): FileType {
        return FileType.TOML
    }
}