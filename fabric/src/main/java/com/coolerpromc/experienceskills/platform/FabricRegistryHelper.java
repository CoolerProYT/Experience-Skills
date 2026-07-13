package com.coolerpromc.experienceskills.platform;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.platform.services.IRegistryHelper;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import com.mojang.brigadier.arguments.ArgumentType;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.fabricmc.fabric.api.object.builder.v1.entity.FabricEntityDataRegistry;
import net.fabricmc.fabric.mixin.command.ArgumentTypeInfosAccessor;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Function;
import java.util.function.UnaryOperator;

public class FabricRegistryHelper implements IRegistryHelper {
    @Override
    public <T extends Block> RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, BlockBehaviour.Properties p) {
        ResourceKey<Block> key = IRegistryHelper.blockKey(name);
        Holder<Block> holder = Registry.registerForHolder(BuiltInRegistries.BLOCK, key, func.apply(p.setId(key)));

        return () -> holder;
    }

    @Override
    public <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func, Item.Properties p) {
        ResourceKey<Item> key = IRegistryHelper.itemKey(name);
        Holder<Item> holder = Registry.registerForHolder(BuiltInRegistries.ITEM, key, func.apply(p.setId(key)));

        return () -> holder;
    }

    @Override
    public <T extends Entity> RegistryHandler.Entities<T> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder) {
        ResourceKey<EntityType<?>> key = IRegistryHelper.entityKey(name);
        Holder<EntityType<?>> holder = Registry.registerForHolder(BuiltInRegistries.ENTITY_TYPE, key, builder.apply(EntityType.Builder.of(factory, category)).build(key));

        return () -> holder;
    }

    @Override
    public <T> RegistryHandler<EntityDataSerializer<?>, EntityDataSerializer<T>> registerEntityDataSerializer(String name, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec) {
        Identifier id = Constants.id(name);
        EntityDataSerializer<T> serializer = EntityDataSerializer.forValueType(streamCodec);
        FabricEntityDataRegistry.register(id, serializer);

        return new RegistryHandler<>() {
            @Override
            public Holder<EntityDataSerializer<?>> holder() {
                return Holder.direct(serializer);
            }

            @Override
            public EntityDataSerializer<T> get() {
                return serializer;
            }
        };
    }

    @Override
    public <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> RegistryHandler<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<A, T>> registerArgumentTypeInfo(String name, Class<A> clazz, ArgumentTypeInfo<A, T> info) {
        Identifier id = Constants.id(name);
        ArgumentTypeInfosAccessor.fabric_getClassMap().put(clazz, info);
        Holder<ArgumentTypeInfo<?, ?>> holder = Registry.registerForHolder(BuiltInRegistries.COMMAND_ARGUMENT_TYPE, id, info);

        return () -> holder;
    }
}
