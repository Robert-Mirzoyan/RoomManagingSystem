package com.example.project.configurations;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import java.time.LocalTime;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
@ActiveProfiles("test")
@TestPropertySource(properties = {
        "booking.open-time=08:00",
        "booking.close-time=22:00"
})
public class BookingTimeConfigTest {

    @Autowired
    private BookingTimeConfig bookingTimeConfig;

    @Test
    void testBookingTimesAreLoaded() {
        assertNotNull(bookingTimeConfig.getOpenTime());
        assertNotNull(bookingTimeConfig.getCloseTime());
    }

    @Test
    void testBookingTimesAreLoadedCorrectly() {
        assertEquals(bookingTimeConfig.getOpenTime(), LocalTime.of(8, 0));
        assertEquals(bookingTimeConfig.getCloseTime(), LocalTime.of(22, 0));
    }

    @Test
    void testBookingTimesAreUpdatedCorrectly() {
        bookingTimeConfig.setOpenTime(LocalTime.of(10, 0));
        bookingTimeConfig.setCloseTime(LocalTime.of(20, 0));

        assertEquals(bookingTimeConfig.getOpenTime(), LocalTime.of(10, 0));
        assertEquals(bookingTimeConfig.getCloseTime(), LocalTime.of(20, 0));
    }
}
