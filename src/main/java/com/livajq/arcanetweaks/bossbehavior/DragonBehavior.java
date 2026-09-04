package com.livajq.arcanetweaks.bossbehavior;

import com.estie.arcanecontent.init.ArcaneDamageSources;
import com.estie.arcanecontent.init.ArcaneSounds;
import com.github.alexmodguy.alexscaves.AlexsCaves;
import com.github.alexmodguy.alexscaves.server.entity.ACEntityRegistry;
import com.github.alexmodguy.alexscaves.server.entity.item.NuclearExplosionEntity;
import com.github.alexthe666.iceandfire.entity.EntityDragonBase;
import com.livajq.arcanetweaks.Config;
import net.miauczel.legendary_monsters.Particle.custom.MovingTrailParticle;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.effect.MobEffectInstance;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.level.Level;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraftforge.registries.ForgeRegistries;

import java.awt.*;
import java.util.List;

public abstract class DragonBehavior<T extends EntityDragonBase> extends BossBehavior<T> {
    private static final String TAG_COUNTDOWN = "ArcaneTweaks_DragonNukeCountdown";
    private static final String TAG_PAUSE = "ArcaneTweaks_DragonNukePause";
    
    public DragonBehavior(int phaseCount) {
        super(phaseCount);
    }
    
    @Override
    public void onPhaseTick(EntityDragonBase boss, int phase) {
        if (boss.tickCount < 10) return;
        if (boss.getHealth() <= 0 || boss.getDragonStage() != 5 || boss.isTame()) return;
        CompoundTag tag = boss.getPersistentData();
        int countdown = tag.getInt(TAG_COUNTDOWN);
        int pause = tag.getInt(TAG_PAUSE);
        
        if (countdown > 0) {
            countdown--;
            if (countdown == 0) causeNuke(boss);
            
            stallMovement(boss);
            if (!boss.isFlying()) boss.setFlying(true);
            
            if (boss.tickCount % 5 == 0) spawnDragonPreviewLine(boss);
            
            tag.putInt(TAG_COUNTDOWN, countdown);
        }
        
        else if (pause > 0) {
            pause--;
            
            stallMovement(boss);
            if (boss.isFlying()) boss.setFlying(false);
            
            tag.putInt(TAG_PAUSE, pause);
        }
    }
    
    @Override
    public void onPhaseChange(EntityDragonBase boss, int newPhase, int oldPhase, boolean firstTime) {
        if (!firstTime || newPhase != 2) return;
        if (boss.getDragonStage() != 5 || boss.isTame()) return;
        if (boss.tickCount < 10) return;
        startNukeCharge(boss);
    }
    
    private void startNukeCharge(EntityDragonBase dragon) {
        CompoundTag tag = dragon.getPersistentData();
        tag.putInt(TAG_COUNTDOWN, 120);
        dragon.addEffect(new MobEffectInstance(MobEffects.LEVITATION, 120, 3, false, true));
        
        dragon.level().playSound(
                null,
                dragon.getX(),
                dragon.getY(),
                dragon.getZ(),
                getExplosionChargeSound(),
                SoundSource.HOSTILE,
                6.0f,
                1.0f
        );
    }
    
    private void causeNuke(EntityDragonBase dragon) {
        double x = dragon.getX();
        double y = dragon.getY();
        double z = dragon.getZ();
        Level level = dragon.level();
        alexNuke(dragon);
        
        AABB box = new AABB(
                x - 64, y - 64, z - 64,
                x + 64, y + 64, z + 64
        );
        
        List<LivingEntity> targets = level.getEntitiesOfClass(
                LivingEntity.class,
                box,
                entity -> {
                    
                    //Skip the dragon itself
                    if (entity == dragon) return false;
                    
                    //Configured immune mobs
                    ResourceLocation id = ForgeRegistries.ENTITY_TYPES.getKey(entity.getType());
                    if (id != null && Config.dragonNukeImmuneSet.contains(id.toString())) return false;
                    
                    //Shield check
                    if (entity instanceof Player player) {
                        if (player.isBlocking()) {
                            ItemStack shield = player.getUseItem();
                            
                            if (!shield.isEmpty()) {
                                
                                boolean unbreakable = shield.getTag() != null
                                        && shield.getTag().getBoolean("Unbreakable");
                                
                                return !unbreakable && shield.getMaxDamage() < 5000;
                            }
                        }
                    }
                    
                    return true;
                }
        );
        
        for (LivingEntity target : targets) {
            target.hurt(ArcaneDamageSources.vaporized(level), 1000.0F);
        }
        
        level.playSound(
                null,
                dragon.getX(),
                dragon.getY(),
                dragon.getZ(),
                ArcaneSounds.DRAGON_NUKE_EXPLODE.get(),
                SoundSource.HOSTILE,
                6.0f,
                1.0f
        );
        
        dragon.getPersistentData().putInt(TAG_PAUSE, 300);
    }
    
    private void stallMovement(EntityDragonBase dragon) {
        dragon.setDeltaMovement(0, dragon.getDeltaMovement().y, 0);
        if (dragon.isHovering()) dragon.setHovering(false);
        if (dragon.getTarget() != null) dragon.setTarget(null);
    }
    
    private void alexNuke(EntityDragonBase dragon) {
        Level level = dragon.level();
        
        Vec3 start = dragon.position();
        Vec3 end = start.subtract(0, 300, 0);
        
        BlockHitResult hit = level.clip(new ClipContext(
                start,
                end,
                ClipContext.Block.COLLIDER,
                ClipContext.Fluid.NONE,
                dragon
        ));

        Vec3 explosionPos = hit.getType() == HitResult.Type.BLOCK ? hit.getLocation() : start;

        NuclearExplosionEntity explosion = ACEntityRegistry.NUCLEAR_EXPLOSION.get().create(level);
        if (explosion == null) return;
        explosion.getPersistentData().putBoolean("ArcaneTweaks_DragonNuke", true);
        
        explosion.setPos(explosionPos.x, explosionPos.y, explosionPos.z);
        explosion.setSize(AlexsCaves.COMMON_CONFIG.nukeExplosionSizeModifier.get().floatValue());
        level.addFreshEntity(explosion);
    }
    
    private void spawnDragonPreviewLine(EntityDragonBase dragon) {
        ServerLevel level = (ServerLevel) dragon.level();
        
        Vec3 start = dragon.position();
        Vec3 end = start.subtract(0, 80, 0);
        
        int samples = 40;
        float width = 0.25f;
        float height = 0.25f;
        Color color = getExplosionColor();
        float r = color.getRed() / 255.0f;
        float g = color.getGreen() / 255.0f;
        float b = color.getBlue() / 255.0f;
        
        for (int i = 0; i < samples; i++) {
            float t = i / (float)samples;
            Vec3 pos = start.lerp(end, t);
            
            MovingTrailParticle.TrailData data = new MovingTrailParticle.TrailData(r, g, b, width, height);
            
            List<ServerPlayer> list = level.getServer().getPlayerList().getPlayers();
            
            for (ServerPlayer player : list) {
                level.sendParticles(
                        player,
                        data,
                        true,
                        pos.x, pos.y, pos.z,
                        1,
                        0, 0, 0,
                        0
                );
            }
        }
    }
    
    public abstract Color getExplosionColor();
    public abstract SoundEvent getExplosionChargeSound();
}