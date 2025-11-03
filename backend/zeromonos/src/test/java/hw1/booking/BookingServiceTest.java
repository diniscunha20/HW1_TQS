package hw1.booking;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import hw1.booking.StatusChange;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

  MunicipalityClient muni = mock(MunicipalityClient.class);
  BookingRepository repo = mock(BookingRepository.class);
  BookingRules rules = mock(BookingRules.class);
  TokenService tokens = mock(TokenService.class);
  LimitsService limits = mock(LimitsService.class);

  BookingService service = new BookingService(repo, rules, muni, tokens, limits);


  @Test
  void updateStatus_acrescentaTimeline() {
    var b = new Booking("TOK","Ana","LX", LocalDate.now().plusDays(2),"AM", BookingStatus.RECEIVED);
    when(repo.findByToken("TOK")).thenReturn(Optional.of(b));

    var after1 = service.updateStatus("TOK", "CONFIRMED");
    assertThat(after1.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    assertThat(after1.getTimeline()).extracting(StatusChange::getStatus)
        .containsExactly(BookingStatus.RECEIVED, BookingStatus.CONFIRMED);

    var after2 = service.updateStatus("TOK", "IN_PROGRESS");
    assertThat(after2.getTimeline()).extracting(StatusChange::getStatus)
        .containsExactly(BookingStatus.RECEIVED, BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS);
  }
}