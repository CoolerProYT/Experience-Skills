package com.coolerpromc.experienceskills.api.internal;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.ExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.api.type.IExperienceTypeRegistry;
import com.coolerpromc.experienceskills.attribute.ModAttributes;
import com.coolerpromc.experienceskills.config.value.Milestone;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.stat.ModStats;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Holder;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import org.apache.commons.io.IOUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

@ExperienceSkillsPlugin
public final class InternalExperienceSkillsPlugin implements IExperienceSkillsPlugin {
    public static String WALKING_SPEED = "walking_speed";
    public static String STRENGTH = "strength";
    public static String VITALITY = "vitality";
    public static String JUMP = "jump";
    public static String OXYGEN = "oxygen";
    public static String SWIM_SPEED = "swim_speed";
    public static String FISHING_SPEED = "fishing_speed";
    public static String FISHING_LUCK = "fishing_luck";
    public static String TOUGHNESS = "toughness";
    public static String BLOCK_REACH = "block_reach";
    public static String ENTITY_REACH = "entity_reach";
    public static String MINING_LUCK = "mining_luck";
    public static String SUBMERGED_BREAKING_SPEED = "submerged_breaking_speed";
    public static String PICKAXE_BREAKING_SPEED = "pickaxe_breaking_speed";
    public static String SHOVEL_BREAKING_SPEED = "shovel_breaking_speed";
    public static String AXE_BREAKING_SPEED = "axe_breaking_speed";
    public static String HOE_BREAKING_SPEED = "hoe_breaking_speed";
    public static String FLOATING_BREAKING_SPEED = "floating_breaking_speed";
    public static String KNOCKBACK_RESISTANCE = "knockback_resistance";
    public static String FALL_RESISTANCE = "fall_resistance";
    public static String SNEAKING = "sneaking";
    public static String LUCK = "luck";
    public static String FIRE_RESISTANCE = "fire_resistance";
    public static String ARCHERY = "archery";
    public static String FARMING = "farming";
    public static String ENCHANTING = "enchanting";
    public static String TRADING = "trading";
    public static String BREEDING = "breeding";
    public static String COOKING = "cooking";

    @Override
    public void registerExperienceType(IExperienceTypeRegistry registry) {
        registry.register(WALKING_SPEED, new SkillsConfig(true, 0.02f,  100,  5, 16711680, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_WALKED.id(), true)
            .withMilestones(Milestone.attribute(50, vanilla("step_height"), 0.5, AttributeModifier.Operation.ADD_VALUE)), Attributes.MOVEMENT_SPEED);
        registry.register(STRENGTH, new SkillsConfig(true, 0.02f, 20, 5, 0xFF5555, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.ENTITY_KILLED.id(), true)
            .withMilestones(Milestone.attribute(50, vanilla("attack_knockback"), 0.5, AttributeModifier.Operation.ADD_VALUE)), Attributes.ATTACK_DAMAGE);
        registry.register(VITALITY, new SkillsConfig(true, 0.01f, 100, 5, 14060262, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, Stats.DAMAGE_TAKEN, true)
            .withMilestones(Milestone.attribute(50, vanilla("max_health"), 4, AttributeModifier.Operation.ADD_VALUE)), Attributes.MAX_HEALTH);
        registry.register(JUMP, new SkillsConfig(true, 0.02f, 100, 5, 14392458, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, Stats.JUMP, true)
            .withMilestones(Milestone.attribute(50, vanilla("fall_damage_multiplier"), -0.25, AttributeModifier.Operation.ADD_MULTIPLIED_BASE)), Attributes.JUMP_STRENGTH, InternalExperienceSkillsPlugin::handleJump);
        registry.register(OXYGEN, new SkillsConfig(true, 0.05f, 1200, 5, 5745663, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.UNDER_WATER_TIME.id(), true)
            .withMilestones(Milestone.effect(100, vanilla("water_breathing"), 0)), Attributes.OXYGEN_BONUS);
        registry.register(SWIM_SPEED, new SkillsConfig(true, 0.05f, 100, 5, 5800104, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.WATER_WALKED.id(), true)
            .withMilestones(Milestone.effect(50, vanilla("dolphins_grace"), 0)), ModAttributes.SWIM_SPEED.holder());
        registry.register(FISHING_SPEED, new SkillsConfig(true, 2, 1200, 5, 14599001, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.FISHING_TIME.id(), true), ModAttributes.LURE_SPEED.holder());
        registry.register(FISHING_LUCK, new SkillsConfig(true, 0.8f, 10, 5, 12045424, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.ITEMS_FISHED.id(), true)
            .withMilestones(Milestone.attribute(50, vanilla("luck"), 1, AttributeModifier.Operation.ADD_VALUE)), ModAttributes.FISHING_LUCK.holder());
        registry.register(TOUGHNESS, new SkillsConfig(true, 0.02f, 100, 5, 0x8A8A8A, AttributeModifier.Operation.ADD_VALUE, 100, Stats.DAMAGE_TAKEN, true)
            .withMilestones(Milestone.attribute(50, vanilla("knockback_resistance"), 0.1, AttributeModifier.Operation.ADD_VALUE)), Attributes.ARMOR_TOUGHNESS);
        registry.register(BLOCK_REACH, new SkillsConfig(true, 0.01f, 50, 5, 0x55D6D6, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_INTERACTED.id(), true), Attributes.BLOCK_INTERACTION_RANGE);
        registry.register(ENTITY_REACH, new SkillsConfig(true, 0.01f, 20, 5, 0xD65555, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.ENTITY_KILLED.id(), true), Attributes.ENTITY_INTERACTION_RANGE);
        registry.register(MINING_LUCK, new SkillsConfig(true, 0.2f, 50, 5, 9412536, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.ORE_MINED.id(), true), ModAttributes.MINING_LUCK.holder());
        registry.register(SUBMERGED_BREAKING_SPEED, new SkillsConfig(true, 0.02f, 50, 5, 0x9cfff3, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_MINED_IN_WATER.id(), true), Attributes.SUBMERGED_MINING_SPEED);
        registry.register(PICKAXE_BREAKING_SPEED, new SkillsConfig(true, 0.05f, 50, 5, 0xfcba03, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_MINED_WITH_PICKAXE.id(), true)
            .withMilestones(Milestone.effect(100, vanilla("haste"), 0)), ModAttributes.PICKAXE_MINING_SPEED.holder());
        registry.register(SHOVEL_BREAKING_SPEED, new SkillsConfig(true, 0.05f, 50, 5, 0xc2e35f, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_MINED_WITH_SHOVEL.id(), true), ModAttributes.SHOVEL_MINING_SPEED.holder());
        registry.register(AXE_BREAKING_SPEED, new SkillsConfig(true, 0.05f, 50, 5, 0x58dbba, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_MINED_WITH_AXE.id(), true), ModAttributes.AXE_MINING_SPEED.holder());
        registry.register(HOE_BREAKING_SPEED, new SkillsConfig(true, 0.05f, 50, 5, 0xab79e0, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_MINED_WITH_HOE.id(), true), ModAttributes.HOE_MINING_SPEED.holder());
        registry.register(FLOATING_BREAKING_SPEED, new SkillsConfig(true, 0.04f, 20, 5, 0xd5d998, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.BLOCK_MINED_WHEN_FLOATING.id(), true), ModAttributes.FLOATING_MINING_SPEED.holder());

        registry.register(KNOCKBACK_RESISTANCE, new SkillsConfig(true, 0.004f, 100, 5, 0x6B5B95, AttributeModifier.Operation.ADD_VALUE, 100, Stats.DAMAGE_BLOCKED_BY_SHIELD, true), Attributes.KNOCKBACK_RESISTANCE);
        registry.register(FALL_RESISTANCE, new SkillsConfig(true, 0.05f, 1000, 5, 0xA0C4FF, AttributeModifier.Operation.ADD_VALUE, 100, Stats.FALL_ONE_CM, true), Attributes.SAFE_FALL_DISTANCE);
        registry.register(SNEAKING, new SkillsConfig(true, 0.004f, 1000, 5, 0x4A4E69, AttributeModifier.Operation.ADD_VALUE, 100, Stats.CROUCH_ONE_CM, true), Attributes.SNEAKING_SPEED);
        registry.register(LUCK, new SkillsConfig(true, 0.02f, 10, 5, 0x3DDC84, AttributeModifier.Operation.ADD_VALUE, 100, Stats.OPEN_CHEST, true), Attributes.LUCK);

        registry.register(FIRE_RESISTANCE, new SkillsConfig(true, -0.005f, 200, 5, 0xFF8C1A, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.TIME_ON_FIRE.id(), true)
            .withMilestones(Milestone.effect(100, vanilla("fire_resistance"), 0)), Attributes.BURNING_TIME);
        registry.register(ARCHERY, new SkillsConfig(true, 0.005f, 20, 5, 0xC9A66B, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.ARROWS_HIT.id(), true), ModAttributes.PROJECTILE_DAMAGE.holder());
        registry.register(FARMING, new SkillsConfig(true, 0.005f, 50, 5, 0xE9C46A, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.CROPS_HARVESTED.id(), true), ModAttributes.HARVEST_LUCK.holder());
        registry.register(ENCHANTING, new SkillsConfig(true, 0.005f, 5, 5, 0xB388FF, AttributeModifier.Operation.ADD_VALUE, 100, Stats.ENCHANT_ITEM, true), ModAttributes.ENCHANT_REFUND_CHANCE.holder());
        registry.register(TRADING, new SkillsConfig(true, 0.003f, 10, 5, 0x2A9D8F, AttributeModifier.Operation.ADD_VALUE, 100, Stats.TRADED_WITH_VILLAGER, true), ModAttributes.TRADE_DISCOUNT.holder());
        registry.register(BREEDING, new SkillsConfig(true, 0.003f, 10, 5, 0xF4A6C1, AttributeModifier.Operation.ADD_VALUE, 100, Stats.ANIMALS_BRED, true), ModAttributes.TWIN_CHANCE.holder());
        registry.register(COOKING, new SkillsConfig(true, 0.005f, 20, 5, 0xD62828, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.FOOD_EATEN.id(), true), ModAttributes.SATURATION_BONUS.holder());

        this.registerFromConfigFiles(registry);
    }

    private void registerFromConfigFiles(IExperienceTypeRegistry registry){
        File dir = Services.PLATFORM.getConfigDir().resolve("experienceskills/skills/").toFile();
        if (!dir.exists() && dir.mkdirs()) {
            Constants.LOGGER.info("Created /config/experienceskills/skills/ directory");
        }

        try (var paths = Files.walk(dir.toPath())) {
            List<File> files = paths.filter(Files::isRegularFile).filter(path -> path.toString().toLowerCase().endsWith(".json")).map(Path::toFile).toList();

            for (File file : files) {
                InputStreamReader reader = null;
                String name = null;
                ConfigRegistry configRegistry = null;

                try {
                    reader = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
                    JsonObject json = JsonParser.parseReader(reader).getAsJsonObject();

                    configRegistry = ConfigRegistry.CODEC.parse(JsonOps.INSTANCE, json).getOrThrow();
                    name = configRegistry.name();

                    reader.close();
                } catch (Exception e) {
                    Constants.LOGGER.error("Something is wrong when registering {} skill type.", name, e);
                } finally {
                    IOUtils.closeQuietly(reader);
                }

                if (configRegistry != null){
                    registry.register(configRegistry.name(), configRegistry.config(), configRegistry.attribute());
                }
            }
        } catch (Exception e) {
            Constants.LOGGER.error("Something is wrong when registering skill type.", e);
        }
    }

    private static Identifier vanilla(String path) {
        return Identifier.withDefaultNamespace(path);
    }

    private static void handleJump(ServerPlayer player, int level, ExperienceType type){
        AttributeInstance attr = player.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        if (attr != null){
            SkillsConfig config = type.getConfig();
            float jumpStrengthAtMaxLevel = 0.42f + (0.42f * (float) config.bonusForLevel(level));
            float safeFallBonus = safeFallDistanceForLevel(jumpStrengthAtMaxLevel) - 3.0f;

            Identifier id = Constants.id("update_for_jump");
            AttributeModifier modifier = new AttributeModifier(id, safeFallBonus, AttributeModifier.Operation.ADD_VALUE);
            attr.removeModifier(id);
            attr.addPermanentModifier(modifier);

            if (!config.enabled()){
                attr.removeModifier(id);
            }
        }
    }

    public static double simulateJumpHeight(double jumpStrength) {
        double v = jumpStrength;
        double height = 0;
        while (v > 0) {
            height += v;
            v = (v - 0.08) * 0.98;
        }
        return height;
    }

    public static float safeFallDistanceForLevel(float jumpStrengthAtLevel) {
        double baseHeight = simulateJumpHeight(0.42);
        double leveledHeight = simulateJumpHeight(jumpStrengthAtLevel);
        double bonusHeight = leveledHeight - baseHeight;
        return (float)(3.0 + bonusHeight);
    }

    private record ConfigRegistry(String name, SkillsConfig config, Holder<Attribute> attribute){
        public static final Codec<ConfigRegistry> CODEC = RecordCodecBuilder.create(i -> i.group(
            Codec.STRING.fieldOf("name").forGetter(ConfigRegistry::name),
            SkillsConfig.CODEC.fieldOf("defaultConfig").forGetter(ConfigRegistry::config),
            Attribute.CODEC.fieldOf("attribute").forGetter(ConfigRegistry::attribute)
        ).apply(i, ConfigRegistry::new));
    }
}
