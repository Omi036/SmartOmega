package com.omible.smartomega.events;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.omible.smartomega.Config;
import com.omible.smartomega.SmartOmega;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.util.Objects;

public class PlayerJoinEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        ServerPlayer player = (ServerPlayer) event.getEntity();
        GameProfile profile = player.getGameProfile();
        PlayerList list = SmartOmega.server.getPlayerList();
        String welcomeMessage = Config.welcomeMessage;

        if(Config.deopOnJoin && list.isOp(profile)){
            list.deop(profile);
            LOGGER.info("Player {} was deoped because he joined.", player.getName());
        }

        if(!welcomeMessage.isEmpty()){
            String ping = String.valueOf(player.latency);
            String name = player.getName().getString();
            String ip = player.getIpAddress();
            String playerCount = String.valueOf(SmartOmega.server.getPlayerCount());

            Component message;

            welcomeMessage = welcomeMessage
                    .replaceAll("\\{ping}", ping)
                    .replaceAll("\\{name}", name)
                    .replaceAll("\\{ip}", ip)
                    .replaceAll("\\{count}", playerCount);

            try {
                message = Objects.requireNonNull(Component.Serializer.fromJson(welcomeMessage));
                player.sendSystemMessage(message);
            } catch (Exception e){
                LOGGER.error("Error sending welcome message, does it use a valid json format?");
            }
        }
    }

}
