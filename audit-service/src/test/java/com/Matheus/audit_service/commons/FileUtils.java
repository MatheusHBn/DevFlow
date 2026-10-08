package com.Matheus.audit_service.commons;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.file.Files;

@Component
public class FileUtils {

    @Autowired
    private ResourceLoader resourceLoader;

    private final ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();

    public String readResourceFile(String fileName) throws IOException {
        var file = resourceLoader.getResource("classpath:%s".formatted(fileName)).getFile();
        return new String(Files.readAllBytes(file.toPath()));
    }

    public <T> T readResourceAsObject(String path, Class<T> destinyClass) throws Exception {
        String json = readResourceFile(path);
        return mapper.readValue(json, destinyClass);
    }
}
