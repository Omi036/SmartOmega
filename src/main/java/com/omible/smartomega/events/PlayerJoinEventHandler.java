package com.omible.smartomega.events;

import com.google.gson.JsonObject;
import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.omible.smartomega.Config;
import com.omible.smartomega.DiscordWebhook.EmbedObject;
import com.omible.smartomega.DiscordWebhook;
import com.omible.smartomega.ServerData;
import com.omible.smartomega.SmartOmega;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import org.slf4j.Logger;

import java.awt.*;
import java.time.Instant;
import java.util.Objects;

public class PlayerJoinEventHandler {
    private static final Logger LOGGER = LogUtils.getLogger();

    @SubscribeEvent
    public void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
        ServerPlayer player = (ServerPlayer) event.getEntity();
        GameProfile profile = player.getGameProfile();
        String playerIp = player.connection.getRemoteAddress().toString().split(":")[0].replace("/","");
        String playerName = profile.getName();
        PlayerList list = SmartOmega.server.getPlayerList();
        String welcomeMessage = Config.welcomeMessage;

        // T2 Security - OP Protection
        if(Config.deopOnJoin && list.isOp(profile)){
            list.deop(profile);
            LOGGER.info("Player {} was deoped because he joined.", player.getName());
        }


        // T1 Security - ImPersonation
        if(Config.ipSecurityEnabled){
            ServerData.ensureData("players.json");
            JsonObject players = ServerData.getJson("players.json");

            if(players.has(playerName)){
                JsonObject playerRecord = (JsonObject) players.get(playerName);
                String last_ip = playerRecord.get("last_ip").getAsString();
                int timeSafe = playerRecord.get("timesafe").getAsInt();
                long last_connection = playerRecord.get("last_connection").getAsLong();
                long eta_time = last_connection + timeSafe*1000L;

                // If timesafe is still valid and IP differs.
                if(System.currentTimeMillis() < eta_time && !last_ip.equals(playerIp)){
                    player.connection.disconnect(Component.literal(Config.kickMessage).withStyle(style -> style.withColor(0xff2222)));
                    LOGGER.error(String.format("[CRITICAL] Connection with IP %s tried to join to %s, but it differs on IP", playerIp, playerName));
                    return;
                }

                playerRecord.addProperty("last_ip", playerIp);
                playerRecord.addProperty("last_connection", System.currentTimeMillis());
                players.add(playerName, playerRecord);
                ServerData.saveJson("players.json", players);

            } else {
                JsonObject newPlayer = new JsonObject();
                newPlayer.addProperty("timesafe", Config.ipTimeSafe);
                newPlayer.addProperty("last_ip", playerIp);
                newPlayer.addProperty("last_connection", System.currentTimeMillis());

                players.add(playerName, newPlayer);
                ServerData.saveJson("players.json", players);
            }
        }


        // Send welcome message
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




        // Send discord webhook
        if(Config.webhooksEnabled && Config.webhooksClientEnabled){
            DiscordWebhook webhook = new DiscordWebhook(Config.webhookUrl);
            EmbedObject embed = new EmbedObject()
                    .setTitle(String.format("🟢 PlayerJoin %s", player.getDisplayName().getString()))
                    .setDescription(
                            "Player is now playing on the server\\n"
                            + String.format("- Name: `%s`\\n",player.getDisplayName().getString())
                            + String.format("- Position: `%03d %03d %03d`\\n", (int) player.position().x, (int) player.position().y, (int) player.position().z)
                            + String.format("- Dimension: `%s`\\n", player.level().dimension().location()))

                    .setColor(new Color(0x67d95e))
                    .setFooter("ID: " + Instant.now().getEpochSecond(), "");;

            webhook.addEmbed(embed);
            webhook.execute();
        }
    }

}
