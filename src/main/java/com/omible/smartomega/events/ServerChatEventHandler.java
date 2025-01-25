package com.omible.smartomega.events;

import com.omible.smartomega.Config;
import com.omible.smartomega.SmartOmega;
import net.minecraft.network.chat.Component;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;

public class ServerChatEventHandler {

    @SubscribeEvent
    public void onChatMessage(ServerChatEvent event) {
        if(!Config.tagNames) return;


        if(SmartOmega.server.getPlayerList().isOp(event.getPlayer().getGameProfile())){

            SmartOmega.server.getPlayerList().getPlayers().forEach(player -> {
                Component message = Component.Serializer.fromJson(String.format("[{\"text\":\"\\uE921\"},{\"text\":\" %s \", \"color\":\"#9253a9\", \"bold\":true}, {\"text\":\">> \", \"color\":\"#555555\"}, {\"text\":\"%s\"}]", event.getPlayer().getDisplayName().getString(), event.getMessage().getString()));
                assert message != null;
                player.sendSystemMessage(message);
            });


        } else {

            SmartOmega.server.getPlayerList().getPlayers().forEach(player -> {
                Component message = Component.Serializer.fromJson(String.format("[{\"text\":\"\\uE922\"},{\"text\":\" %s\", \"color\":\"#dddddd\", \"bold\":true}, {\"text\":\": \", \"color\":\"#555555\"}, {\"text\":\"%s\"}]", event.getPlayer().getDisplayName().getString(), event.getMessage().getString()));
                assert message != null;
                player.sendSystemMessage(message);
            });

        }

        event.setCanceled(true);
    }

}
