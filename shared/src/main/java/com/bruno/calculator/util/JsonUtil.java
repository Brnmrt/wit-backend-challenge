package com.bruno.calculator.util;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;

public class JsonUtil {
    private static final ObjectMapper OBJECT_MAPPER = new ObjectMapper();
    private static final ObjectWriter WRITER = OBJECT_MAPPER.writerWithDefaultPrettyPrinter();

    public static String toJson(Object object) {
        try {
            return WRITER.writeValueAsString(object);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao serializar para JSON", e);
        }
    }

    public static <T> T fromJson(String json, Class<T> clazz) {
        try {
            return OBJECT_MAPPER.readValue(json, clazz);
        } catch (Exception e) {
            throw new RuntimeException("Erro ao desserializar de JSON", e);
        }
    }

    private JsonUtil() {
    }
}