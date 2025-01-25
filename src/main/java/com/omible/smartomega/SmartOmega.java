package com.omible.smartomega;

import com.omible.smartomega.events.*;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.io.File;
import java.util.List;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SmartOmega.MODID)
@SuppressWarnings("ResultOfMethodCallIgnored")
public class SmartOmega {
    public static final String MODID = "smartomega";

    public static MinecraftServer server;
    public static File modDirectory;
    public static File dataDirectory;
    public static int clock = 0;


    public SmartOmega() {
        // Register mod config
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        // Register Server Setup Handler
        MinecraftForge.EVENT_BUS.addListener(this::serverSetup);

        // Register other Event Handlers
        MinecraftForge.EVENT_BUS.register(new PlayerJoinEventHandler());
        MinecraftForge.EVENT_BUS.register(new PlayerLeftEventHandler());
        MinecraftForge.EVENT_BUS.register(new ClockTickEventHandler());
        MinecraftForge.EVENT_BUS.register(new RegisterCommandEventHandler());
        MinecraftForge.EVENT_BUS.register(new ServerChatEventHandler());
        MinecraftForge.EVENT_BUS.register(new BlockBreakEventHandler());
        MinecraftForge.EVENT_BUS.register(new BlockPlacedEventHandler());
        MinecraftForge.EVENT_BUS.register(new DetonateEventHandler());
    }

    private void serverSetup(ServerStartingEvent event) {
        // Stores server for easy access
        server = event.getServer();
        File serverDirectory = server.getServerDirectory();

        modDirectory = new File(serverDirectory, "omega/commands/");
        dataDirectory = new File(serverDirectory, "omega/data/");

        //Ensures directories exist
        if(!modDirectory.exists()) modDirectory.mkdirs();
        if(!dataDirectory.exists()) dataDirectory.mkdirs();

        // Reload command and regions
        Parser.loadOCommands(modDirectory);
        Region.reloadRegions();
    }


    // TabList
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
