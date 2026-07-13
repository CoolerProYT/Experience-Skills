package com.coolerpromc.experienceskills.entity.custom;

import com.coolerpromc.experienceskills.Constants;
import com.coolerpromc.experienceskills.api.type.ExperienceType;
import com.coolerpromc.experienceskills.config.ModCommonConfig;
import com.coolerpromc.experienceskills.data.attachment.helper.AttachmentKey;
import com.coolerpromc.experienceskills.entity.ModEntities;
import com.coolerpromc.experienceskills.platform.Services;
import com.coolerpromc.experienceskills.platform.util.RegistryHandler;
import com.coolerpromc.experienceskills.util.XpMath;
import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.resources.Identifier;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvents;
import net.minecraft.sounds.SoundSource;
import net.minecraft.tags.FluidTags;
import net.minecraft.util.ExtraCodecs;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeInstance;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.entity.EntityTypeTest;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.phys.shapes.Shapes;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.jspecify.annotations.NonNull;
import org.jspecify.annotations.Nullable;

import java.util.List;

public class ModExperienceOrb extends Entity {
    protected static final EntityDataAccessor<Integer> DATA_VALUE = SynchedEntityData.defineId(ModExperienceOrb.class, EntityDataSerializers.INT);
    private int age = 0;
    private int health = 5;
    private int count = 1;
    private @Nullable Player followingPlayer;
    private final InterpolationHandler interpolation = new InterpolationHandler(this);
    private final ExperienceType experienceType;

    public ModExperienceOrb(EntityType<? extends ModExperienceOrb> type, Level level, ExperienceType experienceType) {
        super(type, level);
        this.experienceType = experienceType;
    }

    protected void unstuckIfPossible(double maxDistance) {
        Vec3 center = this.position().add(0.0, this.getBbHeight() / 2.0, 0.0);
        VoxelShape allowedCenters = Shapes.create(AABB.ofSize(center, maxDistance, maxDistance, maxDistance));
        this.level()
            .findFreePosition(this, allowedCenters, center, this.getBbWidth(), this.getBbHeight(), this.getBbWidth())
            .ifPresent(pos -> this.setPos(pos.add(0.0, -this.getBbHeight() / 2.0, 0.0)));
    }

    @Override
    protected Entity.@NonNull MovementEmission getMovementEmission() {
        return Entity.MovementEmission.NONE;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder entityData) {
        entityData.define(DATA_VALUE, 0);
    }

    @Override
    protected double getDefaultGravity() {
        return 0.03;
    }

    @Override
    public void tick() {
        this.interpolation.interpolate();
        if (this.firstTick && this.level().isClientSide()) {
            this.firstTick = false;
        } else {
            super.tick();
            boolean colliding = !this.level().noCollision(this.getBoundingBox());
            if (this.isEyeInFluid(FluidTags.WATER)) {
                this.setUnderwaterMovement();
            } else if (!colliding) {
                this.applyGravity();
            }

            if (this.level().getFluidState(this.blockPosition()).is(FluidTags.LAVA)) {
                this.setDeltaMovement(
                    (this.random.nextFloat() - this.random.nextFloat()) * 0.2F, 0.2F, (this.random.nextFloat() - this.random.nextFloat()) * 0.2F
                );
            }

            if (this.tickCount % 20 == 1) {
                this.scanForMerges();
            }

            this.followNearbyPlayer();
            if (this.followingPlayer == null && !this.level().isClientSide() && colliding) {
                boolean nextColliding = !this.level().noCollision(this.getBoundingBox().move(this.getDeltaMovement()));
                if (nextColliding) {
                    this.moveTowardsClosestSpace(this.getX(), (this.getBoundingBox().minY + this.getBoundingBox().maxY) / 2.0, this.getZ());
                    this.needsSync = true;
                }
            }

            double fallSpeed = this.getDeltaMovement().y;
            this.move(MoverType.SELF, this.getDeltaMovement());
            this.applyEffectsFromBlocks();
            float friction = 0.98F;
            if (this.onGround()) {
                friction = this.level().getBlockState(this.getBlockPosBelowThatAffectsMyMovement()).getBlock().getFriction() * 0.98F;
            }

            this.setDeltaMovement(this.getDeltaMovement().scale(friction));
            if (this.verticalCollisionBelow && fallSpeed < -this.getGravity()) {
                this.setDeltaMovement(new Vec3(this.getDeltaMovement().x, -fallSpeed * 0.4, this.getDeltaMovement().z));
            }

            this.age++;
            if (this.age >= 6000) {
                this.discard();
            }
        }
    }

    private void followNearbyPlayer() {
        if (this.followingPlayer == null || this.followingPlayer.isSpectator() || this.followingPlayer.distanceToSqr(this) > 64.0) {
            Player nearestPlayer = this.level().getNearestPlayer(this, 8.0);
            if (nearestPlayer != null && !nearestPlayer.isSpectator() && !nearestPlayer.isDeadOrDying()) {
                this.followingPlayer = nearestPlayer;
            } else {
                this.followingPlayer = null;
            }
        }

        if (this.followingPlayer != null) {
            Vec3 delta = new Vec3(
                this.followingPlayer.getX() - this.getX(),
                this.followingPlayer.getY() + this.followingPlayer.getEyeHeight() / 2.0 - this.getY(),
                this.followingPlayer.getZ() - this.getZ()
            );
            double length = delta.lengthSqr();
            double power = 1.0 - Math.sqrt(length) / 8.0;
            this.setDeltaMovement(this.getDeltaMovement().add(delta.normalize().scale(power * power * 0.1)));
        }
    }

    @Override
    public @NonNull BlockPos getBlockPosBelowThatAffectsMyMovement() {
        return this.getOnPos(0.999999F);
    }

    private void scanForMerges() {
        if (this.level() instanceof ServerLevel) {
            for (ModExperienceOrb orb : this.level().getEntities(EntityTypeTest.forClass(ModExperienceOrb.class), this.getBoundingBox().inflate(0.5), this::canMerge)) {
                this.merge(orb);
            }
        }
    }

    public static void award(ServerLevel level, Vec3 pos, int amount, ExperienceType type) {
        awardWithDirection(level, pos, Vec3.ZERO, amount, type);
    }

    public static void awardWithDirection(ServerLevel level, Vec3 pos, Vec3 roughDirection, int amount, ExperienceType type) {
        while (amount > 0) {
            int newCount = getExperienceValue(amount);
            amount -= newCount;
            ModExperienceOrb orb = ModEntities.byName(type.getSerializedName() + "_experience_orb").get().create(level, EntitySpawnReason.TRIGGERED);
            if (!tryMergeToExisting(level, pos, newCount) && orb.canAward()) {
                orb.spawn(level, pos, roughDirection, newCount);
            }
        }
    }

    private static boolean tryMergeToExisting(ServerLevel level, Vec3 pos, int value) {
        AABB box = AABB.ofSize(pos, 1.0, 1.0, 1.0);
        int id = level.getRandom().nextInt(40);
        List<ModExperienceOrb> orbs = level.getEntities(EntityTypeTest.forClass(ModExperienceOrb.class), box, orbx -> canMerge(orbx, id, value));
        if (!orbs.isEmpty()) {
            ModExperienceOrb orb = orbs.getFirst();
            orb.count++;
            orb.age = 0;
            return true;
        } else {
            return false;
        }
    }

    private boolean canMerge(ModExperienceOrb orb) {
        return orb != this && canMerge(orb, this.getId(), this.getValue());
    }

    private static boolean canMerge(ModExperienceOrb orb, int id, int value) {
        return !orb.isRemoved() && (orb.getId() - id) % 40 == 0 && orb.getValue() == value;
    }

    private void merge(ModExperienceOrb orb) {
        this.count = this.count + orb.count;
        this.age = Math.min(this.age, orb.age);
        orb.discard();
    }

    private void setUnderwaterMovement() {
        Vec3 movement = this.getDeltaMovement();
        this.setDeltaMovement(movement.x * 0.99F, Math.min(movement.y + 5.0E-4F, 0.06F), movement.z * 0.99F);
    }

    @Override
    protected void doWaterSplashEffect() {
    }

    @Override
    public final boolean hurtClient(@NonNull DamageSource source) {
        return !this.isInvulnerableToBase(source);
    }

    @Override
    public final boolean hurtServer(@NonNull ServerLevel level, @NonNull DamageSource source, float damage) {
        if (this.isInvulnerableToBase(source)) {
            return false;
        } else {
            this.markHurt();
            this.health = (int)(this.health - damage);
            if (this.health <= 0) {
                this.discard();
            }

            return true;
        }
    }

    @Override
    protected void addAdditionalSaveData(ValueOutput output) {
        output.putShort("Health", (short)this.health);
        output.putShort("Age", (short)this.age);
        output.putShort("Value", (short)this.getValue());
        output.putInt("Count", this.count);
    }

    @Override
    protected void readAdditionalSaveData(ValueInput input) {
        this.health = input.getShortOr("Health", (short)5);
        this.age = input.getShortOr("Age", (short)0);
        this.setValue(input.getShortOr("Value", (short)0));
        this.count = input.read("Count", ExtraCodecs.POSITIVE_INT).orElse(1);
    }

    @Override
    public void playerTouch(@NonNull Player player) {
        if (player instanceof ServerPlayer serverPlayer && !reachedMaxLevel(serverPlayer, this.experienceType.getKey(), this.experienceType.getMaxLevel())) {
            if (player.takeXpDelay == 0) {
                player.takeXpDelay = 2;
                this.addExperience(serverPlayer, this.getValue(), this.getExperienceType().getKey());

                this.count--;
                if (this.count == 0) {
                    this.discard();
                }
            }
        }
    }

    public int getValue() {
        return this.entityData.get(DATA_VALUE);
    }

    private void setValue(int value) {
        this.entityData.set(DATA_VALUE, value);
    }

    public ExperienceType getExperienceType(){
        return this.experienceType;
    }

    public int getIcon() {
        int value = this.getValue();
        if (value >= 2477) {
            return 10;
        } else if (value >= 1237) {
            return 9;
        } else if (value >= 617) {
            return 8;
        } else if (value >= 307) {
            return 7;
        } else if (value >= 149) {
            return 6;
        } else if (value >= 73) {
            return 5;
        } else if (value >= 37) {
            return 4;
        } else if (value >= 17) {
            return 3;
        } else if (value >= 7) {
            return 2;
        } else {
            return value >= 3 ? 1 : 0;
        }
    }

    public static int getExperienceValue(int maxValue) {
        if (maxValue >= 2477) {
            return 2477;
        } else if (maxValue >= 1237) {
            return 1237;
        } else if (maxValue >= 617) {
            return 617;
        } else if (maxValue >= 307) {
            return 307;
        } else if (maxValue >= 149) {
            return 149;
        } else if (maxValue >= 73) {
            return 73;
        } else if (maxValue >= 37) {
            return 37;
        } else if (maxValue >= 17) {
            return 17;
        } else if (maxValue >= 7) {
            return 7;
        } else {
            return maxValue >= 3 ? 3 : 1;
        }
    }

    @Override
    public boolean isAttackable() {
        return false;
    }

    @Override
    public @NonNull SoundSource getSoundSource() {
        return SoundSource.AMBIENT;
    }

    @Override
    public InterpolationHandler getInterpolation() {
        return this.interpolation;
    }

    public void addExperience(ServerPlayer entity, int amount, AttachmentKey<Integer> key){
        int original = Services.ATTACHMENT.get(entity, key);
        int newAmount = original + amount;
        Services.ATTACHMENT.set(entity, key, newAmount);
        float vol = newAmount > 30 ? 1.0F : newAmount / 30.0F;

        if (canAward()){
            entity.level().playSound(null, entity.getX(), entity.getY(), entity.getZ(), SoundEvents.PLAYER_LEVELUP, entity.getSoundSource(), vol * 0.75F, 1.0F);
            updateSkillStatus(entity, key);
        }
    }

    public void spawn(ServerLevel level, Vec3 pos, Vec3 roughly, int value){
        if (!level.isClientSide()) {
            this.setYRot(this.random.nextFloat() * 360.0F);
            Vec3 randomMovement = new Vec3(
                (this.random.nextDouble() * 0.2 - 0.1) * 2.0, this.random.nextDouble() * 0.2 * 2.0, (this.random.nextDouble() * 0.2 - 0.1) * 2.0
            );
            if (roughly.lengthSqr() > 0.0 && roughly.dot(randomMovement) < 0.0) {
                randomMovement = randomMovement.scale(-1.0);
            }

            double size = this.getBoundingBox().getSize();
            this.setPos(pos.add(roughly.normalize().scale(size * 0.5)));
            this.setDeltaMovement(randomMovement);
            if (!level.noCollision(this.getBoundingBox())) {
                this.unstuckIfPossible(size);
            }
        }

        this.setValue(value);

        level.addFreshEntity(this);
    }

    public void updateSkillStatus(ServerPlayer entity, AttachmentKey<Integer> key){
        int level = getLevel(entity, key);
        double extraSpeed = level * ModCommonConfig.getConfigByPath(getExperienceType().getSerializedName()).get().incrementPerLevel();

        AttributeInstance attr = entity.getAttribute(getExperienceType().getAttributeHolder());
        if (attr != null){
            Identifier id = Constants.id(getExperienceType().getSerializedName() + "_skill_bonus");
            AttributeModifier modifier = new AttributeModifier(id, extraSpeed, getExperienceType().operation());
            attr.removeModifier(id);
            attr.addOrUpdateTransientModifier(modifier);
            getExperienceType().onExperiencePointChange(entity, level);

            if (!enabled()){
                attr.removeModifier(id);
            }
        }
    }

    public boolean canAward(){
        return enabled();
    }

    public boolean enabled(){
        return getExperienceType().isEnabled();
    }

    public static int getLevel(LivingEntity entity, AttachmentKey<Integer> key) {
        int points = Services.ATTACHMENT.get(entity, key);
        return XpMath.getLevel(points);
    }

    public static float getProgress(LivingEntity entity, AttachmentKey<Integer> key) {
        int points = Services.ATTACHMENT.get(entity, key);
        return XpMath.getProgress(points);
    }

    public static int getXpNeededForNextLevel(LivingEntity entity, AttachmentKey<Integer> key){
        int level = getLevel(entity, key);
        return XpMath.getXpNeededForNextLevel(level);
    }

    public static void setLevel(ServerPlayer entity, int level, AttachmentKey<Integer> key){
        int points = XpMath.getTotalForLevel(level);
        Services.ATTACHMENT.set(entity, key, points);
        updateAllSkillStatus(entity);
    }

    public static void setPoints(ServerPlayer entity, int points, AttachmentKey<Integer> key){
        int level = getLevel(entity, key);
        int levelXpPoints = XpMath.getTotalForLevel(level);
        Services.ATTACHMENT.set(entity, key, levelXpPoints + points);
        updateAllSkillStatus(entity);
    }

    public static void giveExperiencePoints(ServerPlayer player, Integer integer, ExperienceType experienceType) {
        AttachmentKey<Integer> key = experienceType.getKey();
        int original = Services.ATTACHMENT.get(player, key);
        int newAmount = original + integer;
        Services.ATTACHMENT.set(player, key, newAmount);
        updateAllSkillStatus(player);
    }

    public static void giveExperienceLevels(ServerPlayer player, Integer integer, ExperienceType experienceType) {
        AttachmentKey<Integer> key = experienceType.getKey();
        int level = getLevel(player, key);
        int targetLevel = level + integer;
        int currentTotalXp = Services.ATTACHMENT.get(player, key);
        int targetTotalXp = XpMath.getTotalForLevel(targetLevel);
        giveExperiencePoints(player, targetTotalXp - currentTotalXp, experienceType);
    }

    public static int getCurrentLevelPoints(ServerPlayer player, ExperienceType experienceType){
        AttachmentKey<Integer> key = experienceType.getKey();
        int currentTotalXp = Services.ATTACHMENT.get(player, key);
        int levelXp = XpMath.getTotalForLevel(getLevel(player, key));

        return currentTotalXp - levelXp;
    }

    public static void updateAllSkillStatus(ServerPlayer player){
        for (RegistryHandler.Entities<? extends ModExperienceOrb> entities : ModEntities.ALL){
            ModExperienceOrb orb = entities.get().create(player.level(), EntitySpawnReason.LOAD);
            if (orb != null){
                orb.updateSkillStatus(player, orb.getExperienceType().getKey());
            }
        }
    }

    public static boolean reachedMaxLevel(ServerPlayer player, AttachmentKey<Integer> key, int maxLevel){
        int level = getLevel(player, key);
        return level >= maxLevel;
    }
}
