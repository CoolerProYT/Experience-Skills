package com.coolerpromc.experienceskills.compat.jei;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.ExperienceSkillsAPI;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.client.SkillText;
import com.coolerpromc.experienceskills.config.value.Milestone;
import com.coolerpromc.experienceskills.config.value.SkillsConfig;
import com.coolerpromc.experienceskills.config.value.WeightedStat;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.coolerpromc.experienceskills.item.ModItems;
import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.registration.IRecipeRegistration;
import mezz.jei.api.registration.ISubtypeRegistration;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

@JeiPlugin
public class ExperienceSkillsJeiPlugin implements IModPlugin {
    @Override
    public Identifier getPluginUid(){
        return Constants.id("jei_plugin");
    }

    @Override
    public void registerItemSubtypes(ISubtypeRegistration registration){
        registration.registerFromDataComponentTypes(ModItems.EXPERIENCE_BOTTLE.get(), ModDataComponents.EXPERIENCE_TYPE.get());
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration){
        for (ExperienceType type : ExperienceSkillsAPI.getEnabledTypes()){
            registration.addItemStackInfo(SkillText.icon(type), describe(type).toArray(Component[]::new));
        }
    }

    private static List<Component> describe(ExperienceType type){
        SkillsConfig config = type.getConfig();
        List<Component> lines = new ArrayList<>();

        lines.add(Component.translatable("jei.experienceskills.bottle", SkillText.skillName(type)));
        lines.add(Component.translatable("jei.experienceskills.earned_by", config.xpPointToAward(), config.xpAwardActionCount()));
        for (WeightedStat source : config.allStats()){
            lines.add(Component.literal(" • ").append(SkillText.statName(source.stat())));
        }
        lines.add(Component.translatable("jei.experienceskills.bonus", SkillText.bonus(type, type.getMaxLevel()), type.getMaxLevel()));

        config.milestones().stream().sorted(Comparator.comparingInt(Milestone::level)).forEach(m ->
            lines.add(Component.translatable("jei.experienceskills.milestone", m.level(), SkillText.milestone(m))));
        return lines;
    }
}
