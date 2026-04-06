package com.stockManagePortfolio.stockManager;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(properties = "JWT_SECRET=test-jwt-secret-key-with-32-characters")
class StockManagerApplicationTests {

    @Test
    void contextLoads() {
    }
}
