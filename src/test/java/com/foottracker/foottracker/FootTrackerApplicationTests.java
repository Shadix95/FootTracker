package com.foottracker.foottracker;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:contexttest")
class FootTrackerApplicationTests {

    @Test
    void contextLoads() {
    }

}
