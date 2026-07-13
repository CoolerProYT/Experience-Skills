package com.coolerpromc.experienceskills.platform;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.platform.services.IRegistryHelper;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.commands.synchronization.ArgumentTypeInfos;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.attachment.AttachmentType;
import net.neoforged.neoforge.registries.*;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class NeoForgeRegistryHelper implements IRegistryHelper {
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(Constants.MODID);
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(Constants.MODID);
    public static final DeferredRegister.Entities ENTITIES = DeferredRegister.createEntities(Constants.MODID);
    public static final DeferredRegister<AttachmentType<?>> ATTACHMENTS = DeferredRegister.create(NeoForgeRegistries.ATTACHMENT_TYPES, Constants.MODID);
    public static final DeferredRegister<EntityDataSerializer<?>> ENTITY_DATA_SERIALIZERS = DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, Constants.MODID);
    public static final DeferredRegister<ArgumentTypeInfo<?, ?>> ARGUMENT_TYPE_INFOS = DeferredRegister.create(Registries.COMMAND_ARGUMENT_TYPE, Constants.MODID);

    @Override
    public <T extends Block> RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, BlockBehaviour.Properties p) {
        DeferredBlock<T> deferredBlock = BLOCKS.registerBlock(name, func, () -> p);
        return () -> deferredBlock;
    }

    @Override
    public <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func, Item.Properties p) {
        DeferredItem<T> deferredItem = ITEMS.registerItem(name, func, () -> p);
        return () -> deferredItem;
    }

    @Override
    public <T extends Entity> RegistryHandler.Entities<T> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder) {
        DeferredHolder<EntityType<?>, EntityType<T>> deferredHolder = ENTITIES.registerEntityType(name, factory, category, builder);
        return () -> deferredHolder;
    }

    @Override
    public <T> RegistryHandler<EntityDataSerializer<?>, EntityDataSerializer<T>> registerEntityDataSerializer(String name, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<T>> deferredHolder = ENTITY_DATA_SERIALIZERS.register(name, () -> EntityDataSerializer.forValueType(streamCodec));
        return () -> deferredHolder;
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> RegistryHandler<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<A, T>> registerArgumentTypeInfo(String name, Class<A> clazz, ArgumentTypeInfo<A, T> info) {
        DeferredHolder<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<A, T>> deferredHolder = ARGUMENT_TYPE_INFOS.register(name, () -> ArgumentTypeInfos.registerByClass(clazz, info));
        return () -> deferredHolder;
    }

    public static void register(IEventBus eventBus){
        BLOCKS.register(eventBus);
        ITEMS.register(eventBus);
        ENTITIES.register(eventBus);
        ATTACHMENTS.register(eventBus);
        ENTITY_DATA_SERIALIZERS.register(eventBus);
        ARGUMENT_TYPE_INFOS.register(eventBus);
    }
}
