package hw1.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;

class InMemoryBookingRepositoryTest {

  private InMemoryBookingRepository repository;

  @BeforeEach
  void setUp() { repository = new InMemoryBookingRepository(); }

  @Test
  void save_andFindByToken_returnsBooking() {
    var booking = new Booking("TOK123","Maria","LX", LocalDate.now().plusDays(5),"AM", BookingStatus.RECEIVED);
    repository.save(booking);
    Optional<Booking> found = repository.findByToken("TOK123");
    assertThat(found).isPresent();
    assertThat(found.get().getName()).isEqualTo("Maria");
  }

  @Test
  void countActiveByDate_ignoresCancelled() {
    var d = LocalDate.now().plusDays(5);
    repository.save(new Booking("T1","Ana","LX",d,"AM",BookingStatus.CANCELLED));
    repository.save(new Booking("T2","Bruno","LX",d,"PM",BookingStatus.RECEIVED));
    assertThat(repository.countActiveByDate(d)).isEqualTo(1);
  }

  @Test
  void save_twice_updates() {
    var b = new Booking("TOK","Maria","LX", LocalDate.now().plusDays(5),"AM", BookingStatus.RECEIVED);
    repository.save(b);
    b.updateStatus(BookingStatus.CONFIRMED);
    repository.save(b);
    assertThat(repository.findByToken("TOK")).get().extracting(Booking::getStatus)
      .isEqualTo(BookingStatus.CONFIRMED);
  }
}
