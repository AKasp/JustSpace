package net.vildulv.minecraft.justspace.mob.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.util.RandomSource;
import net.minecraft.util.TimeUtil;
import net.minecraft.util.valueproviders.UniformInt;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.ai.attributes.AttributeSupplier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.ai.goal.FloatGoal;
import net.minecraft.world.entity.ai.goal.Goal;
import net.minecraft.world.entity.ai.goal.MeleeAttackGoal;
import net.minecraft.world.entity.ai.goal.RandomLookAroundGoal;
import net.minecraft.world.entity.ai.goal.target.HurtByTargetGoal;
import net.minecraft.world.entity.ai.goal.target.ResetUniversalAngerTargetGoal;
import net.minecraft.world.entity.ai.navigation.FlyingPathNavigation;
import net.minecraft.world.entity.ai.navigation.PathNavigation;
import net.minecraft.world.entity.ai.util.AirRandomPos;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.FlyingAnimal;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.LevelAccessor;
import net.minecraft.world.phys.Vec3;
import net.vildulv.minecraft.justspace.SoundRegister;

import javax.annotation.Nullable;
import java.util.EnumSet;
import java.util.UUID;

public class VoidMantaEntity extends Animal implements FlyingAnimal, NeutralMob {

    private static final EntityDataAccessor<Integer> DATA_REMAINING_ANGER_TIME;
    private static final UniformInt PERSISTENT_ANGER_TIME;

    private UUID persistentAngerTarget;

    public VoidMantaEntity(EntityType<? extends Animal> entityType, Level level) {
        super(entityType, level);
    }

    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        super.defineSynchedData(builder);
        builder.define(DATA_REMAINING_ANGER_TIME, 0);
    }

    public static boolean checkVoidMantaSpawnRules(EntityType<VoidMantaEntity> manta, LevelAccessor level, MobSpawnType spawnType, BlockPos pos, RandomSource random) {
        return spawnType == MobSpawnType.SPAWNER || level.getBlockState(pos).isAir();
    }

    @Override
    public boolean checkSpawnRules(LevelAccessor level, MobSpawnType spawnReason) {
        return level.getBlockState(this.blockPosition()).isAir();
        // return this.getWalkTargetValue(this.blockPosition(), level) >= 0.0F;
    }

    public static AttributeSupplier.Builder createAttributes() {
        return Mob.createMobAttributes()
                .add(Attributes.MAX_HEALTH, 50.0)
                .add(Attributes.MOVEMENT_SPEED, 0.3F)
                .add(Attributes.ATTACK_DAMAGE, 5.0)
                .add(Attributes.SCALE, 7.0);
    }


    @Override
    protected void registerGoals() {
        this.goalSelector.addGoal(0, new FloatGoal(this));
        this.goalSelector.addGoal(2, new RandomLookAroundGoal(this));
        this.goalSelector.addGoal(4, new WanderGoal());
        this.goalSelector.addGoal(6, new MeleeAttackGoal(this, (double) 1.2F, true));

        this.targetSelector.addGoal(3, (new HurtByTargetGoal(this, new Class[0])).setAlertOthers(new Class[0]));
        this.targetSelector.addGoal(8, new ResetUniversalAngerTargetGoal(this, true));
    }

    @Nullable
    @Override
    public SoundEvent getAmbientSound() {
        return this.random.nextInt(10) != 0 ? null : SoundRegister.VOID_MANTA_IDLE.get();
    }

    @Override
    protected SoundEvent getHurtSound(DamageSource damageSource) {
        return SoundRegister.VOID_MANTA_IDLE.get();
    }

    @Override
    public boolean isFood(ItemStack itemStack) {
        return false;
    }

    @Override
    public @Nullable AgeableMob getBreedOffspring(ServerLevel serverLevel, AgeableMob ageableMob) {
        return null;
    }

    public int getRemainingPersistentAngerTime() {
        return (Integer) this.entityData.get(DATA_REMAINING_ANGER_TIME);
    }

    public void setRemainingPersistentAngerTime(int time) {
        this.entityData.set(DATA_REMAINING_ANGER_TIME, time);
    }

    @Nullable
    public UUID getPersistentAngerTarget() {
        return this.persistentAngerTarget;
    }

    public void setPersistentAngerTarget(@Nullable UUID target) {
        this.persistentAngerTarget = target;
    }

    public void startPersistentAngerTimer() {
        this.setRemainingPersistentAngerTime(PERSISTENT_ANGER_TIME.sample(this.random));
    }

    static {
        DATA_REMAINING_ANGER_TIME = SynchedEntityData.defineId(VoidMantaEntity.class, EntityDataSerializers.INT);
        PERSISTENT_ANGER_TIME = TimeUtil.rangeOfSeconds(20, 39);
    }

    @Override
    public boolean isFlying() {
        return true;
    }

    public int getUniqueFlapTickOffset() {
        return this.getId() * 3;
    }

    protected PathNavigation createNavigation(Level p_level) {
        FlyingPathNavigation flyingpathnavigation = new FlyingPathNavigation(this, p_level) {
            public boolean isStableDestination(BlockPos p_27947_) {
                return !this.level.getBlockState(p_27947_.below()).isAir();
            }
        };
        flyingpathnavigation.setCanOpenDoors(false);
        flyingpathnavigation.setCanFloat(true);
        flyingpathnavigation.setCanPassDoors(true);
        return flyingpathnavigation;
    }


    class WanderGoal extends Goal {

        WanderGoal() {
            this.setFlags(EnumSet.of(Goal.Flag.MOVE));
        }

        @Override
        public boolean canUse() {
            return VoidMantaEntity.this.navigation.isDone() && VoidMantaEntity.this.random.nextInt(10) == 0;
        }

        @Override
        public boolean canContinueToUse() {
            return VoidMantaEntity.this.navigation.isInProgress();
        }

        @Override
        public void start() {
            Vec3 vec3 = this.findPos();
            if (vec3 != null) {
            //    System.out.println(VoidMantaEntity.this + "Flying from " + VoidMantaEntity.this.position() + " to " + vec3);
                VoidMantaEntity.this.navigation.moveTo(VoidMantaEntity.this.navigation.createPath(BlockPos.containing(vec3), 1), 1.0);
            }
        }

        @javax.annotation.Nullable
        private Vec3 findPos() {
            Vec3 vec3 = VoidMantaEntity.this.getViewVector(0.0F);

            return AirRandomPos.getPosTowards(VoidMantaEntity.this, 20, 10, 10, vec3, (float) (Math.PI / 10));
        }
    }

}
