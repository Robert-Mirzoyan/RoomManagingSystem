package com.example.project.repository;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.transaction.annotation.Transactional;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ActiveProfiles("test")
@Sql(scripts = "/sql/room_test_data.sql")
@Transactional
public class RoomRepositoryIntegrationTest {

    @Autowired
    private RoomRepository roomRepository;

    @ParameterizedTest
    @DisplayName("Should find all rooms by IDs")
    @ValueSource(ints = {10, 11, 12})
    void testFindById(int id) {
        assertTrue(roomRepository.findById(id).isPresent());
    }

    @ParameterizedTest
    @DisplayName("Should delete all rooms by IDs")
    @ValueSource(ints = {10, 11, 12})
    void testDeleteById(int id) {
        roomRepository.deleteById(id);
        assertTrue(roomRepository.findById(id).isEmpty());
    }

}
