package com.coolerpromc.experienceskills.skill;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.event.SkillEvents;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.ModGeneralConfig;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import com.coolerpromc.experienceskills.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;

public class SkillManager {
    public static int getPoints(Entity entity, ExperienceType type){
        AttachmentKey<Integer> key = type.getKey();
        if (key == null) return 0;
        return Services.ATTACHMENT.get(entity, key, 0);
    }

    public static int getLevel(Entity entity, ExperienceType type){
        return type.levelFor(getPoints(entity, type));
    }

    public static float getProgress(Entity entity, ExperienceType type){
        return type.getConfig().curve().progress(getPoints(entity, type));
    }

    public static int getPointsIntoLevel(Entity entity, ExperienceType type){
        int points = getPoints(entity, type);
        return points - type.totalPointsForLevel(type.levelFor(points));
    }

    public static boolean reachedMaxLevel(Entity entity, ExperienceType type){
        return getLevel(entity, type) >= type.getMaxLevel();
    }

    public static void setPoints(ServerPlayer player, ExperienceType type, int points, boolean celebrate){
        AttachmentKey<Integer> key = type.getKey();
        if (key == null) return;

        int oldLevel = getLevel(player, type);
        Services.ATTACHMENT.set(player, key, Math.max(0, points));
        int newLevel = getLevel(player, type);

        updateSkillStatus(player, type);

        if (newLevel != oldLevel){
            if (celebrate && newLevel > oldLevel){
                float volume = newLevel >= 30 ? 1.0F : newLevel / 30.0F;
                player.level().playSound(null, player.getX(), player.getY(), player.getZ(), SoundEvents.PLAYER_LEVELUP, player.getSoundSource(), Math.max(0.3F, volume) * 0.75F, 1.0F);
            }
            for (SkillEvents.OnLevelChange listener : SkillEvents.LEVEL_CHANGE){
                listener.onLevelChange(player, type, oldLevel, newLevel);
            }
        }
    }

    public static void addPoints(ServerPlayer player, ExperienceType type, int amount, boolean celebrate){
        long total = (long) getPoints(player, type) + amount;
        setPoints(player, type, (int) Math.clamp(total, 0, Integer.MAX_VALUE), celebrate);
    }

    public static void updateSkillStatus(ServerPlayer player, ExperienceType type){
        if (type == ExperienceType.VANILLA) return;

        int level = getLevel(player, type);
        Holder<Attribute> attribute = type.getAttributeHolder();
        AttributeInstance instance = attribute == null ? null : player.getAttribute(attribute);
        if (instance != null){
            Identifier id = Constants.id(type.getSerializedName() + "_skill_bonus");
            instance.removeModifier(id);
            if (type.isEnabled()){
                instance.addOrUpdateTransientModifier(new AttributeModifier(id, type.bonusForLevel(level), type.operation()));
            }
            type.onExperiencePointChange(player, level);
        }

        SkillMilestones.apply(player, type, level);
    }

    public static void updateAll(ServerPlayer player){
        for (ExperienceType type : ExperienceSkillsAPI.getAllTypes()){
            updateSkillStatus(player, type);
        }
    }

    public static void shareWithNearby(ServerPlayer collector, ExperienceType type, int amount){
        double fraction = ModGeneralConfig.SHARED_XP_FRACTION.get();
        double radius = ModGeneralConfig.SHARED_XP_RADIUS.get();
        if (fraction <= 0 || radius <= 0) return;

        double exact = amount * fraction;
        int whole = (int) Math.floor(exact);
        double remainder = exact - whole;

        for (ServerPlayer other : collector.level().getPlayers(p -> p != collector && !p.isSpectator() && p.isAlive() && p.distanceToSqr(collector) <= radius * radius)){
            int shared = whole + (other.getRandom().nextDouble() < remainder ? 1 : 0);
            if (shared > 0 && !reachedMaxLevel(other, type)){
                addPoints(other, type, shared, true);
            }
        }
    }

    public static int modifyGain(ServerPlayer player, ExperienceType type, int amount){
        for (SkillEvents.ModifyGain modifier : SkillEvents.MODIFY_GAIN){
            amount = modifier.modify(player, type, amount);
        }
        return Math.max(0, amount);
    }
}
