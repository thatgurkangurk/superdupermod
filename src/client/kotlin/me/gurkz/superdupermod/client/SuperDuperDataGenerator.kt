package me.gurkz.superdupermod.client

import me.gurkz.superdupermod.client.datagen.SuperDuperModelProvider
import me.gurkz.superdupermod.client.datagen.SuperDuperRecipeProvider
import net.fabricmc.fabric.api.datagen.v1.DataGeneratorEntrypoint
import net.fabricmc.fabric.api.datagen.v1.FabricDataGenerator

object SuperDuperDataGenerator : DataGeneratorEntrypoint {
    override fun onInitializeDataGenerator(fabricDataGenerator: FabricDataGenerator) {
        val pack = fabricDataGenerator.createPack()

        pack.addProvider(::SuperDuperModelProvider)
        pack.addProvider(::SuperDuperRecipeProvider)
    }
}