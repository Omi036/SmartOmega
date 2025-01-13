package com.omible.smartomega;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.omible.smartomega.commands.OOPCommand;
import com.omible.smartomega.commands.RunCommand;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.BuildCreativeModeTabContentsEvent;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLDedicatedServerSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SmartOmega.MODID)
public class SmartOmega
{

    public static int clock = 0;
    public static MinecraftServer server;
    public static File modDirectory;

    public static final String MODID = "smartomega";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SmartOmega() {
        IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //modEventBus.addListener(this::serverSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        MinecraftForge.EVENT_BUS.addListener(this::serverSetup);
    }

    private void serverSetup(ServerStartingEvent event) {
        server = event.getServer();
        File serverDirectory = server.getServerDirectory();
        modDirectory = new File(serverDirectory, "omega/commands/");

        if(!modDirectory.exists()){
            modDirectory.mkdirs();
        }

        Parser.loadOCommands(modDirectory);
    }


    public static void updateTabList(MinecraftServer server){
        List<ServerPlayer> players = server.getPlayerList().getPlayers();

        for (ServerPlayer player : players) {
            String ping = String.valueOf(player.latency);
            String name = String.valueOf(player.getName());
            String ip = player.getIpAddress();
            String health = String.valueOf(Math.round(player.getHealth()));

            String headerContent = Config.tablistHeader
                    .replaceAll("(?<!\\\\)&","§")
                    .replaceAll("\\{ping}", ping)
                    .replaceAll("\\{name}", name)
                    .replaceAll("\\{ip}", ip)
                    .replaceAll("\\{health}", health);

            String footerContent = Config.tablistFooter
                    .replaceAll("(?<!\\\\)&","§")
                    .replaceAll("\\{ping}", ping)
                    .replaceAll("\\{name}", name)
                    .replaceAll("\\{ip}", ip)
                    .replaceAll("\\{health}", health);

            Component header = Component.literal(headerContent);
            Component footer = Component.literal(footerContent);

            player.connection.send(new ClientboundTabListPacket(header, footer));
        }
    }

    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ServerEventsListener {
//        @SubscribeEvent
//        public static void onChatMessage(ServerChatEvent event) {
//        }

        @SubscribeEvent
        public static void onClockTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                clock++;

                if (clock % 20*5 == 0) {
                    updateTabList(event.getServer());
                }
            }
        }

        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event){
            OOPCommand.register(event.getDispatcher());
            RunCommand.register(event.getDispatcher());
        }

//        @SubscribeEvent
//        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
//            ServerPlayer player = (ServerPlayer) event.getEntity();
//
//        }

        @SubscribeEvent
        public static void onPlayerLeft(PlayerEvent.PlayerLoggedOutEvent event){
            ServerPlayer player = (ServerPlayer) event.getEntity();
            GameProfile profile = player.getGameProfile();
            PlayerList list = server.getPlayerList();

            if(Config.deopOnLeave && list.isOp(profile)){
                list.deop(profile);
                LOGGER.info(String.format("Player %s was deoped because he left.", player.getName()));
            }
        }

        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
            ServerPlayer player = (ServerPlayer) event.getEntity();
            GameProfile profile = player.getGameProfile();
            PlayerList list = server.getPlayerList();

            if(Config.deopOnLeave && list.isOp(profile)){
                list.deop(profile);
                LOGGER.info(String.format("Player %s was deoped because he joined.", player.getName()));
            }
        }
    }
}
