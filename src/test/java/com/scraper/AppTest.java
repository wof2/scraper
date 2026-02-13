package com.scraper;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AppTest {

    @Test
    void getGreeting() {
        App app = new App();
        assertEquals("Hello, World!", app.getGreeting());
    }
}
