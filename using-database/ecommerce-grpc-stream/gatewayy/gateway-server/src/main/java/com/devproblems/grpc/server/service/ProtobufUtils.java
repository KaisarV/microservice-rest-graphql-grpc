package com.devproblems.grpc.server.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.protobuf.Descriptors;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.protobuf.ProtobufJsonFormatHttpMessageConverter;
import com.google.protobuf.Message;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProtobufUtils {
    public static Map<String, Object> protobufToMap(Message message) throws JsonProcessingException {
        Map<String, Object> map = new HashMap<>();
        for (Map.Entry<Descriptors.FieldDescriptor, Object> entry : message.getAllFields().entrySet()) {
            Object value = entry.getValue();
            if (value instanceof Message) {
                map.put(entry.getKey().getName(), protobufToMap((Message) value));
            } else {
                map.put(entry.getKey().getName(), value);
            }
        }
        return map;
    }
}