package com.example;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.javalin.Javalin;
import io.javalin.http.staticfiles.Location;
import io.javalin.plugin.bundled.CorsPluginConfig;

import java.io.IOException;
import java.io.InputStream;
import java.util.Iterator;
import java.util.Map;

public class JavalinJsonApp {

    private static ArrayNode cleanedData;
    private static final ObjectMapper mapper = new ObjectMapper();

    public static void main(String[] args) {
        try {
            cleanedData = loadAndCleanJsonFromResources();
            System.out.println("JSON data loaded and sanitized successfully!");
        } catch (Exception e) {
            System.err.println("Fatal error processing source JSON data: " + e.getMessage());
            return;
        }

        Javalin app = Javalin.create(config -> {
            config.staticFiles.add(staticFiles -> {
                staticFiles.hostedPath = "/";
                staticFiles.directory = "/public";
                staticFiles.location = Location.CLASSPATH;
            });

            config.bundledPlugins.enableCors(cors -> {
                cors.addRule(CorsPluginConfig.CorsRule::anyHost);
            });
        });

        app.get("/api/covid-data", ctx -> {
            ctx.json(cleanedData);
        });

        app.start(8080);
        System.out.println("Javalin server running at http://localhost:8080/api/covid-data");
    }

    /**
     * Finds and opens a file from target classpath resources,
     * stripping whitespace formats from integers strings
     * and parsing the integer string into integers.
     */
    private static ArrayNode loadAndCleanJsonFromResources() throws IOException {
        try (InputStream is = JavalinJsonApp.class.getClassLoader().getResourceAsStream("covid.json")) {
            if (is == null) {
                throw new IOException("Resource file covid.json could not be found in the resources directory.");
            }

            JsonNode rootNode = mapper.readTree(is);
            ArrayNode targetArray = mapper.createArrayNode();

            if (rootNode.isArray()) {
                for (JsonNode record : rootNode) {
                    ObjectNode cleanedRecord = mapper.createObjectNode();
                    Iterator<Map.Entry<String, JsonNode>> fields = record.fields();

                    while (fields.hasNext()) {
                        Map.Entry<String, JsonNode> field = fields.next();
                        String key = field.getKey();
                        JsonNode value = field.getValue();

                        if (key.equalsIgnoreCase("Date")) {
                            cleanedRecord.put(key, value.asText());
                        } else {
                            String rawValue = value.asText().replaceAll("\\s+", "");
                            try {
                                int parsedInt = Integer.parseInt(rawValue);
                                cleanedRecord.put(key, parsedInt);
                            } catch (NumberFormatException e) {
                                cleanedRecord.put(key, value.asText());
                            }
                        }
                    }
                    targetArray.add(cleanedRecord);
                }
            }
            return targetArray;
        }
    }
}
