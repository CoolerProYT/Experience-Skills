package com.coolerpromc.experienceskills.item.custom;

import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.data.component.ModDataComponents;
import com.coolerpromc.experienceskills.entity.custom.ModThrownExperienceBottle;
import net.minecraft.core.Direction;
import net.minecraft.core.Position;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.stats.Stats;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.Projectile;
import net.minecraft.world.item.ExperienceBottleItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;

public class ModExperienceBottle extends ExperienceBottleItem {
    public ModExperienceBottle(Properties properties) {
        super(properties);
    }

    @Override
    public InteractionResult use(Level level, Player player, InteractionHand hand) {
        ItemStack itemStack = player.getItemInHand(hand);
        level.playSound(
            null,
            player.getX(),
            player.getY(),
            player.getZ(),
            SoundEvents.EXPERIENCE_BOTTLE_THROW,
            SoundSource.NEUTRAL,
            0.5F,
            0.4F / (level.getRandom().nextFloat() * 0.4F + 0.8F)
        );
        if (level instanceof ServerLevel serverLevel) {
            ExperienceType experienceType = itemStack.get(ModDataComponents.EXPERIENCE_TYPE.get());
            Projectile.spawnProjectileFromRotation((level1, entity, itemStack1) -> new ModThrownExperienceBottle(level1, entity, itemStack1, experienceType), serverLevel, itemStack, player, -20.0F, 0.7F, 1.0F);
        }

        player.awardStat(Stats.ITEM_USED.get(this));
        itemStack.consume(1, player);
        return InteractionResult.SUCCESS;
    }

    @Override
    public Projectile asProjectile(Level level, Position position, ItemStack itemStack, Direction direction) {
        ExperienceType experienceType = itemStack.get(ModDataComponents.EXPERIENCE_TYPE.get());
        return new ModThrownExperienceBottle(level, position.x(), position.y(), position.z(), itemStack, experienceType);
    }

    @Override
    public Component getName(ItemStack itemStack) {
        ExperienceType experienceType = itemStack.get(ModDataComponents.EXPERIENCE_TYPE.get());
        if (experienceType != null){
            return Component.translatable("skill.experienceskills." + experienceType.getSerializedName()).append(" ").append(super.getName(itemStack));
        }
        return super.getName(itemStack);
    }
}
