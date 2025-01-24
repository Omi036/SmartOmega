package com.omible.smartomega.commands;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.mojang.brigadier.Command;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.context.CommandContext;
import com.omible.smartomega.*;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.phys.Vec3;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;

// DEAR GOD, THIS TOOK FOREVER TO WRITE
// Not because it was hard, but because in java
// one line of code = 100 chars, my god
// Lord forgive me, but im not refactoring all
// of this for the third time.

public class RegionCommand {
    public static String COMMAND_NAME = "region";
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){

        LiteralArgumentBuilder<CommandSourceStack> sourceStack = Commands.literal(COMMAND_NAME)
                .requires(source -> source.hasPermission(4) && Config.regionsEnabled)

            // Subcommand list
            .then(Commands.literal("list")
                .executes(RegionCommand::list))

            // Subcommand create
            .then(Commands.literal("create")
                .then(Commands.argument("name", StringArgumentType.word())
                .then(Commands.argument("startPos", BlockPosArgument.blockPos())
                .then(Commands.argument("endPos", BlockPosArgument.blockPos())
                    .executes(RegionCommand::create)))))

            // Subcommand remove
            .then(Commands.literal("remove")
                .then(Commands.argument("name", StringArgumentType.word())
                    .executes(RegionCommand::remove)))

            .then(Commands.literal("reload")
                .executes(RegionCommand::reload))


            .then(Commands.literal("properties")
                .then(Commands.argument("regionName", StringArgumentType.word())
                    .then(Commands.literal("get")
                        .executes(RegionCommand::getProperties))

                    .then(Commands.literal("set")
                        .then(Commands.literal("enabled")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes(ctx -> RegionCommand.setProperty(ctx, "enabled"))))

                        .then(Commands.literal("opOverride")
                            .then(Commands.argument("value", BoolArgumentType.bool())
                            .executes(ctx -> RegionCommand.setProperty(ctx, "opOverride")))))));

        dispatcher.register( sourceStack );
    }



    /**
     * Command that reloads all the region in the server.
     * @param commandSourceStackCommandContext commandSource
     * @return SINGLE_SUCCESS
     */
    private static int reload(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        SmartOmega.reloadRegions();
        return Command.SINGLE_SUCCESS;
    }



    /**
     * Command that removes a region in the player dimension given its name
     * @param commandSourceStackCommandContext commandSource
     * @return SINGLE_SUCCESS
     */
    private static int remove(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        String regionName = StringArgumentType.getString(commandSourceStackCommandContext, "name");

        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayer();
        String dimension = player.level().dimension().location().toString();

        JsonObject regions = ServerData.getJson("regions.json");

        // If no region in that dimension, end.
        if(!regions.has(dimension)) {
            player.sendSystemMessage(Component.literal("Region not found on this dimension").withStyle(style -> style.withColor(0xc94f4f)));
            return Command.SINGLE_SUCCESS;
        }

        JsonArray regionList = regions.getAsJsonArray(dimension);
        Region selectedRegion = Region.findRegionWithName(regionList, regionName);

        // If no region named like that, end.
        if(selectedRegion.empty){
            player.sendSystemMessage(Component.literal("Region not found on this dimension").withStyle(style -> style.withColor(0xc94f4f)));
            return Command.SINGLE_SUCCESS;
        }

        // Add every new region except for the one
        JsonArray newDimension = new JsonArray();
        regionList.forEach( region -> {
            if(!region.getAsJsonObject().get("name").getAsString().equals(regionName)){
                newDimension.add(region);
            }
        } );

        // Updates the regions of the dimension
        regions.remove(dimension);
        regions.add(dimension, newDimension);


        // And stores it
        ServerData.saveJson(Region.REGION_FILENAME, regions);
        SmartOmega.reloadRegions();

        player.sendSystemMessage(Component.literal("Region eliminated").withStyle(style -> style.withColor(0x57965c)));
        return Command.SINGLE_SUCCESS;
    }



    /**
     * Command that creates a region in the player dimension given the name, start position and end position.
     * @param commandSourceStackCommandContext commandSource
     * @return SINGLE_SUCCESS
     */
    private static int create(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayer();
        JsonObject regions = ServerData.getJson(Region.REGION_FILENAME);

        // Region attributes
        String dimension = player.level().dimension().location().toString();
        String regionName = StringArgumentType.getString(commandSourceStackCommandContext, "name");
        Vec3 startPos = BlockUtils.Vec3FromBlockPos(BlockPosArgument.getBlockPos(commandSourceStackCommandContext, "startPos"));
        Vec3 endPos =  BlockUtils.Vec3FromBlockPos(BlockPosArgument.getBlockPos(commandSourceStackCommandContext, "endPos"));

        // Default properties
        Map<String, Boolean> properties = new HashMap<>();
        properties.put("enabled", true);
        properties.put("opOverride", true);

        // Defines the region give its attributes
        Region region = new Region(regionName, startPos, endPos, properties);

        // If regions exists in the dimension, check for duplicates.
        if(regions.has(dimension)) {
            JsonArray regionList = regions.getAsJsonArray(dimension);
            Region newRegionFound = Region.findRegionWithName(regionList, regionName);

            // If region with the same name found, exit.
            if (!newRegionFound.empty) {
                player.sendSystemMessage(Component.literal(String.format("There is already a region called %s in this dimension", regionName)).withStyle(style -> style.withColor(0xc94f4f)));
                return Command.SINGLE_SUCCESS;
            }

            regionList.add(region.toJson());
            regions.add(dimension, regionList);

        } else {
            JsonArray regionList = new JsonArray();
            regionList.add(region.toJson());
            regions.add(dimension, regionList);
        }

        // Store the region
        ServerData.saveJson(Region.REGION_FILENAME, regions);
        SmartOmega.reloadRegions();

        player.sendSystemMessage(Component.literal(String.format("Region %s created", regionName)).withStyle(style -> style.withColor(0xfcb725)));
        return Command.SINGLE_SUCCESS;
    }



    /**
     * Command that lists every region in the server
     * @param commandSourceStackCommandContext commandSource
     * @return SINGLE_SUCCESS
     */
    private static int list(CommandContext<CommandSourceStack> commandSourceStackCommandContext) {
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayer();
        ServerData.ensureData(Region.REGION_FILENAME); // Ensures regions.json exists
        MutableComponent message = Component.literal("");

        JsonObject regions = ServerData.getJson(Region.REGION_FILENAME); // Gets full json

        // Iterate through every region inside the dimensions
        regions.keySet().forEach(dimension -> {
            regions.getAsJsonArray(dimension).asList().forEach(regionJson -> {
                Region currentRegion = Region.fromJson((JsonObject) regionJson);

                message.append( Component.literal("\n"));
                message.append( Component.literal(currentRegion.name)
                        .withStyle(style -> style.withColor(0xfcb725).withBold(true)));

                message.append( Component.literal( " (" + dimension + ")").withStyle(style -> style.withBold(false).withColor(0xbbbbbb)));

                message.append( Component.literal(String.format("\n - From %03d %03d %03d",
                        (int) currentRegion.startPos.x,
                        (int) currentRegion.startPos.y,
                        (int) currentRegion.startPos.z)).withStyle(style -> style.withColor(0xc7a312)));

                message.append( Component.literal(String.format("\n - To %03d %03d %03d\n",
                        (int) currentRegion.endPos.x,
                        (int) currentRegion.endPos.y,
                        (int) currentRegion.endPos.z)).withStyle(style -> style.withColor(0xc7a312)));
            });
        });


        if(message.getString().isBlank()){
            player.sendSystemMessage(Component.literal("No regions created").withStyle(style -> style.withColor(0xbbbbbb)));
            return Command.SINGLE_SUCCESS;
        }


        // Sends the full component
        player.sendSystemMessage(message);
        return Command.SINGLE_SUCCESS;
    }



    /**
     * Command that shows all properties of a region given its name
     * @param commandSourceStackCommandContext commandSource
     * @return SINGLE_SUCCESS
     */
    private static int getProperties(CommandContext<CommandSourceStack> commandSourceStackCommandContext){
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayer();

        ServerData.ensureData(Region.REGION_FILENAME); // Ensures regions.json exists
        JsonObject regions = ServerData.getJson(Region.REGION_FILENAME); // Gets full json
        MutableComponent message = Component.literal("");

        String dimension = player.level().dimension().location().toString();
        String regionName = StringArgumentType.getString(commandSourceStackCommandContext, "regionName");

        if(!regions.has(dimension)) {
            message.append(Component.literal("Region not found").withStyle(style -> style.withColor(0xc94f4f)));
            player.sendSystemMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        Region selectedRegion = Region.findRegionWithName(regions.getAsJsonArray(dimension), regionName);

        if(selectedRegion.empty){
            message.append(Component.literal("Region not found").withStyle(style -> style.withColor(0xc94f4f)));
            player.sendSystemMessage(message);
            return Command.SINGLE_SUCCESS;

        }

        message.append(Component.literal("Region " + regionName + "\n").withStyle(style -> style.withColor(0xfcb725).withBold(true)));
        message.append(Component.literal(" - enabled: " + selectedRegion.properties.get("enabled") + "\n").withStyle(style -> style.withColor(0xc7a312).withBold(false)));
        message.append(Component.literal(" - opOverride: " + selectedRegion.properties.get("opOverride")).withStyle(style -> style.withColor(0xc7a312).withBold(false)));

        player.sendSystemMessage(message);
        return Command.SINGLE_SUCCESS;
    }



    /**
     * Command that sets a property on the region.
     * @param commandSourceStackCommandContext commandSource
     * @param propertyName Property to change
     * @return
     */
    private static int setProperty(CommandContext<CommandSourceStack> commandSourceStackCommandContext, String propertyName){
        ServerPlayer player = commandSourceStackCommandContext.getSource().getPlayer();

        String dimension = player.level().dimension().location().toString();
        String regionName = StringArgumentType.getString(commandSourceStackCommandContext, "regionName");
        Boolean value = BoolArgumentType.getBool(commandSourceStackCommandContext, "value");

        ServerData.ensureData(Region.REGION_FILENAME);
        JsonObject regions = ServerData.getJson(Region.REGION_FILENAME);
        MutableComponent message = Component.literal("");

        if(!regions.has(dimension)){
            message.append(Component.literal("Region not found").withStyle(style -> style.withColor(0xc94f4f)));
            player.sendSystemMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        Region selectedRegion = Region.findRegionWithName(regions.getAsJsonArray(dimension), regionName);

        if(selectedRegion.empty){
            message.append(Component.literal("Region not found").withStyle(style -> style.withColor(0xc94f4f)));
            player.sendSystemMessage(message);
            return Command.SINGLE_SUCCESS;
        }

        selectedRegion.properties.put(propertyName, value);
        JsonArray newDimensionJson = Region.replaceRegionInsideDimension(regions.getAsJsonArray(dimension), regionName, selectedRegion.toJson());
        regions.remove(dimension);
        regions.add(dimension, newDimensionJson);

        ServerData.saveJson(Region.REGION_FILENAME, regions);
        SmartOmega.reloadRegions();

        message.append(Component.literal("Region " + regionName + " edited").withStyle(style -> style.withColor(0xfcb725).withBold(true)));
        player.sendSystemMessage(message);

        return Command.SINGLE_SUCCESS;
    }
}