package com.omible.smartomega.commands;

import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.omible.smartomega.utils.Parser;
import com.omible.smartomega.SmartOmega;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.players.PlayerList;

import static com.omible.smartomega.SmartOmega.LOGGER;

@SuppressWarnings("SameReturnValue")
public class RunCommand {
    public static final String COMMAND_NAME = "run";
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){

        LiteralArgumentBuilder<CommandSourceStack> sourceStack = Commands.literal(COMMAND_NAME)
                .requires(source -> source.hasPermission(4))
            .then(Commands.argument("script", StringArgumentType.string())
            .executes(RunCommand::execute));

        dispatcher.register( sourceStack );
    }


    public static int execute(CommandContext<CommandSourceStack> command){
        PlayerList playerList = command.getSource().getServer().getPlayerList();
        String scriptname = StringArgumentType.getString(command, "script") + ".ocmd";

        if(command.getSource().isPlayer()){
            ServerPlayer player = (ServerPlayer) command.getSource().getEntity();

            assert player != null;

            System.out.println("Executting command " + scriptname);

            if(!playerList.isOp(player.getGameProfile())) {
                player.sendSystemMessage(Component.literal("§4You're not OP"));
                return Command.SINGLE_SUCCESS;
            }

            if(scriptname.equals("reload.ocmd")){
                Parser.loadOCommands(SmartOmega.modDirectory);
                player.sendSystemMessage(Component.literal("§2Commands updated"));
                return Command.SINGLE_SUCCESS;
            }

            try{
                Parser.exec(scriptname);
            } catch (Error e){
                player.sendSystemMessage(Component.literal(String.format("§4Error: %s", e.getMessage())));
            }


        } else if (command.getSource().getEntity() == null) {
            System.out.println("Executting command " + scriptname);

            if(scriptname.equals("reload.ocmd")){
                Parser.loadOCommands(SmartOmega.modDirectory);
                LOGGER.info(Component.literal("§2Commands updated"));
                return Command.SINGLE_SUCCESS;
            }

            try{
                Parser.exec(scriptname);
            } catch (Error e){
                LOGGER.info(Component.literal(String.format("§4Error: %s", e.getMessage())));
            }
        }


        return Command.SINGLE_SUCCESS;
    }
}
