package com.example.project.controller;

import com.example.project.dto.RoomCreateDto;
import com.example.project.dto.RoomReadDto;
import com.example.project.service.RoomRestService;
import jakarta.validation.Valid;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@Validated
public class RoomRestController {

    private final RoomRestService roomRestService;

    public RoomRestController(RoomRestService roomRestService) {
        this.roomRestService = roomRestService;
    }

    @GetMapping
    public ResponseEntity<Page<RoomReadDto>> getAllRooms(Pageable pageable) {
        return ResponseEntity.ok(roomRestService.getAllRooms(pageable));
    }

    @GetMapping("/{id}")
    public ResponseEntity<RoomReadDto> getRoomById(@PathVariable Integer id) {
        RoomReadDto dto = roomRestService.getRoomById(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(dto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRoomById(@PathVariable Integer id) {
        RoomReadDto dto = roomRestService.deleteRoom(id);
        if (dto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.noContent().build();
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PutMapping("/{id}")
    public ResponseEntity<RoomReadDto> updateRoomById(@PathVariable Integer id,@Valid @RequestBody RoomCreateDto dto) {
        RoomReadDto createDto = roomRestService.updateRoomById(id, dto);
        if (createDto == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(createDto);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping
    public ResponseEntity<RoomReadDto> createRoom(@Valid @RequestBody RoomCreateDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(roomRestService.createRoom(dto));
    }

    @GetMapping("/filter")
    public ResponseEntity<Page<RoomReadDto>> filterRooms(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String type,
            @RequestParam(required = false) int minCapacity,
            Pageable pageable
    ) {
        return ResponseEntity.ok(roomRestService.filterRooms(name, type, minCapacity, pageable));
    }
}