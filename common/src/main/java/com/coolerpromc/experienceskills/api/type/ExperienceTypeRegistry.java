package com.coolerpromc.experienceskills.api.type;

import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import com.coolerpromc.experienceskills.platform.Services;
import net.minecraft.core.Holder;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.ai.attributes.Attribute;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.Map;

/**
 * Internal implementation of {@link IExperienceTypeRegistry}. Collects every skill declared by plugins into
 * {@link #REGISTERED_TYPES}, from which the rest of the mod derives config entries, attachments, entities and
 * so on. Not part of the public API — plugins receive the {@link IExperienceTypeRegistry} interface instead.
 */
@ApiStatus.Internal
public class ExperienceTypeRegistry implements IExperienceTypeRegistry{
    public static final ExperienceTypeRegistry INSTANCE = new ExperienceTypeRegistry();
    public static final Map<String, RegistryHolder> REGISTERED_TYPES = new HashMap<>();

    @Override
    public void register(String name, SkillsConfig config, Holder<Attribute> attributeHolder, Handler handler) {
        REGISTERED_TYPES.put(name, new RegistryHolder(name, config, (entityType, level) -> new ModExperienceOrb(entityType, level, ExperienceType.byName(name).get()), attributeHolder, handler));
    }

    public static void init(){
        for (IExperienceSkillsPlugin plugin : Services.PLATFORM.getPlugins()) {
            plugin.registerExperienceType(INSTANCE);
        }
    }

    public record RegistryHolder(String name, SkillsConfig config, EntityType.EntityFactory<? extends ModExperienceOrb> factory, Holder<Attribute> attributeHolder, Handler handler){
    }
}
