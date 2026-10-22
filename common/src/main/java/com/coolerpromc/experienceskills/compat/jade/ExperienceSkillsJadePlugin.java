package com.coolerpromc.experienceskills.compat.jade;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.client.SkillText;
import com.coolerpromc.experienceskills.entity.custom.ModExperienceOrb;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.Identifier;
import snownee.jade.api.EntityAccessor;
import snownee.jade.api.IEntityComponentProvider;
import snownee.jade.api.ITooltip;
import snownee.jade.api.IWailaClientRegistration;
import snownee.jade.api.IWailaPlugin;
import snownee.jade.api.WailaPlugin;
import snownee.jade.api.config.IPluginConfig;

@WailaPlugin
public class ExperienceSkillsJadePlugin implements IWailaPlugin {
    public static final Identifier ORB_INFO = Constants.id("orb_info");

    @Override
    public void registerClient(IWailaClientRegistration registration){
        registration.registerEntityComponent(OrbInfoProvider.INSTANCE, ModExperienceOrb.class);
    }

    private enum OrbInfoProvider implements IEntityComponentProvider {
        INSTANCE;

        @Override
        public void appendTooltip(ITooltip tooltip, EntityAccessor accessor, IPluginConfig config){
            if (accessor.getEntity() instanceof ModExperienceOrb orb){
                tooltip.add(Component.translatable("jade.experienceskills.orb", SkillText.skillName(orb.getExperienceType()).withStyle(s -> s.withColor(orb.getExperienceType().color())), orb.getValue()));
            }
        }

        @Override
        public Identifier getUid(){
            return ORB_INFO;
        }
    }
}
