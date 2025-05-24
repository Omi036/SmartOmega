package com.omible.smartomega.utils;

import com.mojang.logging.LogUtils;
import com.omible.smartomega.SmartOmega;
import net.minecraft.commands.CommandSourceStack;
import net.minecraftforge.event.TickEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.slf4j.Logger;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

public class Parser {
    private static final Logger LOGGER = LogUtils.getLogger();

    public static HashMap<String, List<String>> scripts = new HashMap<>();
    private static final Queue<ScheduledCommand> commandQueue = new LinkedBlockingQueue<>();
    private static int tickCounter = 0;

    // Loads commandfile statements and stores it.
    public static void loadOCommands(File directory){
        scripts.clear();
        for(File ocommand : Objects.requireNonNull(directory.listFiles())){
            if(!ocommand.isDirectory() && ocommand.getName().endsWith(".ocmd")){
                try{
                    List<String> statements = Files.readAllLines(Path.of(ocommand.getAbsolutePath()));
                    scripts.put(ocommand.getName(), statements);

                } catch (IOException e){
                    LOGGER.error(e.toString());
                }

            }
        }
    }

    public static List<String> getStatementsByScriptname(String scriptname) throws Error {
        if(!scripts.containsKey(scriptname)){
            throw new Error(String.format("No script named %s", scriptname));
        }

        return scripts.get(scriptname);
    }



    // Ejecuta la lista de comandos con sus tiempos de espera
    public static void exec(String scriptname) {
        commandQueue.clear();
        List<String> statements = getStatementsByScriptname(scriptname);
        int currentDelay = 0;

        for (String statement : statements) {
            if (statement.startsWith("wait")) {
                try {
                    int delay = Integer.parseInt(statement.split(" ")[1]);
                    currentDelay += delay;
                } catch (NumberFormatException e) {
                    System.err.println("Invalid wait delay: " + statement);
                }
            } else {
                commandQueue.add(new ScheduledCommand(currentDelay, statement));
            }
        }
    }

    @Mod.EventBusSubscriber(modid = SmartOmega.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
    public static class ServerEventsListener {
        @SubscribeEvent
        public static void onServerTick(TickEvent.ServerTickEvent event) {
            if (event.phase == TickEvent.Phase.END) {
                tickCounter++;

                while (!commandQueue.isEmpty() && commandQueue.peek().shouldExecute(tickCounter)) {
                    ScheduledCommand command = commandQueue.poll();
                    executeCommand(command.command);
                }
            }
        }
    }


    private static void executeCommand(String command) {
        CommandSourceStack source = SmartOmega.server.createCommandSourceStack();
        SmartOmega.server.getCommands().performPrefixedCommand(source, command);
    }

    private static class ScheduledCommand {
        private final int executionTick;
        private final String command;

        public ScheduledCommand(int delayTicks, String command) {
            this.executionTick = tickCounter + delayTicks;
            this.command = command;
        }

        public boolean shouldExecute(int currentTick) {
            return currentTick >= executionTick;
        }
    }
}
