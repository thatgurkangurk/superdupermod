/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.video

import com.google.gson.Gson
import me.gurkz.superdupermod.video.type.DialogueRegistryFile
import me.gurkz.superdupermod.video.type.RegisteredVideo
import me.gurkz.superdupermod.video.type.Video
import net.minecraft.resources.Identifier
import net.minecraft.server.packs.resources.ResourceManager
import net.minecraft.server.packs.resources.ResourceManagerReloadListener
import net.silkmc.silk.core.logging.logger
import kotlin.collections.component1
import kotlin.collections.component2
import kotlin.collections.iterator
import kotlin.jvm.optionals.getOrNull

object VideoDialogueRegistry : ResourceManagerReloadListener {
    val videos = mutableMapOf<Identifier, RegisteredVideo>()
    private val logger = logger()
    private val gson = Gson()

    override fun onResourceManagerReload(resourceManager: ResourceManager) {
        videos.clear()

        val registryResources = resourceManager.listResources("videodialogues") { it.path.endsWith("registry.json") }

        for ((registryId, resource) in registryResources) {
            try {
                resource.open().reader().use { reader ->
                    val registryFile = gson.fromJson(reader, DialogueRegistryFile::class.java)

                    for (entry in registryFile.dialogues) {
                        val videoId = Identifier.parse(entry.id)
                        val soundId = entry.sound?.let { Identifier.parse(it) }

                        val videoPath = Identifier.fromNamespaceAndPath(
                            videoId.namespace,
                            "videodialogues/${videoId.path}.json"
                        )

                        val videoResource = resourceManager.getResource(videoPath).getOrNull()

                        if (videoResource != null) {
                            videoResource.open().reader().use { videoReader ->
                                val videoData = gson.fromJson(videoReader, Video::class.java)
                                videos[videoId] = RegisteredVideo(videoData, soundId)
                                logger.info("Registered video: $videoId (Sound: $soundId, Frames: ${videoData.frames.size})")
                            }
                        } else {
                            logger.error("Could not find video data file at $videoPath for ID $videoId")
                        }
                    }
                }
            } catch (e: Exception) {
                logger.error("Could not load video data file at $registryId", e)
            }
        }
    }

}