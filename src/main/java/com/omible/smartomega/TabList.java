package com.omible.smartomega;

import com.omible.smartomega.utils.TPSMonitor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;

import java.util.List;

public class TabList {

    public static void updateTabList(MinecraftServer server){
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        for (ServerPlayer player : players) {
            String ping = String.valueOf(player.latency);
            String name = player.getName().getString();
            String ip = player.getIpAddress();
            String health = String.valueOf(Math.round(player.getHealth()));
            String playerCount = String.valueOf(server.getPlayerList().getPlayerCount());
            String TPS = String.valueOf(TPSMonitor.tps);

            String headerContent = Config.tablistHeader
                    .replaceAll("(?<!\\\\)&","§")
                    .replaceAll("\\{ping}", ping)
                    .replaceAll("\\{name}", name)
                    .replaceAll("\\{ip}", ip)
                    .replaceAll("\\{count}", playerCount)
                    .replaceAll("\\{health}", health)
                    .replaceAll("\\{tps}", TPS);


            String footerContent = Config.tablistFooter
                    .replaceAll("(?<!\\\\)&","§")
                    .replaceAll("\\{ping}", ping)
                    .replaceAll("\\{name}", name)
                    .replaceAll("\\{ip}", ip)
                    .replaceAll("\\{count}", playerCount)
                    .replaceAll("\\{health}", health)
                    .replaceAll("\\{tps}", TPS);

            Component header = Component.literal(headerContent);
            Component footer = Component.literal(footerContent);

            player.connection.send(new ClientboundTabListPacket(header, footer));
        }
    }

}
