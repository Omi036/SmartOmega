package com.omible.smartomega;

import com.omible.smartomega.events.*;
import com.omible.smartomega.DiscordWebhook.EmbedObject;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;

import java.awt.*;
import java.io.File;
import java.time.Instant;

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
        MinecraftForge.EVENT_BUS.register(new PlayerDeathEventHandler());
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

        // Notifies to Discord Webhook
        if(Config.webhooksEnabled && Config.webhooksStartupEnabled){
            DiscordWebhook webhook = new DiscordWebhook(Config.webhookUrl);
            EmbedObject embed = new EmbedObject()
                .setTitle("🔵 ServerStart")
                .setDescription("The server is now online")
                .setColor(new Color(0x5eced9))
                .setFooter( "ID: " + Instant.now().getEpochSecond(), "");

            webhook.addEmbed(embed);
            webhook.execute();
        }
    }
}
