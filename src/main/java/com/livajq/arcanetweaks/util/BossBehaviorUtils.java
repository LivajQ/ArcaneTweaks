package com.livajq.arcanetweaks.util;

import com.estie.arcanecontent.common.capability.ArcaneCapabilities;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.levelgen.Heightmap;

public class BossBehaviorUtils {
    
    public static <T extends Mob> void spawnMinions(LivingEntity boss, EntityType<T> type, int count, double radius) {
        Level level = boss.level();
        if (level.isClientSide) return;
        
        int spawned = 0;
        int attempts = 0;
        int maxAttempts = 100;
        
        while (spawned < count && attempts < maxAttempts) {
            attempts++;
            
            double angle = level.random.nextDouble() * Math.PI * 2;
            double x = boss.getX() + radius * Math.cos(angle);
            double z = boss.getZ() + radius * Math.sin(angle);
            
            BlockPos base = BlockPos.containing(x, boss.getY(), z);
            BlockPos safe = level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING_NO_LEAVES, base);
            
            T minion = type.create(level);
            if (minion == null) continue;
            
            minion.moveTo(
                    safe.getX() + 0.5,
                    safe.getY(),
                    safe.getZ() + 0.5,
                    level.random.nextFloat() * 360F,
                    0F
            );
            
            minion.getCapability(ArcaneCapabilities.BOSS_MINION)
                    .ifPresent(cap -> cap.setBoss(boss));
            
            minion.setPersistenceRequired();
            
            if (level.addFreshEntity(minion)) spawned++;
        }
    }
}

