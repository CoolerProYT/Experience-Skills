package com.coolerpromc.experienceskills.client;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.value.Milestone;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.coolerpromc.experienceskills.item.ModItems;
import net.minecraft.core.Holder;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.resources.Identifier;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.item.ItemStack;

import java.util.Locale;

public class SkillText {
    public static MutableComponent skillName(ExperienceType type){
        return Component.translatableWithFallback("skill.experienceskills." + type.getSerializedName(), prettify(type.getSerializedName()));
    }

    public static MutableComponent statName(Identifier stat){
        return Component.translatableWithFallback("stat." + stat.toString().replace(':', '.'), prettify(stat.getPath()));
    }

    public static MutableComponent bonus(ExperienceType type, int level){
        Holder<Attribute> attribute = type.getAttributeHolder();
        MutableComponent amount = Component.literal(formatAmount(type.bonusForLevel(level), type.operation()));
        if (attribute == null) return amount;
        return amount.append(" ").append(Component.translatable(attribute.value().getDescriptionId()));
    }

    public static MutableComponent milestone(Milestone milestone){
        if (milestone.effect().isPresent()){
            Identifier id = milestone.effect().get();
            MutableComponent name = BuiltInRegistries.MOB_EFFECT.get(id).map(h -> h.value().getDisplayName().copy()).orElse(Component.literal(id.toString()));
            if (milestone.amplifier() > 0){
                name.append(" ").append(Component.translatable("enchantment.level." + (milestone.amplifier() + 1)));
            }
            return name;
        }

        Identifier id = milestone.attribute().orElseThrow();
        MutableComponent name = BuiltInRegistries.ATTRIBUTE.get(id).map(h -> Component.translatable(h.value().getDescriptionId())).orElse(Component.literal(id.toString()));
        return Component.literal(formatAmount(milestone.amount(), milestone.operation()) + " ").append(name);
    }

    public static ItemStack icon(ExperienceType type){
        ItemStack stack = ModItems.EXPERIENCE_BOTTLE.toStack();
        stack.set(ModDataComponents.EXPERIENCE_TYPE.get(), type);
        return stack;
    }

    private static String formatAmount(double amount, AttributeModifier.Operation operation){
        if (operation == AttributeModifier.Operation.ADD_VALUE){
            return String.format(Locale.ROOT, "%+.2f", amount);
        }
        return String.format(Locale.ROOT, "%+.1f%%", amount * 100);
    }

    private static String prettify(String id){
        StringBuilder out = new StringBuilder();
        for (String word : id.split("_")){
            if (word.isEmpty()) continue;
            if (!out.isEmpty()) out.append(' ');
            out.append(Character.toUpperCase(word.charAt(0))).append(word.substring(1));
        }
        return out.toString();
    }
}
