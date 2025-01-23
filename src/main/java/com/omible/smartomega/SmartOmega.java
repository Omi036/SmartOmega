package com.omible.smartomega;

import com.mojang.authlib.GameProfile;
import com.mojang.logging.LogUtils;
import com.omible.smartomega.commands.OOPCommand;
import com.omible.smartomega.commands.RunCommand;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ClientboundTabListPacket;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.event.ServerChatEvent;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import org.slf4j.Logger;

import java.io.File;
import java.util.List;
import java.util.Objects;

// The value here should match an entry in the META-INF/mods.toml file
@Mod(SmartOmega.MODID)
public class SmartOmega
{

    public static int clock = 0;
    public static MinecraftServer server;
    public static File modDirectory;
    public static File dataDirectory;

    public static final String MODID = "smartomega";
    private static final Logger LOGGER = LogUtils.getLogger();

    public SmartOmega() {
        //IEventBus modEventBus = FMLJavaModLoadingContext.get().getModEventBus();
        //modEventBus.addListener(this::serverSetup);

        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, Config.SPEC);

        MinecraftForge.EVENT_BUS.addListener(this::serverSetup);
    }

    private void serverSetup(ServerStartingEvent event) {
        server = event.getServer();
        File serverDirectory = server.getServerDirectory();
        modDirectory = new File(serverDirectory, "omega/commands/");
        dataDirectory = new File(serverDirectory, "omega/data/");

        if(!modDirectory.exists()){
            modDirectory.mkdirs();
        }

        if(!dataDirectory.exists()){
            dataDirectory.mkdirs();
        }

        Parser.loadOCommands(modDirectory);
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

    // Custom Chat
    @Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ServerEventsListener {
        @SubscribeEvent
        public static void onChatMessage(ServerChatEvent event) {
            if(!Config.tagNames) return;


            if(server.getPlayerList().isOp(event.getPlayer().getGameProfile())){

                server.getPlayerList().getPlayers().forEach(player -> {
                    Component message = Component.Serializer.fromJson(String.format("[{\"text\":\"\\uE921\"},{\"text\":\" %s \", \"color\":\"#9253a9\", \"bold\":true}, {\"text\":\">> \", \"color\":\"#555555\"}, {\"text\":\"%s\"}]", event.getPlayer().getDisplayName().getString(), event.getMessage().getString()));
                    assert message != null;
                    player.sendSystemMessage(message);
                });


            } else {

                server.getPlayerList().getPlayers().forEach(player -> {
                    Component message = Component.Serializer.fromJson(String.format("[{\"text\":\"\\uE922\"},{\"text\":\" %s\", \"color\":\"#dddddd\", \"bold\":true}, {\"text\":\": \", \"color\":\"#555555\"}, {\"text\":\"%s\"}]", event.getPlayer().getDisplayName().getString(), event.getMessage().getString()));
                    assert message != null;
                    player.sendSystemMessage(message);
                });

            }

            event.setCanceled(true);
        }


        // Mod Clock
        @SubscribeEvent
        public static void onClockTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                clock++;

                if (clock % 20*5 == 0) {
                    updateTabList(event.getServer());
                }
            }
        }

        // Registering Commands
        @SubscribeEvent
        public static void registerCommands(RegisterCommandsEvent event){
            OOPCommand.register(event.getDispatcher());
            RunCommand.register(event.getDispatcher());
        }



        // On Player Left
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

        // On Player Join
        @SubscribeEvent
        public static void onPlayerJoin(PlayerEvent.PlayerLoggedInEvent event){
            ServerPlayer player = (ServerPlayer) event.getEntity();
            GameProfile profile = player.getGameProfile();
            PlayerList list = server.getPlayerList();
            String welcomeMessage = Config.welcomeMessage;

            if(Config.deopOnJoin && list.isOp(profile)){
                list.deop(profile);
                LOGGER.info(String.format("Player %s was deoped because he joined.", player.getName()));
            }

            if(!welcomeMessage.isEmpty()){
                String ping = String.valueOf(player.latency);
                String name = player.getName().getString();
                String ip = player.getIpAddress();
                String playerCount = String.valueOf(server.getPlayerCount());

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
}
