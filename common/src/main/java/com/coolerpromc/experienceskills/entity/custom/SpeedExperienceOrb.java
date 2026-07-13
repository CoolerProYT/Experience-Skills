package com.coolerpromc.experienceskills.entity.custom;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.Vec3;

public class SpeedExperienceOrb extends AbstractExperienceOrb {
    public SpeedExperienceOrb(Level level, Vec3 pos, Vec3 roughly, int value) {
        super(ModEntities.SPEED_EXPERIENCE_ORB.get(), level, pos, roughly, value);
    }

    public SpeedExperienceOrb(EntityType<? extends SpeedExperienceOrb> type, Level level) {
        super(type, level);
    }

    @Override
    public ExperienceType getExperienceType() {
        return ExperienceType.SPEED;
    }

    @Override
    public void updateSkillStatus(LivingEntity entity, AttachmentKey<Integer> key) {
        int level = getLevel(entity, key);
        float extraSpeed = level * ModCommonConfig.SPEED.get().incrementPerLevel();

        AttributeInstance attr = entity.getAttribute(Attributes.MOVEMENT_SPEED);
        if (attr != null){
            Identifier id = Constants.id("speed_skill_bonus");
            AttributeModifier modifier = new AttributeModifier(id, extraSpeed, AttributeModifier.Operation.ADD_MULTIPLIED_BASE);
            attr.removeModifier(id);
            attr.addPermanentModifier(modifier);

            if (!canAward()){
                attr.removeModifier(id);
            }
        }
    }
}
