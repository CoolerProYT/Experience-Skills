package com.coolerpromc.experienceskills.mixin;

import com.coolerpromc.experienceskills.attribute.ModAttributes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.animal.Animal;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Animal.class)
public abstract class AnimalMixin {
    @Inject(method = "finalizeSpawnChildFromBreeding", at = @At("HEAD"))
    private void spawnTwin(ServerLevel level, Animal partner, @Nullable AgeableMob offspring, CallbackInfo ci){
        if (offspring == null) return;

        Animal self = (Animal) (Object) this;
        ServerPlayer breeder = self.getLoveCause() != null ? self.getLoveCause() : partner.getLoveCause();
        if (breeder == null) return;

        double chance = breeder.getAttributeValue(ModAttributes.TWIN_CHANCE.holder());
        if (chance <= 0 || self.getRandom().nextDouble() >= chance) return;

        AgeableMob twin = self.getBreedOffspring(level, partner);
        if (twin != null){
            twin.setBaby(true);
            twin.snapTo(self.getX(), self.getY(), self.getZ(), 0.0F, 0.0F);
            level.addFreshEntityWithPassengers(twin);
        }
    }
}
