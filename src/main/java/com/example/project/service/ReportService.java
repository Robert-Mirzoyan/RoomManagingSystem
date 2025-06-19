package com.example.project.service;

import com.example.project.model.Booking;
import com.example.project.model.Room;
import com.example.project.model.Status;
import com.example.project.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;

@Service
public class ReportService {
    private final RoomRepository roomRepository;

    @Autowired
    public ReportService(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public List<Room> mostBookedRooms(int topN) {
        Map<Room, Integer> usageMap = generateUsageReport();
        List<Room> sortedRooms = new ArrayList<>(usageMap.keySet());

        sortedRooms.sort((r1, r2) -> usageMap.get(r2) - usageMap.get(r1));
        return sortedRooms.subList(0, Math.min(topN, sortedRooms.size()));
    }

    private Map<Room, Integer> generateUsageReport() {
        List<Room> rooms = roomRepository.findAll();
        Map<Room, Integer> report = new HashMap<>();

        for (Room room : rooms) {
            int approvedCount = (int) room.getBookings().stream()
                    .filter(booking -> booking.getStatus() == Status.APPROVED)
                    .count();

            report.put(room, approvedCount);
        }
        return report;
    }

    public List<Booking> roomUsageByDate(LocalDate date) {
        return roomRepository.findAll().stream().flatMap(room -> room.getBookings().stream())
                .filter(booking -> booking.getStatus() == Status.APPROVED)
                .filter(booking -> booking.getStartTime().toLocalDate().equals(date))
                .toList();
    }
}
