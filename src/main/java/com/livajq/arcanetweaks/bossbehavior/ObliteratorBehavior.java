package com.livajq.arcanetweaks.bossbehavior;

import com.estie.arcanecontent.common.capability.ArcaneCapabilities;
import net.miauczel.legendary_monsters.entity.AnimatedMonster.IAnimatedBoss.TheObliterator.TheObliteratorEntity;
import net.miauczel.legendary_monsters.entity.ModEntities;
import net.minecraft.ChatFormatting;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.ClipContext;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;
import net.minecraft.world.scores.PlayerTeam;
import net.minecraft.world.scores.Scoreboard;
import java.util.List;

public class ObliteratorBehavior extends BossBehavior<TheObliteratorEntity> {
    
    public ObliteratorBehavior() {
        super(1);
    }
    
    @Override
    public void onPhaseTick(TheObliteratorEntity obliterator, int phase) {
        
        if (isClone(obliterator)) {
            obliterator.getCapability(ArcaneCapabilities.BOSS_MINION)
                    .ifPresent(cap -> {
                        obliterator.setBossBarVisible(false);
                        LivingEntity boss = cap.getBoss();
                        if (obliterator.tickCount < 100) return;
                        if (boss == null || boss.isRemoved() || boss.isDeadOrDying()) obliterator.discard();
                    });
            return;
        }
        
        if (obliterator.tickCount % 20 != 0) return;
        //nbt instead of onPhaseChange for a bit better sync with boss' actual phases
        CompoundTag data = obliterator.getPersistentData();
        int p = obliterator.getPhase();
        
        if (p == 2 && !data.getBoolean("ArcaneTweaks_P2Flag")) {
            spawnClone(obliterator);
            data.putBoolean("ArcaneTweaks_P2Flag", true);
        }
        
        if (p == 3 && !data.getBoolean("ArcaneTweaks_P3Flag")) {
            spawnClone(obliterator);
            data.putBoolean("ArcaneTweaks_P3Flag", true);
        }
    }
    
    @Override
    public HurtResult onHurt(TheObliteratorEntity boss, DamageSource src, float amount) {
        if (isClone(boss)) return HurtResult.cancel();
        return HurtResult.pass();
    }
    
    @Override
    public void onBossDied(TheObliteratorEntity boss) {
        ServerLevel level = (ServerLevel) boss.level();
        
        List<Mob> minions = level.getEntitiesOfClass(Mob.class, boss.getBoundingBox().inflate(256), mob ->
                mob.getCapability(ArcaneCapabilities.BOSS_MINION)
                        .map(cap -> boss.equals(cap.getBoss()))
                        .orElse(false)
        );
        
        for (Mob mob : minions) mob.discard();
    }
    
    @Override
    public void onMinionAdded(TheObliteratorEntity boss, Mob clone) {
        if (clone.level().isClientSide) return;
        
        ServerLevel level = (ServerLevel) clone.level();
        Scoreboard scoreboard = level.getScoreboard();
        
        PlayerTeam team = scoreboard.getPlayerTeam("obliterator_clones");
        if (team == null) {
            team = scoreboard.addPlayerTeam("obliterator_clones");
            team.setColor(ChatFormatting.GREEN);
        }
        
        scoreboard.addPlayerToTeam(clone.getStringUUID(), team);
        clone.setGlowingTag(true);
    }
    
    private void spawnClone(TheObliteratorEntity boss) {
        ServerLevel level = (ServerLevel) boss.level();
        
        TheObliteratorEntity clone = ModEntities.THE_OBLITERATOR.get().create(level);
        if (clone == null) return;
        
        double radius = 6.0;
        int maxTries = 16;
        
        BlockPos spawnPos = null;
        
        for (int i = 0; i < maxTries; i++) {
            double angle = level.random.nextDouble() * Math.PI * 2;
            double x = boss.getX() + radius * Math.cos(angle);
            double z = boss.getZ() + radius * Math.sin(angle);
            
            //find ground under that X/Z
            BlockPos.MutableBlockPos cursor = new BlockPos.MutableBlockPos(
                    x, boss.getY() + 10, z
            );
            
            while (cursor.getY() > level.getMinBuildHeight() && level.isEmptyBlock(cursor)) {
                cursor.move(Direction.DOWN);
            }
            cursor.move(Direction.UP);
            BlockPos pos = cursor.immutable();
            
            //raytrace from boss eye to the potential spawn spot
            Vec3 from = boss.getEyePosition();
            Vec3 to = new Vec3(pos.getX() + 0.5, pos.getY() + 0.5, pos.getZ() + 0.5);
            
            ClipContext ctx = new ClipContext(
                    from, to,
                    ClipContext.Block.COLLIDER,
                    ClipContext.Fluid.NONE,
                    boss
            );
            BlockHitResult hit = level.clip(ctx);
            
            boolean clear = hit.getType() == HitResult.Type.MISS;
            
            if (clear) {
                spawnPos = pos;
                break;
            }
        }
        
        //hard fallback: just put it next to the boss
        if (spawnPos == null) spawnPos = boss.blockPosition().offset(1, 0, 0);
        
        clone.moveTo(
                spawnPos.getX() + 0.5,
                spawnPos.getY(),
                spawnPos.getZ() + 0.5,
                level.random.nextFloat() * 360F,
                0F
        );
        
        clone.getCapability(ArcaneCapabilities.BOSS_MINION)
                .ifPresent(cap -> cap.setBoss(boss));
        
        clone.setPersistenceRequired();
        
        clone.getAttribute(Attributes.MAX_HEALTH).setBaseValue(2137);
        clone.setHealth(clone.getMaxHealth());
        clone.setInvulnerable(true);
        clone.getAttribute(Attributes.ATTACK_DAMAGE).setBaseValue(
                boss.getAttributeValue(Attributes.ATTACK_DAMAGE) / 2
        );
        clone.getAttribute(Attributes.MOVEMENT_SPEED).setBaseValue(
                boss.getAttributeValue(Attributes.MOVEMENT_SPEED) / 2
        );
        
        clone.setCustomName(Component.literal("Annihilation Clone"));
        
        LivingEntity target = boss.getTarget();
        if (target != null) clone.setTarget(target);
        
        level.addFreshEntity(clone);
        clone.setPhase(2);
    }
    
    private boolean isClone(TheObliteratorEntity entity) {
        return entity.getCapability(ArcaneCapabilities.BOSS_MINION)
                .map(cap -> {
                    LivingEntity boss = cap.getBoss();
                    return boss != null && !boss.getUUID().equals(entity.getUUID());
                })
                .orElse(false);
    }
}