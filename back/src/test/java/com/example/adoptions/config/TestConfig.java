package com.example.adoptions.config;

import org.mockito.Mockito;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;

@TestConfiguration
public class TestConfig {


        @Bean
        VectorStore mockVectorStore() {
            // Use a mock for tests so that DogListener / AppointmentListener can be constructed
            return Mockito.mock(VectorStore.class);
        }

}
