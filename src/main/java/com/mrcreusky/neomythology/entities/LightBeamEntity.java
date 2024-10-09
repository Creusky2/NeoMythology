// package com.mrcreusky.neomythology.entities;

// import net.minecraft.world.entity.EntityType;
// import net.minecraft.world.entity.projectile.Projectile;
// import net.minecraft.world.level.Level;
// import net.minecraft.world.phys.EntityHitResult;

// import net.minecraft.network.syncher.EntityDataAccessor;
// import net.minecraft.network.syncher.EntityDataSerializers;
// import net.minecraft.network.syncher.SynchedEntityData;
// import net.minecraft.network.syncher.SynchedEntityData.Builder;



// public class LightBeamEntity extends Projectile {
//     private float power;
//     private static final EntityDataAccessor<Float> DATA_POWER = SynchedEntityData.defineId(LightBeamEntity.class, EntityDataSerializers.FLOAT);

//     public LightBeamEntity(EntityType<? extends Projectile> type, Level level, float power) {
//         super(type, level);
//         this.power = power;
//     }

//     @Override
//     protected void defineSynchedData(SynchedEntityData.Builder builder) {
//         this.entityData.set(DATA_POWER, this.power);
//     }

//     // @Override
//     // protected void onEntityHit(EntityHitResult result) {
//     //     // Logique pour infliger des dégâts aux entités touchées par le rayon
//     // }
// }