package com.itstep.homecook.services.config;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.inject.Inject;
import com.google.inject.Singleton;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

@Singleton
public class JsonConfigService implements ConfigService {

    private static final Gson gson = new Gson();
    private final Logger logger;
    private JsonElement root;

    @Inject
    public JsonConfigService(Logger logger) {
        this.logger = logger;
    }

    @Override
    public void addFile(String filename) {
        try (InputStream inputStream = Objects.requireNonNull(
                this
                        .getClass()
                        .getClassLoader()
                        .getResourceAsStream(filename)); 
                        Reader reader = new InputStreamReader(inputStream)) {
            root = gson.fromJson(reader, JsonElement.class);
        } catch (IOException ex) {
            this.logger.log(Level.SEVERE, ex.getMessage());
        }
    }

    @Override
    public String get(String path) {
        if (root == null) {
            this.addFile("appsettings.json");
        }
        
        String[] parts = path.split("\\.");
        JsonObject jsonObject = root.getAsJsonObject();
        for (int i = 0; i < parts.length - 1; i++) {
            JsonElement element = jsonObject.get(parts[i]);
            if (element == null || !element.isJsonObject()) {
                throw new NoSuchFieldError(
                        "JsonConfigService: Error accessing field '"
                        + parts[i]
                        + "'. Field either absent or not an object"
                );
            }
            jsonObject = element.getAsJsonObject();
        }
        return jsonObject.get(parts[parts.length - 1]).getAsString();



    }

}