package com.coolerpromc.experienceskills.entity.helper;

import com.coolerpromc.experienceskills.api.internal.entity.MiningSpeedExperienceOrb;
import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import com.coolerpromc.experienceskills.entity.custom.SpeedExperienceOrb;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.phys.Vec3;

public interface ExperienceOrbFactory {
    AbstractExperienceOrb createExperienceOrb(ServerLevel level, Vec3 pos, Vec3 roughDirection, int amount);

    record SpeedExperienceOrbFactory() implements ExperienceOrbFactory{
        @Override
        public AbstractExperienceOrb createExperienceOrb(ServerLevel level, Vec3 pos, Vec3 roughDirection, int amount) {
            return new SpeedExperienceOrb(level, pos, roughDirection, amount);
        }
    }

    // TODO: Temporarily code to test API
    record MiningSpeedExperienceOrbFactory() implements ExperienceOrbFactory{
        @Override
        public AbstractExperienceOrb createExperienceOrb(ServerLevel level, Vec3 pos, Vec3 roughDirection, int amount) {
            return new MiningSpeedExperienceOrb(level, pos, roughDirection, amount);
        }
    }
}
