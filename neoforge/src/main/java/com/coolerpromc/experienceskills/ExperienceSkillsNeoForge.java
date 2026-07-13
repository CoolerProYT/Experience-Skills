package com.coolerpromc.experienceskills;

import com.coolerpromc.experienceskills.api.ExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.api.IExperienceSkillsPlugin;
import com.coolerpromc.experienceskills.command.ModCommands;
import com.coolerpromc.experienceskills.event.*;
import com.coolerpromc.experienceskills.platform.NeoForgeRegistryHelper;
import com.coolerpromc.experienceskills.stat.ModStats;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.bus.api.EventPriority;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.level.BlockEvent;
import net.neoforged.neoforgespi.language.ModFileScanData;
import org.objectweb.asm.Type;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Mod(Constants.MODID)
@EventBusSubscriber(modid = Constants.MODID)
public class ExperienceSkillsNeoForge {
    private static final Type TYPE = Type.getType(ExperienceSkillsPlugin.class);
    public static final List<IExperienceSkillsPlugin> PLUGINS = new ArrayList<>();

    public ExperienceSkillsNeoForge(IEventBus eventBus) {
        List<ModFileScanData.AnnotationData> annotations = ModList.get().getAllScanData().stream().map(ModFileScanData::getAnnotations).flatMap(Collection::stream).filter(a -> TYPE.equals(a.annotationType())).toList();

        for (ModFileScanData.AnnotationData annotationData : annotations){
            String targetClassName = annotationData.memberName();

            try {
                Class<?> targetClass = Class.forName(targetClassName, false, IExperienceSkillsPlugin.class.getClassLoader());

                if (IExperienceSkillsPlugin.class.isAssignableFrom(targetClass)) {
                    IExperienceSkillsPlugin plugin = (IExperienceSkillsPlugin) targetClass.getDeclaredConstructor().newInstance();
                    PLUGINS.add(plugin);
                }

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (InvocationTargetException | InstantiationException | IllegalAccessException | NoSuchMethodException e) {
                throw new RuntimeException(e);
            }
        }

        ExperienceSkills.init();

        NeoForgeRegistryHelper.register(eventBus);
    }

    @SubscribeEvent
    public static void onStatAward(net.neoforged.neoforge.event.StatAwardEvent event) {
        StatAwardEvent.onStatsAward(event.getEntity(), event.getStat(), event.getValue());
    }

    @SubscribeEvent
    public static void onLivingDeath(net.neoforged.neoforge.event.entity.living.LivingDeathEvent event) {
        LivingDeathEvent.onLivingDeath(event.getEntity(), event.getSource());
    }

    @SubscribeEvent
    public static void onPlayerLoggedIn(PlayerEvent.PlayerLoggedInEvent event) {
        if (event.getEntity() instanceof ServerPlayer player){
            PlayerJoinEvent.onPlayerJoined(player);
        }
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        ModCommands.register(event.getDispatcher(), event.getBuildContext(), event.getCommandSelection());
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlock(BlockEvent.BreakEvent event) {
        if (!event.isCanceled()){
            PlayerBlockBreakEvent.afterBreak(event.getPlayer().level(), event.getPlayer(), event.getPos(), event.getState(), event.getLevel().getBlockEntity(event.getPos()));
        }
    }

    @SubscribeEvent
    public static void onServerStarted(net.neoforged.neoforge.event.server.ServerStartedEvent event) {
        ServerStartedEvent.onServerStarted(event.getServer());
    }

    @SubscribeEvent
    public static void onPlayerTick(net.neoforged.neoforge.event.tick.PlayerTickEvent.Post event) {
        PlayerTickEvent.onPlayerTickEnd(event.getEntity());
    }

    @SubscribeEvent
    public static void onBlockEntityPlace(BlockEvent.EntityPlaceEvent event) {
        if (event.getEntity() instanceof ServerPlayer player) player.awardStat(ModStats.BLOCK_PLACED.get(), 1);
    }
}