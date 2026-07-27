package com.aibankapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class AiBankAppApplicationTests {

    @Test
    void contextLoads() {
        // Verifies the Spring application context starts successfully with the test profile (H2 in-memory DB).
    }
}
