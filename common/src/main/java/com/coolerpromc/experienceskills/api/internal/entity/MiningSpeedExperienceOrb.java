package com.coolerpromc.experienceskills.api.internal.entity;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.entity.custom.AbstractExperienceOrb;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

// TODO: Temporarily code to test API
public class MiningSpeedExperienceOrb extends AbstractExperienceOrb {
    public MiningSpeedExperienceOrb(Level level, Vec3 pos, Vec3 roughly, int value) {
        super(ModEntities.byName("mining_speed").get(), level, pos, roughly, value);
    }

    public MiningSpeedExperienceOrb(EntityType<? extends AbstractExperienceOrb> type, Level level) {
        super(type, level);
    }

    @Override
    public ExperienceType getExperienceType() {
        return ExperienceType.byName("mining_speed").orElse(null);
    }

    @Override
    public void updateSkillStatus(LivingEntity entity, AttachmentKey<Integer> key) {
        int level = getLevel(entity, key);
        float extraSpeed = level * ModCommonConfig.getConfigByPath("mining_speed").get().incrementPerLevel();

        AttributeInstance attr = entity.getAttribute(Attributes.BLOCK_BREAK_SPEED);
        if (attr != null){
            Identifier id = Constants.id("mining_speed_skill_bonus");
            AttributeModifier modifier = new AttributeModifier(id, extraSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            attr.removeModifier(id);
            attr.addPermanentModifier(modifier);

            if (!canAward()){
                attr.removeModifier(id);
            }
        }
    }
}
