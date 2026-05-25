/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.config;

import blue.endless.jankson.Jankson;
import io.wispforest.owo.config.ConfigWrapper;
import io.wispforest.owo.config.ConfigWrapper.BuilderConsumer;
import io.wispforest.owo.config.Option;
import io.wispforest.owo.util.Observable;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

public class SuperDuperConfig extends ConfigWrapper<me.gurkz.superdupermod.config.SuperDuperConfigModel> {

    public final Keys keys = new Keys();

    private final Option<java.lang.Long> petPettingCooldown = this.optionForKey(this.keys.petPettingCooldown);

    private SuperDuperConfig() {
        super(me.gurkz.superdupermod.config.SuperDuperConfigModel.class);
    }

    private SuperDuperConfig(BuilderConsumer consumer) {
        super(me.gurkz.superdupermod.config.SuperDuperConfigModel.class, consumer);
    }

    public static SuperDuperConfig createAndLoad() {
        var wrapper = new SuperDuperConfig();
        wrapper.load();
        return wrapper;
    }

    public static SuperDuperConfig createAndLoad(BuilderConsumer consumer) {
        var wrapper = new SuperDuperConfig(consumer);
        wrapper.load();
        return wrapper;
    }

    public long petPettingCooldown() {
        return petPettingCooldown.value();
    }

    public void petPettingCooldown(long value) {
        petPettingCooldown.set(value);
    }


    public static class Keys {
        public final Option.Key petPettingCooldown = new Option.Key("petPettingCooldown");
    }
}

