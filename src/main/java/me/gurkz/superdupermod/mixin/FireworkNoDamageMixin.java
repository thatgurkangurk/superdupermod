package me.gurkz.superdupermod.mixin;

import net.minecraft.world.entity.projectile.FireworkRocketEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(FireworkRocketEntity.class)
public abstract class FireworkNoDamageMixin {

    @Inject(method = "dealExplosionDamage", at = @At("HEAD"), cancellable = true)
    private void superdupermod$cancelFireworkDamage(CallbackInfo ci) {
        FireworkRocketEntity firework = (FireworkRocketEntity) (Object) this;

        if (firework.entityTags().contains("superdupermod:no_damage")) {
            ci.cancel();
        }
    }
}
