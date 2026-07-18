package com.livajq.arcanetweaks.client.audio;

import com.livajq.arcanetweaks.ArcaneTweaks;
import com.livajq.arcanetweaks.bossbehavior.BossBehavior;
import com.livajq.arcanetweaks.bossbehavior.BossBehaviorRegistry;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mod.EventBusSubscriber(modid = ArcaneTweaks.MODID, value = Dist.CLIENT)
public class BossMusicManager {
    //the "currently correct" track per boss id
    private static final Map<Integer, BossPhaseMusic> CURRENT = new HashMap<>();
    //old tracks that have been superseded but are still fading out
    private static final List<BossPhaseMusic> FADING = new ArrayList<>();
    
    @SubscribeEvent
    public static void onClientTick(TickEvent.ClientTickEvent event) {
        if (event.phase != TickEvent.Phase.END) return;
        ClientLevel level = Minecraft.getInstance().level;
        if (level == null) return;
        
        for (Entity e : level.entitiesForRendering()) {
            if (!(e instanceof LivingEntity boss)) continue;
            
            BossBehavior<LivingEntity> behavior = BossBehaviorRegistry.get(boss);
            int id = boss.getId();
            SoundEvent desired = null;
            
            if (behavior != null && boss.isAlive() && !boss.isDeadOrDying()) {
                desired = behavior.getPhaseMusic(boss);
            }
            
            BossPhaseMusic current = CURRENT.get(id);
            
            if (desired == null) {
                if (current != null) retire(id, current);
                continue;
            }
            
            if (current == null || current.getTrack() != desired) {
                if (current != null) retire(id, current);
                
                int phase = behavior.getPhase(boss) - 1;
                BossPhaseMusic fresh = new BossPhaseMusic(desired, boss, phase);
                CURRENT.put(id, fresh);
                Minecraft.getInstance().getSoundManager().play(fresh);
            }
        }
        
        FADING.removeIf(instance -> {
            boolean done = instance.isStopped();
            if (!done && !Minecraft.getInstance().getSoundManager().isActive(instance)) {
                Minecraft.getInstance().getSoundManager().play(instance);
            }
            return done;
        });
    }
    
    private static void retire(int id, BossPhaseMusic instance) {
        instance.retire();
        CURRENT.remove(id);
        FADING.add(instance);
    }
}