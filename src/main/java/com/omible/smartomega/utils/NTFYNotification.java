package com.omible.smartomega.utils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.io.OutputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

public class NTFYNotification {
    public static final String endpoint = "https://ntfy.sh/";
    public static class Notification {
        private String topic;
        private String title;
        private String description;
        private String arrachmentURL;
        private String tag;

        public Notification setTopic(String topic){
            this.topic = topic;
            return this;
        }

        public Notification setTitle(String title){
            this.title = title;
            return this;
        }

        public Notification setDescription(String description){
            this.description = description;
            return this;
        }

        public Notification setAttachmentUrl(String attachmentUrl){
            this.arrachmentURL = attachmentUrl;
            return this;
        }

        public Notification setTag(String tag){
            this.tag = tag;
            return this;
        }

        public void send() {
            CompletableFuture.runAsync(() -> {
                URL url;
                HttpsURLConnection connection;

                try {
                    url = new URL(NTFYNotification.endpoint + topic);
                } catch (MalformedURLException e) {
                    System.out.println("Err 1");
                    throw new RuntimeException(e);
                }

                try {
                    connection = (HttpsURLConnection) url.openConnection();
                    connection.addRequestProperty("User-Agent", "Java-Omil");
                    connection.addRequestProperty("Title", title);
                    connection.setDoOutput(true);
                    connection.setRequestMethod("POST");

                    if (this.arrachmentURL != null) {
                        connection.addRequestProperty("Attach", arrachmentURL);
                    }

                    if (this.tag != null) {
                        connection.addRequestProperty("Tags", tag);
                    }

                    try (OutputStream stream = connection.getOutputStream()) {
                        stream.write(description.getBytes());
                        stream.flush();
                    }

                    connection.getInputStream().close(); // Necesario para que la solicitud se complete correctamente
                    connection.disconnect();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            });
        }
    }
}
