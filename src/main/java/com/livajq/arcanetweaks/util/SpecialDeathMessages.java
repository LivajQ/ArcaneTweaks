package com.livajq.arcanetweaks.util;

import com.estie.arcanecontent.ArcaneContent;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.livajq.arcanetweaks.ArcaneTweaks;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.Resource;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.*;

@OnlyIn(Dist.CLIENT)
public class SpecialDeathMessages {
    public static final Map<UUID, List<String>> DEATH_MESSAGES = new HashMap<>();
    public static final ResourceLocation DEATH_MESSAGES_FILE = new ResourceLocation(ArcaneContent.MODID, "special_death_messages/messages.json");
    
    public static void init() {
        DEATH_MESSAGES.clear();
        parseDeathMessages(DEATH_MESSAGES_FILE);
    }
    
    public static void parseDeathMessages(ResourceLocation rl) {
        try {
            Optional<Resource> resOpt = Minecraft.getInstance().getResourceManager().getResource(rl);
            if (resOpt.isEmpty()) return;
            
            try (InputStream stream = resOpt.get().open();
                 Reader reader = new InputStreamReader(stream)) {
                
                JsonElement root = JsonParser.parseReader(reader);
                
                if (!root.isJsonObject()) return;
                
                JsonObject obj = root.getAsJsonObject();
                
                for (Map.Entry<String, JsonElement> entry : obj.entrySet()) {
                    try {
                        UUID uuid = UUID.fromString(entry.getKey());
                        
                        if (!entry.getValue().isJsonArray()) continue;
                        
                        JsonArray arr = entry.getValue().getAsJsonArray();
                        List<String> list = new ArrayList<>();
                        
                        for (JsonElement e : arr) {
                            list.add(e.getAsString());
                        }
                        
                        DEATH_MESSAGES.put(uuid, list);
                        
                    } catch (IllegalArgumentException e) {
                        ArcaneTweaks.LOGGER.warn("Invalid UUID in death messages JSON: {}", entry.getKey());
                    }
                }
            }
            
        } catch (Exception e) {
            ArcaneTweaks.LOGGER.error("Failed to load death messages from {}", rl, e);
        }
    }
}