package com.livajq.arcanetweaks.bossbehavior;

import com.livajq.arcanetweaks.common.capability.ArcaneCapabilities;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.util.*;

public abstract class BossBehavior<T extends LivingEntity> {
    
    public final int phaseCount;
    public final double[] thresholds;
    public static final Map<LivingEntity, Set<UUID>> MINIONS = new WeakHashMap<>();
    
    protected BossBehavior(int phaseCount) {
        this.phaseCount = phaseCount;
        this.thresholds = phaseThresholds();
    }
    
    public void onPhaseTick(T boss, int phase) {}
    public void onPhaseChange(T boss, int newPhase, int oldPhase, boolean firstTime) {}
    public HurtResult onHurt(T boss, DamageSource src, float amount) { return HurtResult.pass(); }
    public void onBossDied(T boss) {}
    public void onMinionAdded(T boss, Mob minion) {}
    public void onMinionDied(T boss, Mob minion) {}
    
    protected double[] phaseThresholds() {
        double[] thresholds = new double[phaseCount - 1];
        for (int i = 1; i < phaseCount; i++) {
            thresholds[i - 1] = 1.0 - (i / (double) phaseCount);
        }
        return thresholds;
    }
    
    public int getPhase(T boss) {
        double hpRatio = boss.getHealth() / boss.getMaxHealth();
        int phase = 1;
        for (int i = 0; i < thresholds.length; i++) {
            if (hpRatio <= thresholds[i]) phase = i + 2;
        }
        return phase;
    }
    
    public int getPhaseFromTag(T boss) {
        CompoundTag tag = boss.getPersistentData();
        return tag.contains("Arcane_BossPhase") ? tag.getInt("Arcane_BossPhase") : 1;
    }
    
    public SoundEvent getPhaseMusic(T boss) {
        return null;
    }
    
    public void reconcileMinions(T boss) {
        ServerLevel level = (ServerLevel) boss.level();
        
        AABB box = boss.getBoundingBox().inflate(128);
        
        List<Mob> mobs = level.getEntitiesOfClass(Mob.class, box, mob ->
                mob.getCapability(ArcaneCapabilities.BOSS_MINION)
                        .map(cap -> boss.equals(cap.getBoss()))
                        .orElse(false)
        );
        
        Set<UUID> set = MINIONS.computeIfAbsent(boss, b -> new HashSet<>());
        
        for (Mob mob : mobs) {
            if (set.add(mob.getUUID())) {
                onMinionAdded(boss, mob);
            }
        }
    }
}