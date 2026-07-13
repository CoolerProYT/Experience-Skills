package com.coolerpromc.experienceskills.api.internal;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.ExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.api.type.IExperienceTypeRegistry;
import com.coolerpromc.experienceskills.attribute.ModAttributes;
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
    public static String BREAKING_SPEED = "breaking_speed";
    public static String STRENGTH = "strength";
    public static String VITALITY = "vitality";
    public static String JUMP = "jump";
    public static String OXYGEN = "oxygen";
    public static String SWIM_SPEED = "swim_speed";
    public static String FISHING_SPEED = "fishing_speed";
    public static String FISHING_LUCK = "fishing_luck";
    public static String ATTACK_DAMAGE = "attack_damage";
    public static String TOUGHNESS = "toughness";
    public static String BLOCK_REACH = "block_reach";
    public static String ENTITY_REACH = "entity_reach";

    @Override
    public void registerExperienceType(IExperienceTypeRegistry registry) {
        registry.register(WALKING_SPEED, new SkillsConfig(true, 0.02f,  100,  5, 16711680, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_WALKED.id()), Attributes.MOVEMENT_SPEED);
        registry.register(BREAKING_SPEED, new SkillsConfig(true, 0.02f, 10, 5, 0x00FF00, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.BLOCK_BROKEN.id()), Attributes.BLOCK_BREAK_SPEED);
        registry.register(STRENGTH, new SkillsConfig(true, 0.02f, 20, 5, 0xFF5555, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.ENTITY_KILLED.id()), Attributes.ATTACK_DAMAGE);
        registry.register(VITALITY, new SkillsConfig(true, 0.01f, 100, 5, 14060262, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, Stats.DAMAGE_TAKEN), Attributes.MAX_HEALTH);
        registry.register(JUMP, new SkillsConfig(true, 0.02f, 100, 5, 14392458, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, Stats.JUMP), Attributes.JUMP_STRENGTH, InternalExperienceSkillsPlugin::handleJump);
        registry.register(OXYGEN, new SkillsConfig(true, 0.05f, 1200, 5, 5745663, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.UNDER_WATER_TIME.id()), Attributes.OXYGEN_BONUS);
        registry.register(SWIM_SPEED, new SkillsConfig(true, 0.05f, 100, 5, 5800104, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.WATER_WALKED.id()), ModAttributes.SWIM_SPEED.holder());
        registry.register(FISHING_SPEED, new SkillsConfig(true, 2, 1200, 5, 14599001, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.FISHING_TIME.id()), ModAttributes.LURE_SPEED.holder());
        registry.register(FISHING_LUCK, new SkillsConfig(true, 0.8f, 10, 5, 12045424, AttributeModifier.Operation.ADD_VALUE, 100, ModStats.ITEMS_FISHED.id()), ModAttributes.FISHING_LUCK.holder());
        registry.register(ATTACK_DAMAGE, new SkillsConfig(true, 0.01f, 200, 5, 0xE8C33A, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, Stats.DAMAGE_DEALT), Attributes.ATTACK_DAMAGE);
        registry.register(TOUGHNESS, new SkillsConfig(true, 0.02f, 100, 5, 0x8A8A8A, AttributeModifier.Operation.ADD_VALUE, 100, Stats.DAMAGE_TAKEN), Attributes.ARMOR_TOUGHNESS);
        registry.register(BLOCK_REACH, new SkillsConfig(true, 0.01f, 50, 5, 0x55D6D6, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, Stats.DAMAGE_TAKEN), Attributes.BLOCK_INTERACTION_RANGE);
        registry.register(ENTITY_REACH, new SkillsConfig(true, 0.01f, 20, 5, 0xD65555, AttributeModifier.Operation.ADD_MULTIPLIED_BASE, 100, ModStats.ENTITY_KILLED.id()), Attributes.ENTITY_INTERACTION_RANGE);

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

    private static void handleJump(ServerPlayer player, int level, ExperienceType type){
        AttributeInstance attr = player.getAttribute(Attributes.SAFE_FALL_DISTANCE);
        if (attr != null){
            SkillsConfig config = type.getConfig();
            float jumpStrengthAtMaxLevel = 0.42f + (0.42f * config.incrementPerLevel() * level);
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
