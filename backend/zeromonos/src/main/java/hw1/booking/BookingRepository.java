package hw1.booking;

import java.util.*;
import java.time.LocalDate;

public interface BookingRepository {
    void save(Booking booking);
    Optional<Booking> findByToken(String token);
    List<Booking> findAll();
    List<Booking> findByMunicipality(String municipalityCode);
    long countActiveByDate(LocalDate date);
}
