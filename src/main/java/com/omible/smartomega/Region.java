package com.omible.smartomega;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import net.minecraft.world.phys.Vec3;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicReference;

public class Region {
    public String name;
    public Vec3 startPos;
    public Vec3 endPos;
    public Map<String, Boolean> properties;
    public Boolean empty;
    public ArrayList<String> owners;

    public static JsonObject regions;
    public static final Region Empty = new Region();
    public static final String REGION_FILENAME = "regions.json";

    public Region(String name, Vec3 startPos, Vec3 endPos, Map<String, Boolean> properties, ArrayList<String> owners){
        this.name = name;
        this.startPos = startPos;
        this.endPos = endPos;
        this.properties = properties;
        this.empty = false;
        this.owners = owners;
    }

    public Region(){
        this.empty = true;
    }


    public JsonObject toJson(){
        JsonArray startPosArray = new JsonArray();
        startPosArray.add(startPos.x);
        startPosArray.add(startPos.y);
        startPosArray.add(startPos.z);

        JsonArray endPosArray = new JsonArray();
        endPosArray.add(endPos.x);
        endPosArray.add(endPos.y);
        endPosArray.add(endPos.z);

        JsonObject propertiesObject = new JsonObject();
        properties.keySet().forEach(key -> propertiesObject.addProperty(key, properties.get(key)));

        JsonArray ownersArray = new JsonArray();
        owners.forEach(ownersArray::add);

        JsonObject object = new JsonObject();
        object.addProperty("name", this.name);
        object.add("startPos", startPosArray);
        object.add("endPos", endPosArray);
        object.add("properties", propertiesObject);
        object.add("owners", ownersArray);

        return object;
    }

    public static Region fromJson(JsonObject jsonRegion){
        if( !(jsonRegion.has("name")
                && jsonRegion.has("startPos")
                && jsonRegion.has("endPos")
                && jsonRegion.has("properties")
                && jsonRegion.has("owners"))) {

            throw new RuntimeException("Object is not a child of a Region");
        }


        String name = jsonRegion.get("name").getAsString();
        JsonArray startPos = jsonRegion.getAsJsonArray("startPos");
        JsonArray endPos = jsonRegion.getAsJsonArray("endPos");
        JsonObject properties = jsonRegion.getAsJsonObject("properties");
        JsonArray owners = jsonRegion.getAsJsonArray("owners");

        Vec3 startPosVec = new Vec3(startPos.get(0).getAsInt(), startPos.get(1).getAsInt(), startPos.get(2).getAsInt());
        Vec3 endPosVec = new Vec3(endPos.get(0).getAsInt(), endPos.get(1).getAsInt(), endPos.get(2).getAsInt());
        Map<String, Boolean> propertiesObject = new HashMap<>();
        ArrayList<String> ownersArray = new ArrayList<>();
        owners.forEach(owner -> ownersArray.add(owner.toString()));

        properties.keySet().forEach(key -> propertiesObject.put(key, properties.get(key).getAsBoolean()));

        return new Region(name, startPosVec, endPosVec, propertiesObject, ownersArray);
    }


    public static Region findRegionWithName(JsonArray dimensionRegions, String name){
        AtomicReference<Region> returnable = new AtomicReference<>(Region.Empty);

        dimensionRegions.forEach(region -> {
            Region currentRegion = Region.fromJson(region.getAsJsonObject());

            if(currentRegion.name.equals(name)) {
                returnable.set(currentRegion);
            }
        });

        return returnable.get();
    }

    public static JsonArray replaceRegionInsideDimension(JsonArray dimension, String regionName, JsonObject newRegion){
        JsonArray newDimensionJson = new JsonArray();
        dimension.forEach(region -> {
            Region currentRegion = Region.fromJson(region.getAsJsonObject());
            if(currentRegion.name.equals(regionName)) return;

            newDimensionJson.add(region);
        });

        newDimensionJson.add(newRegion);
        return newDimensionJson;
    }


    public static void reloadRegions() {
        ServerData.ensureData(Region.REGION_FILENAME);
        regions = ServerData.getJson(REGION_FILENAME);
    }
}
