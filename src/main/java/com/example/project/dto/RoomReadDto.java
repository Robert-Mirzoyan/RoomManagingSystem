package com.example.project.dto;

import com.example.project.validation.ValidRoomName;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Getter
public class RoomReadDto {

    private int id;

    @NotBlank(message = "Name must not be blank")
    @Size(max = 50, message = "Name must not exceed 50 characters")
    @ValidRoomName
    private String name;

    @NotBlank(message = "Type is required")
    private String type;

    @Min(value = 10, message = "Capacity must be at least 10")
    @Max(value = 50, message = "Capacity must be at most 50")
    private int capacity;
}
