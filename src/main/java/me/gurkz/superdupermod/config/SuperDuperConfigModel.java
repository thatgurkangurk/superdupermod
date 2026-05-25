/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.config;

import io.wispforest.owo.config.annotation.Config;
import io.wispforest.owo.config.annotation.Modmenu;

@Config(name = "super-duper-config", wrapperName = "SuperDuperConfig")
@Modmenu(modId = "superdupermod")
public class SuperDuperConfigModel {
    public long petPettingCooldown = 30L;
}
