package com.example.adoptions;

import com.example.adoptions.controller.AdoptionsController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class AdoptionsApplicationTests {
    @Autowired
    private AdoptionsController adoptionsController;

    @Test
    void contextLoads() {
        // ensure that context is creating controller
        assertThat(adoptionsController).isNotNull();
    }
}
