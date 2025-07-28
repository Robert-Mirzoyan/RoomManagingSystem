package com.example.project.controller;

import com.example.project.dto.RoomCreateDto;
import com.example.project.dto.RoomReadDto;
import com.example.project.service.RoomRestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/room")
@Validated
public class RoomRestController {

    private final RoomRestService roomRestService;

    public RoomRestController(RoomRestService roomRestService) {
        this.roomRestService = roomRestService;
    }

    @GetMapping
    public ResponseEntity<List<RoomReadDto>> getAllRooms() {
        return ResponseEntity.ok(roomRestService.getAllRooms());
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomReadDto> getRoomById(@PathVariable Integer id) {
        RoomReadDto dto = roomRestService.getRoomById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<RoomReadDto> deleteRoomById(@PathVariable Integer id) {
        RoomReadDto dto = roomRestService.deleteRoom(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.status(HttpStatus.NO_CONTENT).body(dto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<RoomReadDto> updateRoomById(@PathVariable Integer id,@Valid @RequestBody RoomCreateDto dto) {
        RoomReadDto createDto = roomRestService.updateRoomById(id, dto);
        if (createDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(createDto);
    }

    @PostMapping
    public ResponseEntity<RoomReadDto> createRoom(@Valid @RequestBody RoomCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomRestService.createRoom(dto));
    }
}