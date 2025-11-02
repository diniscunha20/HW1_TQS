package hw1.booking;

import java.util.*;

public interface BookingRepository {
    void save(Booking booking);
    Optional<Booking> findByToken(String token);
    List<Booking> findAll();
    List<Booking> findByMunicipality(String municipalityCode);
}
