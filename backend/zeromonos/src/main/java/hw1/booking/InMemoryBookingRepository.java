package hw1.booking;

import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Repository
public class InMemoryBookingRepository implements BookingRepository {
    private final Map<String, Booking> byToken = new ConcurrentHashMap<>();

    @Override
    public void save(Booking b) {
        byToken.put(b.getToken().toUpperCase(Locale.ROOT), b);
    }

    @Override
    public Optional<Booking> findByToken(String token) {
        return Optional.ofNullable(byToken.get(token.toUpperCase(Locale.ROOT)));
    }

    @Override
    public List<Booking> findByMunicipality(String municipalityCode) {
        return byToken.values().stream()
                .filter(b -> b.getMunicipalityCode().equalsIgnoreCase(municipalityCode))
                .toList();
    }

    @Override
    public List<Booking> findAll() {
        return new ArrayList<>(byToken.values());
    }

    @Override 
    public long countActiveByDate(LocalDate date) {
        return byToken.values().stream()
        .filter(b -> date.equals(b.getDate()))
        .filter(b -> b.getStatus() != BookingStatus.CANCELLED) // ajusta se precisares
        .count();
  }
}
