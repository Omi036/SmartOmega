package com.omible.smartomega;
import com.google.genai.Client;
import com.google.genai.types.GenerateContentResponse;

import java.io.IOException;
import java.util.HashMap;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

public class GenAI {
    static Client apiClient;
    static String systemPrompt = """
            System:
            
            Eres un asistente virtual llamado Nomily en un servidor de Minecraft llamado SmartMC Omega. Debes generar respuestas empáticas para acompañar al usuario. 
            
            Reglas:
            1. Tus respuestas deben ser concisas y no deben exceder las cuatro líneas para evitar cuelgues.
            2. No salgas de tu rol; mantente en la simulación.
            3. No incluyas dos saltos de linea (\\n) seguidos.
            4. Si el usuario ya ha interactuado antes, no es necesario saludarlo nuevamente.
            5. Tampoco es necesario que incluyas una pregunta al final de cada respuesta. 
            6. No tienes por qué incluir todo el contexto en tu respuesta si no es preciso. 
            
            Contexto:
            1. El servidor se llama SmartMC Omega, creado por Omi (Omi036) y DavidEsmart.
            2. Se ejecuta en Forge 1.20.1.
            3. Es un servidor privado, cuyos usuarios son amigos.
            
            Entorno actual:
            1. El TPS Actual es de %s
            2. Ahora mismo hay %d jugadores
            3. Los jugadores conectados son: %s
            4. Te está hablando %s
            5. Veces que el usuario ha interactuado contigo hoy: %d
            """;

    static HashMap<String, Integer> userInteractions = new HashMap<>();

    public static void prepareClient(){
        String API_KEY = Config.geminiKey;
        apiClient = Client.builder().apiKey(API_KEY).build();
    }


    public static String promptText(String text, String playerName)  {
        try {
            String TPS = String.valueOf(TPSMonitor.tps);
            String userPrompt = String.format("User: %s", text);
            AtomicReference<String> playerList = new AtomicReference<>("");

            SmartOmega.server.getPlayerList().getPlayers().forEach(player -> {
                playerList.set( playerList.get() + player.getDisplayName().getString() + ",");
            });

            String prompt = String.format(
                    systemPrompt,
                    TPS,
                    SmartOmega.server.getPlayerCount(),
                    playerList,
                    playerName,
                    userInteractions.getOrDefault(playerName, 1)
            ) + userPrompt;

            GenerateContentResponse response = apiClient.models.generateContent("gemini-2.0-flash-lite", prompt, null);

            if(userInteractions.containsKey(playerName)){
                userInteractions.put( playerName, userInteractions.get(playerName) +1 );
            } else {
                userInteractions.put( playerName, 2 );
            }

            return  Objects.requireNonNull(response.text()).trim();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "Error en la IA :(";
        }
    }
}
