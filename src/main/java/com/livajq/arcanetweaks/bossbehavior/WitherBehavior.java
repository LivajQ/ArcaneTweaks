package com.livajq.arcanetweaks.bossbehavior;

import com.livajq.arcanetweaks.init.ArcaneSounds;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.entity.boss.wither.WitherBoss;

public class WitherBehavior extends BossBehavior<WitherBoss> {
    private static final SoundEvent[] PHASE_MUSIC = new SoundEvent[] {
            ArcaneSounds.WITHER_BOSS_PHASE_1.get(),
            ArcaneSounds.WITHER_BOSS_PHASE_2.get(),
            ArcaneSounds.WITHER_BOSS_PHASE_3.get(),
    };
    
    public WitherBehavior() {
        super(3);
    }
    
    public void onPhaseTick(WitherBoss boss, int phase) {
        //System.out.println("Client: " + boss.level().isClientSide + " Phase: " + phase);
    }
    
    @Override
    public void onPhaseChange(WitherBoss boss, int newPhase, int oldPhase, boolean firstTime) {
        //probably some sounds effects/events for phase changes
    }
    
    @Override
    public SoundEvent getPhaseMusic(WitherBoss boss) {
        System.out.println("Client: " + boss.level().isClientSide + " Phase: " + getPhase(boss));
        return PHASE_MUSIC[getPhase(boss) - 1];
    }
}