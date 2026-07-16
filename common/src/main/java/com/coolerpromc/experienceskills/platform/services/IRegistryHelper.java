package com.coolerpromc.experienceskills.platform.services;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.platform.util.CreativeTabOutput;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import com.mojang.brigadier.arguments.ArgumentType;
import net.minecraft.commands.synchronization.ArgumentTypeInfo;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.resources.Identifier;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;

public interface IRegistryHelper {
    default <T extends Block> RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func){
        return registerBlock(name, func, BlockBehaviour.Properties.of());
    }
    <T extends Block> RegistryHandler.Blocks<T> registerBlock(String name, Function<BlockBehaviour.Properties, T> func, BlockBehaviour.Properties p);
    default <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func){
        return registerItem(name, func, new Item.Properties());
    }
    <T extends Item> RegistryHandler.Items<T> registerItem(String name, Function<Item.Properties, T> func, Item.Properties p);
    <T extends Entity> RegistryHandler.Entities<T> registerEntity(String name, EntityType.EntityFactory<T> factory, MobCategory category, UnaryOperator<EntityType.Builder<T>> builder);
    RegistryHandler<CreativeModeTab, CreativeModeTab> registerCreativeTab(String name, Supplier<ItemStack> icon, Component title, BiConsumer<CreativeTabOutput, CreativeModeTab.ItemDisplayParameters> entries);
    <T> RegistryHandler.Components<T> registerDataComponent(String name, UnaryOperator<DataComponentType.Builder<T>> builder);
    <T> RegistryHandler<EntityDataSerializer<?>, EntityDataSerializer<T>> registerEntityDataSerializer(String name, StreamCodec<? super RegistryFriendlyByteBuf, T> streamCodec);
    <A extends ArgumentType<?>, T extends ArgumentTypeInfo.Template<A>> RegistryHandler<ArgumentTypeInfo<?, ?>, ArgumentTypeInfo<A, T>> registerArgumentTypeInfo(String name, Class<A> clazz, ArgumentTypeInfo<A, T> info);
    default RegistryHandler<Identifier, Identifier> registerStat(String name){
        return registerStat(Constants.MODID, name);
    }
    RegistryHandler<Identifier, Identifier> registerStat(String namespace, String name);
    default RegistryHandler<Attribute, Attribute> registerAttribute(String name, Attribute attribute){
        return registerAttribute(Constants.MODID, name, attribute);
    }
    RegistryHandler<Attribute, Attribute> registerAttribute(String namespace, String name, Attribute attribute);
    List<RegistryHandler<Attribute, Attribute>> getRegisteredAttributes();

    static ResourceKey<Block> blockKey(String name) {
        return ResourceKey.create(Registries.BLOCK, Constants.id(name));
    }
    static ResourceKey<Item> itemKey(String name) {
        return ResourceKey.create(Registries.ITEM, Constants.id(name));
    }
    static ResourceKey<EntityType<?>> entityKey(String name) {
        return ResourceKey.create(Registries.ENTITY_TYPE, Constants.id(name));
    }
}
