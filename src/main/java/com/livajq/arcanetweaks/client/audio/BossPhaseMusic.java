package com.livajq.arcanetweaks.client.audio;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.sounds.AbstractTickableSoundInstance;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.sounds.SoundSource;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.LivingEntity;

public class BossPhaseMusic extends AbstractTickableSoundInstance {
    private static final int FADE_DURATION = 40;
    
    private LivingEntity boss;
    private final SoundEvent track;
    private final int phase;
    private int fadeTimer;
    private int ticksExisted;
    private int timeUntilFade;
    
    public BossPhaseMusic(SoundEvent track, LivingEntity boss, int phase) {
        super(track, SoundSource.RECORDS, boss.getRandom());
        this.boss = boss;
        this.track = track;
        this.phase = phase;
        this.attenuation = Attenuation.NONE;
        this.looping = true;
        this.delay = 0;
        this.x = boss.getX();
        this.y = boss.getY();
        this.z = boss.getZ();
        this.fadeTimer = FADE_DURATION / 2;
        this.volume = fadeFraction();
        this.timeUntilFade = 80;
    }
    
    public int getPhase() { return phase; }
    public SoundEvent getTrack() { return track; }
    
    public void retire() { this.boss = null; }
    
    private float fadeFraction() {
        return Mth.clamp(fadeTimer / (float) FADE_DURATION, 0.0F, 1.0F);
    }
    
    @Override
    public void tick() {
        if (boss != null && boss.isAlive() && !boss.isDeadOrDying()) {
            fadeTimer = Math.min(fadeTimer + 1, FADE_DURATION);
            timeUntilFade = 15;
            this.x = boss.getX();
            this.y = boss.getY();
            this.z = boss.getZ();
        } else {
            if (boss != null && !boss.isAlive()) timeUntilFade = 0;
            boss = null;
            if (timeUntilFade > 0) {
                --timeUntilFade;
            } else {
                fadeTimer = Math.max(fadeTimer - 1, 0);
            }
        }
        
        this.volume = fadeFraction();
        if (this.volume < 0.025F) this.stop();
        
        if (this.ticksExisted % 100 == 0) {
            Minecraft.getInstance().getMusicManager().stopPlaying();
            Minecraft.getInstance().getSoundManager().stop(null, SoundSource.MUSIC);
        }
        ++this.ticksExisted;
    }
}