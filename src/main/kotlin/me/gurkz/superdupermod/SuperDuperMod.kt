package me.gurkz.superdupermod

import net.fabricmc.api.ModInitializer
import org.slf4j.Logger
import org.slf4j.LoggerFactory

object SuperDuperMod : ModInitializer {
    private const val MOD_ID: String = "superdupermod"
    private val LOGGER: Logger = LoggerFactory.getLogger(MOD_ID)

    override fun onInitialize() {
        LOGGER.info("hi from super duper mod")
    }
}