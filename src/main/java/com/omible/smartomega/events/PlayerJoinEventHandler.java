package com.omible.smartomega.events;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.omible.smartomega.Config;
import com.omible.smartomega.DiscordWebhook.EmbedObject;
import com.omible.smartomega.DiscordWebhook;
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
        PlayerList list = SmartOmega.server.getPlayerList();
        String welcomeMessage = Config.welcomeMessage;

        if(Config.deopOnJoin && list.isOp(profile)){
            list.deop(profile);
            LOGGER.info("Player {} was deoped because he joined.", player.getName());
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
