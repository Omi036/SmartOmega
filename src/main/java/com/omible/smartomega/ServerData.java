package com.omible.smartomega;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class ServerData {
    private static File dataDirectory = SmartOmega.dataDirectory;
    private static final Gson GSON = new Gson();



    /**
     * Guarda un JsonObject en un archivo.
     *
     * @param filename Nombre del archivo (incluyendo la extensión .json).
     * @param object   El JsonObject a guardar.
     */
    public static void saveJson(String filename, JsonObject object) {
        File file = new File(dataDirectory, filename);
        try (FileWriter writer = new FileWriter(file)) {
            GSON.toJson(object, writer);
        } catch (IOException e) {
            throw new RuntimeException("Error al guardar el archivo JSON: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Lee un JsonObject desde un archivo.
     *
     * @param filename Nombre del archivo (incluyendo la extensión .json).
     * @return El JsonObject leído.
     */
    public static JsonObject getJson(String filename) {
        File file = new File(dataDirectory, filename);
        if (!file.exists()) {
            throw new RuntimeException("El archivo JSON no existe: " + file.getAbsolutePath());
        }

        try (FileReader reader = new FileReader(file)) {
            return JsonParser.parseReader(reader).getAsJsonObject();
        } catch (IOException e) {
            throw new RuntimeException("Error al leer el archivo JSON: " + file.getAbsolutePath(), e);
        }
    }

    /**
     * Modifica una propiedad de un JsonObject.
     *
     * @param originalObject El JsonObject original.
     * @param property       La propiedad a modificar.
     * @param newValue       El nuevo valor para la propiedad.
     * @return El JsonObject modificado.
     */
    public static JsonObject modifyProperty(JsonObject originalObject, String property, JsonElement newValue) {
        originalObject.add(property, newValue);
        return originalObject;
    }

    public static void ensureData(String filename){
        File datafile = new File(dataDirectory, filename);
        if(!datafile.exists()){
            saveJson(filename, new JsonObject());
        }
    }
}
