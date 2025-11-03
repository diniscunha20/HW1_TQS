package hw1.booking;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BookingRules {

    private final LimitsService limitsService;
    private final Set<String> validSlots;
    private final Map<String, Integer> existentes = new HashMap<>();

    public BookingRules(LimitsService limitsService, String[] validTimeSlots) {
        this.limitsService = limitsService;
        this.validSlots = Set.of(validTimeSlots);
    }

    public void validate(BookingRequest req) {
        // data tem de ser futura (ou troca a regra conforme precisares)
        if (req.getDate().isBefore(LocalDate.now()) || req.getDate().isEqual(LocalDate.now())) {
            throw new IllegalArgumentException("data inválida (passada)");
        }
        if (!validSlots.contains(req.getTimeSlot())) {
            throw new IllegalArgumentException("timeSlot inválido: " + req.getTimeSlot());
        }
        int currentForDay = 0;
        for (String slot : validSlots) {
            currentForDay += existentes.getOrDefault(key(req.getMunicipalityCode(), req.getDate(), slot), 0);
        }
        if (currentForDay >= limitsService.getMaxPerDay()) {
            throw new IllegalStateException("Capacidade diária esgotada");
        }
    }

    public void registerExisting(Booking booking) {
        String key = key(booking.getMunicipalityCode(), booking.getDate(), booking.getTimeSlot());
        if (booking.getStatus() == BookingStatus.CONFIRMED||booking.getStatus() == BookingStatus.RECEIVED) {
            existentes.merge(key, 1, Integer::sum);
        }
    }

    private String key(String muni, LocalDate date, String slot) {
        return muni + "|" + date + "|" + slot;
    }
}
