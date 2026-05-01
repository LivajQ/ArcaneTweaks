package com.livajq.arcanetweaks.util;

import com.livajq.arcanetweaks.Config;
import com.majruszsdifficulty.gamestage.GameStage;
import net.minecraft.world.entity.player.Player;

public class ReskillableUtils {
    
    public static int getMaxLevelForGamestage(Player player) {
        GameStage stage = MajruszsUtils.getGameStage(player);
      
        return switch (stage.getId()) {
            case GameStage.NORMAL_ID -> Config.gamestageSkillCapNormal;
            case GameStage.EXPERT_ID -> Config.gamestageSkillCapExpert;
            case GameStage.MASTER_ID -> Config.gamestageSkillCapMaster;
            default -> 50;
        };
    }
}