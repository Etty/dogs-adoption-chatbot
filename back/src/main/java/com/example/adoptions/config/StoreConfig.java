package com.example.adoptions.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

@Configuration
public class StoreConfig {
    private final Environment env;

    public StoreConfig(Environment env) {
        this.env = env;
    }

    public String getAgencyName() {
        return env.getProperty("agency.name");
    }
    public String getAgencyPhone() {
        return env.getProperty("agency.phone");
    }
    public String getAgencyServeFrom() {
        return env.getProperty("agency.serve.from");
    }

    public String getAgencyServeTo() {
        return env.getProperty("agency.serve.to");
    }
}
