package com.livajq.arcanetweaks.packet;

import com.livajq.arcanetweaks.common.item.BlockItemOfSilly;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class StartSillyRainbowEffectPacket {
    private final int duration;
    
    public StartSillyRainbowEffectPacket(int duration) {
        this.duration = duration;
    }
    
    public static void encode(StartSillyRainbowEffectPacket msg, FriendlyByteBuf buf) {
        buf.writeInt(msg.duration);
    }
    
    public static StartSillyRainbowEffectPacket decode(FriendlyByteBuf buf) {
        return new StartSillyRainbowEffectPacket(buf.readInt());
    }
    
    public static void handle(StartSillyRainbowEffectPacket msg, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            BlockItemOfSilly.ClientHandler.setRainbowTimer(msg.duration);
        });
        
        ctx.get().setPacketHandled(true);
    }
}
