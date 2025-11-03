package hw1.booking;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class BookingRules {

    private final int capacidadeDia;
    private final Set<String> validSlots;
    // chave = municipio|data|slot → contagem existente
    private final Map<String, Integer> existentes = new HashMap<>();

    public BookingRules(int capacidadeDia, String[] validTimeSlots) {
        this.capacidadeDia = capacidadeDia;
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
