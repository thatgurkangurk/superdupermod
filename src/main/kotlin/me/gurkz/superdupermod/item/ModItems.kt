package me.gurkz.superdupermod.item

import me.gurkz.superdupermod.SuperDuperMod
import me.gurkz.superdupermod.item.custom.SilencerStickItem
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.core.registries.Registries
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.Item

object ModItems {
    val SILENCER_STICK = register("silencer_stick", ::SilencerStickItem, Item.Properties().durability(32))

    fun <T : Item> register(
        name: String,
        itemFactory: (Item.Properties) -> T,
        settings: Item.Properties
    ): T {
        val key = ResourceKey.create(
            Registries.ITEM,
            SuperDuperMod.id(name)
        )

        return itemFactory(settings.setId(key)).also { item ->
            Registry.register(BuiltInRegistries.ITEM, key, item)
        }
    }

    fun initialise() {}
}