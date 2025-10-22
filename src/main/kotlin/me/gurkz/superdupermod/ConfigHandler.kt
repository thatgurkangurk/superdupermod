/*
 * Copyright 2025 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod

import com.electronwill.nightconfig.core.CommentedConfig
import com.electronwill.nightconfig.core.file.CommentedFileConfig
import java.io.File
import kotlin.reflect.KClass
import kotlin.reflect.KProperty1
import kotlin.reflect.full.findAnnotation
import kotlin.reflect.full.memberProperties
import kotlin.reflect.full.primaryConstructor
import kotlin.reflect.jvm.jvmErasure
import me.gurkz.superdupermod.annotation.Comment

/**
 * wrapper for night-config that uses kotlin dataclasses
 *
 * documentation for this might be written later, and it might be extracted to its own library at some point
 */
class ConfigHandler<T : Any>(
    private val file: File,
    private val clazz: KClass<T>
) {
    private var configInstance: T

    init {
        file.parentFile.mkdirs()
        configInstance = load()
        save() // ensure defaults and comments are written
    }

    fun get(): T = configInstance

    fun reload() {
        configInstance = load()
    }

    fun save() {
        val fileConfig = CommentedFileConfig.builder(file).autosave().build()
        fileConfig.load()

        // Clear any keys that don't exist in the data class
        cleanConfig(clazz, fileConfig)

        // Write current instance values
        writeConfig(clazz, configInstance, fileConfig)

        fileConfig.save()
    }

    private fun writeConfig(kClass: KClass<*>, instance: Any, config: CommentedConfig, path: String = "") {
        for (prop in kClass.memberProperties) {
            val key = if (path.isEmpty()) prop.name else "$path.${prop.name}"
            val value = (prop as KProperty1<Any, Any?>).get(instance)

            prop.findAnnotation<Comment>()?.let { config.setComment(key, it.comment) }

            if (value != null && value::class.isData) {
                writeConfig(value::class, value, config, key)
            } else {
                config.set<Any?>(key, value)
            }
        }
    }

    private fun cleanConfig(kClass: KClass<*>, config: CommentedConfig, path: String = "") {
        // Collect valid keys for this class
        val validKeys = kClass.memberProperties.map { if (path.isEmpty()) it.name else "$path.${it.name}" }.toSet()

        // Remove keys that exist in the config but are no longer in the class
        val keysToRemove = config.valueMap().keys.filter { it.startsWith(path) && it !in validKeys }
        keysToRemove.forEach { config.remove(it) }

        // Recursively clean nested data classes
        for (prop in kClass.memberProperties) {
            val key = if (path.isEmpty()) prop.name else "$path.${prop.name}"
            if (prop.returnType.jvmErasure.isData) {
                cleanConfig(prop.returnType.jvmErasure, config, key)
            }
        }
    }

    private fun load(): T {
        val fileConfig = CommentedFileConfig.builder(file).build()
        fileConfig.load()
        return populate(clazz, fileConfig) as T
    }

    private fun populate(kClass: KClass<*>, config: CommentedConfig, path: String = ""): Any {
        val constructor = kClass.primaryConstructor!!

        val args = constructor.parameters.associateWith { param ->
            val key = if (path.isEmpty()) param.name!! else "$path.${param.name}"
            val propClass = param.type.jvmErasure

            if (propClass.isData) {
                populate(propClass, config, key)
            } else {
                config.getOrElse(key) {
                    val defaultValue = constructor.callBy(emptyMap())
                    val prop = kClass.memberProperties.first { it.name == param.name } as KProperty1<Any, Any?>
                    val value = prop.get(defaultValue)
                    config.set<Any?>(key, value)
                    value
                }
            }
        }

        return constructor.callBy(args)
    }
}
