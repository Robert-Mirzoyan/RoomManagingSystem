package Service;

import Model.Booking;
import Model.Room;
import Model.Status;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.*;

@Service
public class ReportService {
    private final BookingService bookingService;

    @Autowired
    public ReportService(BookingService bookingService) {
        this.bookingService = bookingService;
    }

    public Map<Room, Integer> generateUsageReport() {
        Map<Room, Integer> report = new HashMap<>();
        for (Booking booking : bookingService.getAllBookings()) {
            if (booking.getStatus() != Status.APPROVED) continue;

            Room room = booking.getRoom();
            report.put(room, report.getOrDefault(room, 0) + 1);
        }
        return report;
    }

    public List<Room> mostBookedRooms(int topN) {
        Map<Room, Integer> usageMap = generateUsageReport();
        List<Room> sortedRooms = new ArrayList<>(usageMap.keySet());

        sortedRooms.sort((r1, r2) -> usageMap.get(r2) - usageMap.get(r1));
        return sortedRooms.subList(0, Math.min(topN, sortedRooms.size()));
    }

    public List<Booking> roomUsageByDate(LocalDate date) {
        List<Booking> result = new ArrayList<>();
        for (Booking booking : bookingService.getAllBookings()) {
            if (booking.getStatus() == Status.APPROVED &&
                    booking.getStartTime().toLocalDate().equals(date)) {
                result.add(booking);
            }
        }
        return result;
    }
}
