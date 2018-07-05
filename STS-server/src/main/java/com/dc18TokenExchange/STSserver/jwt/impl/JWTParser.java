package com.dc18TokenExchange.STSserver.jwt.impl;

import com.dc18TokenExchange.STSserver.jwt.exceptions.JWTDecodeException;
import com.dc18TokenExchange.STSserver.jwt.interfaces.Header;
import com.dc18TokenExchange.STSserver.jwt.interfaces.JWTPartsParser;
import com.dc18TokenExchange.STSserver.jwt.interfaces.Payload;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.module.SimpleModule;

import java.io.IOException;

public class JWTParser implements JWTPartsParser {
    private ObjectMapper mapper;

    public JWTParser() {
        this(getDefaultObjectMapper());
    }

    JWTParser(ObjectMapper mapper) {
        addDeserializers(mapper);
        this.mapper = mapper;
    }

    @Override
    public Payload parsePayload(String json) throws JWTDecodeException {
        return convertFromJSON(json, Payload.class);
    }

    @Override
    public Header parseHeader(String json) throws JWTDecodeException {
        return convertFromJSON(json, Header.class);
    }

    private void addDeserializers(ObjectMapper mapper) {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(Payload.class, new PayloadDeserializer());
        module.addDeserializer(Header.class, new HeaderDeserializer());
        mapper.registerModule(module);
    }

    static ObjectMapper getDefaultObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.disable(SerializationFeature.FAIL_ON_EMPTY_BEANS);
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper;
    }

    @SuppressWarnings("WeakerAccess")
    <T> T convertFromJSON(String json, Class<T> tClazz) throws JWTDecodeException {
        if (json == null) {
            throw exceptionForInvalidJson(null);
        }
        try {
            return mapper.readValue(json, tClazz);
        } catch (IOException e) {
            throw exceptionForInvalidJson(json);
        }
    }

    private JWTDecodeException exceptionForInvalidJson(String json) {
        return new JWTDecodeException(String.format("The string '%s' doesn't have a valid JSON format.", json));
    }
}