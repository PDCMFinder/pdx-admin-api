package org.pdxfinder.util;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class JsonHelper {
  private static final ObjectMapper objectMapper;

  static {
    objectMapper = new ObjectMapper();
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    objectMapper.registerModule(new JavaTimeModule());
    DateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss a z");
    objectMapper.setDateFormat(df);
  }

  private JsonHelper() {}

  public static <T> T fromJson(String json, Class<T> toClass) throws IOException {
    objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    return objectMapper.readValue(json, toClass);
  }

  public static <T> T fromJson(String json, TypeReference<T> typeReference) throws IOException {
    return objectMapper.readValue(json, typeReference);
  }

  public static <T> T fromJson(InputStream is, Class<T> toClass) throws IOException {
    return objectMapper.readValue(is, toClass);
  }

  /** Maps the given object to JSON using a standard Jackson mapper. */
  public static <T> String toJson(T object) throws JsonProcessingException
  {
    return objectMapper.writeValueAsString(object);
  }

  public static Object getJsonStringAsObject(String jsonString) throws JsonProcessingException
  {
    return objectMapper.readTree(jsonString);
  }
}
