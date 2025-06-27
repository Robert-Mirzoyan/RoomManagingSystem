package com.example.project.exercise;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
class DirtyContextTest {

    @Autowired
    Counter counter;

    @Test
    @DirtiesContext
    void testA() {
        counter.increment();
        Assertions.assertEquals(1, counter.getValue());
    }

    @Test
    void testB() {
        Assertions.assertEquals(0, counter.getValue());
    }

    @TestConfiguration
    static class TestConfig {
        @Bean
        public Counter counter() {
            return new Counter();
        }
    }

    static class Counter {
        private int value = 0;

        public void increment() {
            value++;
        }

        public int getValue() {
            return value;
        }
    }
}

