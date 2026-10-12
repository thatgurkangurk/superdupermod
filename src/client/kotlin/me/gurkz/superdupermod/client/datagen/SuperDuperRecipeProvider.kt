/*
 * Copyright 2026 Gurkan
 *
 * This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/.
 */

package me.gurkz.superdupermod.client.datagen

import me.gurkz.superdupermod.item.ModItems
import net.fabricmc.fabric.api.datagen.v1.FabricPackOutput
import net.fabricmc.fabric.api.datagen.v1.provider.FabricRecipeProvider
import net.minecraft.advancements.Advancement
import net.minecraft.core.HolderLookup
import net.minecraft.data.recipes.RecipeCategory
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.tags.ItemTags
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Recipe
import java.util.concurrent.CompletableFuture

class SuperDuperRecipeProvider(
    output: FabricPackOutput,
    registriesFuture: CompletableFuture<HolderLookup.Provider>
) : FabricRecipeProvider(output, registriesFuture) {

    override fun getName(): String {
        return "SuperDuperRecipeProvider"
    }

    override fun createRecipeProvider(
        registries: HolderLookup.Provider,
        recipes: BootstrapContext<Recipe<*>>,
        advancements: BootstrapContext<Advancement>
    ): RecipeProvider {
        return object : RecipeProvider(recipes, advancements) {
            override fun buildRecipes() {
                this.shaped(RecipeCategory.TOOLS, ModItems.SILENCER_STICK, 1)
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