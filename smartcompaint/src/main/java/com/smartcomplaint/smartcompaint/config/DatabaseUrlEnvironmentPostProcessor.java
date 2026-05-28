package com.smartcomplaint.smartcompaint.config;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.MapPropertySource;

import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public class DatabaseUrlEnvironmentPostProcessor implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        String databaseUrl = firstPresent(
                environment.getProperty("DB_URL"),
                environment.getProperty("DATABASE_URL")
        );

        if (databaseUrl == null || databaseUrl.isBlank() || databaseUrl.startsWith("jdbc:")) {
            return;
        }

        if (!databaseUrl.startsWith("postgresql://") && !databaseUrl.startsWith("postgres://")) {
            return;
        }

        URI uri = URI.create(databaseUrl);
        String database = uri.getPath() == null ? "" : uri.getPath().replaceFirst("^/", "");
        String query = uri.getRawQuery() == null ? "" : "?" + uri.getRawQuery();
        String jdbcUrl = "jdbc:postgresql://" + uri.getHost() + ":" + port(uri) + "/" + database + query;
        Map<String, Object> properties = new HashMap<>();
        properties.put("spring.datasource.url", jdbcUrl);

        String rawUserInfo = uri.getRawUserInfo();
        if (rawUserInfo != null && rawUserInfo.contains(":")) {
            String[] parts = rawUserInfo.split(":", 2);
            properties.put("spring.datasource.username", decode(parts[0]));
            properties.put("spring.datasource.password", decode(parts[1]));
        }

        environment.getPropertySources().addFirst(new MapPropertySource("databaseUrlAdapter", properties));
    }

    private String firstPresent(String first, String second) {
        return first != null && !first.isBlank() ? first : second;
    }

    private int port(URI uri) {
        return uri.getPort() == -1 ? 5432 : uri.getPort();
    }

    private String decode(String value) {
        return URLDecoder.decode(value, StandardCharsets.UTF_8);
    }
}
