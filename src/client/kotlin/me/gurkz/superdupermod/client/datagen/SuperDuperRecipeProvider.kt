package me.gurkz.superdupermod.client.datagen

import me.gurkz.superdupermod.item.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricDataOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import java.util.concurrent.CompletableFuture


class SuperDuperRecipeProvider(
    output: FabricDataOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun getName(): String {
        return "SuperDuperRecipeProvider"
    }

    override fun createRecipeProvider(
        registryLookup: HolderLookup.Provider,
        exporter: RecipeOutput
    ): RecipeProvider {
        return object : RecipeProvider(registryLookup, exporter) {
            override fun buildRecipes() {
                val itemLookup = registries.lookupOrThrow(Registries.ITEM)

                shaped(RecipeCategory.TOOLS, ModItems.SILENCER_STICK, 1)
                    .pattern("W  ")
                    .pattern(" A ")
                    .pattern("  A")
                    .define('W', ItemTags.WOOL)
                    .define('A', Items.AMETHYST_SHARD)
                    .unlockedBy(getHasName(Items.AMETHYST_SHARD), has(Items.AMETHYST_SHARD))
                    .unlockedBy("has_wool", has(ItemTags.WOOL))
                    .save(output)
            }
        }
    }
}