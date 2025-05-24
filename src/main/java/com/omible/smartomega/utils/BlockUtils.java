package com.omible.smartomega.utils;

import com.google.gson.JsonArray;
import net.minecraft.core.BlockPos;
import net.minecraft.world.phys.Vec3;

public class BlockUtils {

    public static Vec3 Vec3FromBlockPos(BlockPos blockPos){
        return new Vec3( blockPos.getX(), blockPos.getY(), blockPos.getZ() );
    }

    public static Vec3 Vec3FromJsonArray(JsonArray pos){
        return new Vec3(pos.get(0).getAsInt(), pos.get(1).getAsInt(), pos.get(2).getAsInt());
    }

    public static BlockPos BlockPosFromVec3(Vec3 vec){
        return new BlockPos((int) vec.x, (int) vec.y, (int) vec.z);
    }

    public static BlockPos BlockPosFromJsonArray(JsonArray pos){
        return new BlockPos(pos.get(0).getAsInt(), pos.get(1).getAsInt(), pos.get(2).getAsInt());
    }

    public static boolean isBlockBetween(BlockPos blockPos, BlockPos startPos, BlockPos endPos) {
        // Obtener los límites mínimos y máximos
        int minX = Math.min(startPos.getX(), endPos.getX());
        int maxX = Math.max(startPos.getX(), endPos.getX());
        int minY = Math.min(startPos.getY(), endPos.getY());
        int maxY = Math.max(startPos.getY(), endPos.getY());
        int minZ = Math.min(startPos.getZ(), endPos.getZ());
        int maxZ = Math.max(startPos.getZ(), endPos.getZ());

        // Verificar si el bloque está dentro de los límites
        return blockPos.getX() >= minX && blockPos.getX() <= maxX &&
                blockPos.getY() >= minY && blockPos.getY() <= maxY &&
                blockPos.getZ() >= minZ && blockPos.getZ() <= maxZ;
    }

    public static boolean isBlockBetween(BlockPos blockPos, Vec3 startPos, Vec3 endPos) {
        // Obtener los límites mínimos y máximos
        int minX = Math.min(  (int) startPos.x(), (int) endPos.x()  );
        int maxX = Math.max(  (int) startPos.x(), (int) endPos.x()  );
        int minY = Math.min(  (int) startPos.y(), (int) endPos.y()  );
        int maxY = Math.max(  (int) startPos.y(), (int) endPos.y()  );
        int minZ = Math.min(  (int) startPos.z(), (int) endPos.z()  );
        int maxZ = Math.max(  (int) startPos.z(), (int) endPos.z()  );

        // Verificar si el bloque está dentro de los límites
        return blockPos.getX() >= minX && blockPos.getX() <= maxX &&
                blockPos.getY() >= minY && blockPos.getY() <= maxY &&
                blockPos.getZ() >= minZ && blockPos.getZ() <= maxZ;
    }
}
