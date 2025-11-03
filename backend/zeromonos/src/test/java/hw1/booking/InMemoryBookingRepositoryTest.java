package hw1.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;

class InMemoryBookingRepositoryTest {

    private InMemoryBookingRepository repository;

    @BeforeEach
    void setUp() {
        repository = new InMemoryBookingRepository();
    }

    @Test
    void save_andFindByToken_returnsBooking() {
        Booking booking = new Booking("TOK123", "Maria", "LX", 
            LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        
        repository.save(booking);
        
        Optional<Booking> found = repository.findByToken("TOK123");
        assertThat(found).isPresent();
        assertThat(found.get().getToken()).isEqualTo("TOK123");
        assertThat(found.get().getName()).isEqualTo("Maria");
    }

    @Test
    void findByToken_caseInsensitive() {
        Booking booking = new Booking("abc123", "João", "PT", 
            LocalDate.now().plusDays(3), "PM", BookingStatus.CONFIRMED);
        
        repository.save(booking);
        
        assertThat(repository.findByToken("ABC123")).isPresent();
        assertThat(repository.findByToken("abc123")).isPresent();
        assertThat(repository.findByToken("AbC123")).isPresent();
    }

    @Test
    void findByToken_notFound_returnsEmpty() {
        Optional<Booking> found = repository.findByToken("NONEXISTENT");
        assertThat(found).isEmpty();
    }

    @Test
    void findByMunicipality_returnsMatchingBookings() {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "LX", LocalDate.now().plusDays(2), "PM", BookingStatus.CONFIRMED);
        Booking b3 = new Booking("T3", "Carlos", "PT", LocalDate.now().plusDays(3), "AM", BookingStatus.RECEIVED);
        
        repository.save(b1);
        repository.save(b2);
        repository.save(b3);
        
        List<Booking> lxBookings = repository.findByMunicipality("LX");
        assertThat(lxBookings).hasSize(2);
        assertThat(lxBookings).extracting(Booking::getName).containsExactlyInAnyOrder("Ana", "Bruno");
    }

    @Test
    void findByMunicipality_caseInsensitive() {
        Booking booking = new Booking("T1", "Diana", "lx", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        repository.save(booking);
        
        assertThat(repository.findByMunicipality("LX")).hasSize(1);
        assertThat(repository.findByMunicipality("lx")).hasSize(1);
        assertThat(repository.findByMunicipality("Lx")).hasSize(1);
    }

    @Test
    void findByMunicipality_noMatches_returnsEmpty() {
        Booking booking = new Booking("T1", "Eva", "LX", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        repository.save(booking);
        
        List<Booking> results = repository.findByMunicipality("PT");
        assertThat(results).isEmpty();
    }

    @Test
    void findAll_returnsAllBookings() {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "PT", LocalDate.now().plusDays(2), "PM", BookingStatus.CONFIRMED);
        Booking b3 = new Booking("T3", "Carlos", "BR", LocalDate.now().plusDays(3), "AM", BookingStatus.IN_PROGRESS);
        
        repository.save(b1);
        repository.save(b2);
        repository.save(b3);
        
        List<Booking> all = repository.findAll();
        assertThat(all).hasSize(3);
        assertThat(all).extracting(Booking::getName).containsExactlyInAnyOrder("Ana", "Bruno", "Carlos");
    }

    @Test
    void findAll_emptyRepository_returnsEmptyList() {
        List<Booking> all = repository.findAll();
        assertThat(all).isEmpty();
    }

    @Test
    void countActiveByDate_countsNonCancelledBookings() {
        LocalDate targetDate = LocalDate.now().plusDays(5);
        
        Booking b1 = new Booking("T1", "Ana", "LX", targetDate, "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "PT", targetDate, "PM", BookingStatus.CONFIRMED);
        Booking b3 = new Booking("T3", "Carlos", "BR", targetDate, "AM", BookingStatus.CANCELLED);
        Booking b4 = new Booking("T4", "Diana", "LX", LocalDate.now().plusDays(6), "AM", BookingStatus.RECEIVED);
        
        repository.save(b1);
        repository.save(b2);
        repository.save(b3);
        repository.save(b4);
        
        long count = repository.countActiveByDate(targetDate);
        assertThat(count).isEqualTo(2);
    }

    @Test
    void countActiveByDate_noBookingsOnDate_returnsZero() {
        Booking booking = new Booking("T1", "Eva", "LX", LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        repository.save(booking);
        
        long count = repository.countActiveByDate(LocalDate.now().plusDays(10));
        assertThat(count).isEqualTo(0);
    }

    @Test
    void countActiveByDate_allCancelled_returnsZero() {
        LocalDate targetDate = LocalDate.now().plusDays(5);
        
        Booking b1 = new Booking("T1", "Ana", "LX", targetDate, "AM", BookingStatus.CANCELLED);
        Booking b2 = new Booking("T2", "Bruno", "PT", targetDate, "PM", BookingStatus.CANCELLED);
        
        repository.save(b1);
        repository.save(b2);
        
        long count = repository.countActiveByDate(targetDate);
        assertThat(count).isEqualTo(0);
    }

    @Test
    void save_updatesExistingBooking() {
        Booking booking = new Booking("TOK", "Maria", "LX", LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        repository.save(booking);
        
        booking.updateStatus(BookingStatus.CONFIRMED);
        repository.save(booking);
        
        Optional<Booking> found = repository.findByToken("TOK");
        assertThat(found).isPresent();
        assertThat(found.get().getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }
}
