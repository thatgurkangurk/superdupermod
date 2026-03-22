package me.gurkz.superdupermod.client.datagen

import me.gurkz.superdupermod.item.ModItems
import net.fabricmc.fabric.api.client.datagen.v1.provider.FabricModelProvider
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.ItemModelGenerators
import net.minecraft.client.data.models.model.ModelTemplates

class SuperDuperModelProvider(output: FabricDataOutput) : FabricModelProvider(output) {
    override fun generateBlockStateModels(blockModelGenerator: BlockModelGenerators) {

    }

    override fun generateItemModels(itemModelGenerator: ItemModelGenerators) {
        itemModelGenerator.generateFlatItem(ModItems.SILENCER_STICK, ModelTemplates.FLAT_ITEM)
    }

    override fun getName(): String {
        return "SuperDuperModelProvider"
    }
}