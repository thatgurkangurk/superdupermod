package me.gurkz.superdupermod.kotlin.mixin

import net.minecraft.core.registries.Registries
import net.minecraft.world.effect.MobEffects
import net.minecraft.world.entity.Entity
import net.minecraft.world.entity.LivingEntity
import net.minecraft.world.item.enchantment.EnchantmentHelper
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.Level
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo

object FarmlandMixin {
    @JvmStatic
    fun disableTramplingWithFeatherFalling(
        level: Level, entity: Entity, ci: CallbackInfo
    ) {
        if (entity !is LivingEntity) return
        val livingEntity: LivingEntity = entity

        val featherFalling = level
            .registryAccess()
            .lookupOrThrow(Registries.ENCHANTMENT)
            .getOrThrow(Enchantments.FEATHER_FALLING)

        val featherFallingLevel = EnchantmentHelper.getEnchantmentLevel(featherFalling, entity)

        if (featherFallingLevel > 0 || livingEntity.hasEffect(MobEffects.SLOW_FALLING)) ci.cancel()
    }
}