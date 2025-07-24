package com.example.project.controller;

import com.example.project.dto.RoomCreateDto;
import com.example.project.dto.RoomReadDto;
import com.example.project.service.RoomRestService;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = RoomRestController.class)
@Import(RoomRestControllerTest.TestConfig.class)
class RoomRestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private RoomRestService mockService;

    @TestConfiguration
    static class TestConfig {
        @Bean
        public RoomRestService roomRestService() {
            return Mockito.mock(RoomRestService.class);
        }
    }

    @Test
    void testGetRoomById() throws Exception {
        RoomReadDto dto = new RoomReadDto(1, "A", "Lab", 30);
        Mockito.when(mockService.getRoomById(1)).thenReturn(dto);

        mockMvc.perform(get("/api/room/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A"));
    }

    @Test
    void testGetRoomByInvalidId() throws Exception {
        mockMvc.perform(get("/api/room/100"))
                .andExpect(status().isNotFound());
    }

    @Test
    void testGetAllRooms() throws Exception {
        List<RoomReadDto> rooms = List.of(
                new RoomReadDto(1, "A", "Lab", 30),
                new RoomReadDto(2, "B", "Lab", 30),
                new RoomReadDto(3, "C", "Lab", 30)
        );

        Mockito.when(mockService.getAllRooms()).thenReturn(rooms);

        mockMvc.perform(get("/api/room"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(3))
                .andExpect(jsonPath("$[0].name").value("A"))
                .andExpect(jsonPath("$[1].type").value("Lab"))
                .andExpect(jsonPath("$[2].capacity").value(30));
    }

    @Test
    void testCreateRoom() throws Exception {
        RoomReadDto readDto = new RoomReadDto(1, "A", "Lab", 30);

        Mockito.when(mockService.createRoom(Mockito.any(RoomCreateDto.class))).thenReturn(readDto);

        String requestJson = """
        {
            "name": "A",
            "type": "Lab",
            "capacity": 30
        }
        """;

        mockMvc.perform(post("/api/room")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.name").value("A"))
                .andExpect(jsonPath("$.type").value("Lab"))
                .andExpect(jsonPath("$.capacity").value(30));
    }

    @Test
    void testUpdateRoom() throws Exception {
        RoomReadDto readDto = new RoomReadDto(1, "A", "Lab", 30);

        Mockito.when(mockService.updateRoomById(Mockito.eq(1), Mockito.any())).thenReturn(readDto);

        String requestJson = """
        {
            "name": "A",
            "type": "Lab",
            "capacity": 30
        }
        """;

        mockMvc.perform(put("/api/room/1")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("A"))
                .andExpect(jsonPath("$.type").value("Lab"))
                .andExpect(jsonPath("$.capacity").value(30));
    }

    @Test
    void testInvalidUpdateRoom() throws Exception {
        RoomReadDto readDto = new RoomReadDto(1, "A", "Lab", 30);

        Mockito.when(mockService.updateRoomById(Mockito.eq(1), Mockito.any())).thenReturn(readDto);

        String requestJson = """
        {
            "name": "A",
            "type": "Lab",
            "capacity": 30
        }
        """;

        mockMvc.perform(put("/api/room/100")
                        .contentType("application/json")
                        .content(requestJson))
                .andExpect(status().isNotFound());
    }

    @Test
    void testDeleteRoomById() throws Exception {
        RoomReadDto dto = new RoomReadDto(1, "A", "Lab", 30);
        Mockito.when(mockService.deleteRoom(1)).thenReturn(dto);

        mockMvc.perform(delete("/api/room/1"))
                .andExpect(status().isNoContent())
                .andExpect(jsonPath("$.name").value("A"));
    }

    @Test
    void testInvalidDeleteRoomById() throws Exception {
        RoomReadDto dto = new RoomReadDto(1, "A", "Lab", 30);
        Mockito.when(mockService.deleteRoom(1)).thenReturn(dto);

        mockMvc.perform(delete("/api/room/100"))
                .andExpect(status().isNotFound());
    }
}

