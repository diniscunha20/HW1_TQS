package hw1.booking;

import java.util.List;

public interface BookingRepository {
    void save(Booking booking);
    Booking findByToken(String token);
    List<Booking> findByMunicipality(String municipalityCode);
}
