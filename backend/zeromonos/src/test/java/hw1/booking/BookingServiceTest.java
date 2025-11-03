package hw1.booking;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;
import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceTest {
  MunicipalityClient muni = mock(MunicipalityClient.class);
  BookingRepository repo = mock(BookingRepository.class);
  BookingRules rules = mock(BookingRules.class);
  TokenService tokens = mock(TokenService.class);
  LimitsService limits = mock(LimitsService.class);

  BookingService service = new BookingService(repo, rules, muni, tokens, limits);

  @Test
  void create_happyPath_validMunicipality_respectsLimits_persistsAndReturnsToken() {
    when(muni.listAll()).thenReturn(List.of(new Municipality("LX","Lisboa")));
    when(limits.getMaxPerDay()).thenReturn(20);
    when(repo.countActiveByDate(any())).thenReturn(5L);
    when(tokens.generate()).thenReturn("ABC123");

    var req = new CreateBookingRequest("João","LX", LocalDate.now().plusDays(5),"AM","desc");
    var resp = service.create(req);

    assertThat(resp.token()).isEqualTo("ABC123");
    ArgumentCaptor<Booking> cap = ArgumentCaptor.forClass(Booking.class);
    verify(repo).save(cap.capture());
    assertThat(cap.getValue().getStatus()).isEqualTo(BookingStatus.RECEIVED);
    verify(rules).validate(any());
  }

  @Test
  void create_rejectsUnknownMunicipality() {
    // finge que a capacidade está ok
    when(limits.getMaxPerDay()).thenReturn(100);
    when(repo.countActiveByDate(any())).thenReturn(0L);

    // só existe Porto; LX não existe → município inválido
    when(muni.listAll()).thenReturn(List.of(new Municipality("PT","Porto")));

    var req = new CreateBookingRequest("Ana","LX", LocalDate.now().plusDays(3),"AM","x");

    assertThatThrownBy(() -> service.create(req))
      .satisfies(ex -> assertThat(ex.getMessage()).containsIgnoringCase("município"));
  }


  @Test
  void countActiveByDate_delegatesToRepository() {
    LocalDate d = LocalDate.now().plusDays(5);
    when(repo.countActiveByDate(d)).thenReturn(10L);
    assertThat(service.countActiveByDate(d)).isEqualTo(10L);
    verify(repo).countActiveByDate(d);
  }

  @Test
  void updateStatus_buildsTimeline_andIsCaseInsensitive() {
    var b = new Booking("TOK","Ana","LX", LocalDate.now().plusDays(2),"AM", BookingStatus.RECEIVED);
    when(repo.findByToken("TOK")).thenReturn(Optional.of(b));

    var after1 = service.updateStatus("TOK", "CONFIRMED");
    assertThat(after1.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    assertThat(after1.getTimeline()).extracting(StatusChange::getStatus)
      .containsExactly(BookingStatus.RECEIVED, BookingStatus.CONFIRMED);

    var after2 = service.updateStatus("TOK", "in_progress"); // case-insensitive
    assertThat(after2.getTimeline()).extracting(StatusChange::getStatus)
      .containsExactly(BookingStatus.RECEIVED, BookingStatus.CONFIRMED, BookingStatus.IN_PROGRESS);
  }

  @Test
  void updateStatus_finishedOrCancelled_throws() {
    var b = new Booking("X","Ana","LX", LocalDate.now().plusDays(2),"AM", BookingStatus.CANCELLED);
    when(repo.findByToken("X")).thenReturn(Optional.of(b));
    assertThatThrownBy(() -> service.updateStatus("X","CONFIRMED"))
      .isInstanceOf(IllegalStateException.class)
      .satisfies(ex -> assertThat(ex.getMessage()).containsIgnoringCase("finalizada"));
  }

  @Test
  void updateStatus_unknownToken_throws() {
    when(repo.findByToken("ZZZ")).thenReturn(Optional.empty());
    assertThatThrownBy(() -> service.updateStatus("ZZZ","CONFIRMED"))
      .satisfies(ex -> assertThat(ex.getMessage()).containsIgnoringCase("Token"));
  }

  @Test
  void getByMunicipality_delegatesToRepository() {
    var list = List.of(
      new Booking("t1","A","braga", LocalDate.now().plusDays(1),"AM", BookingStatus.RECEIVED)
    );
    when(repo.findByMunicipality("braga")).thenReturn(list);

    assertThat(service.getByMunicipality("braga")).isEqualTo(list);
    verify(repo).findByMunicipality("braga");
  }

  @Test
  void updateStatus_invalidStatusText_throwsIllegalArgumentException() {
    var b = new Booking("TOK","Ana","LX", LocalDate.now().plusDays(3),"AM", BookingStatus.RECEIVED);
    when(repo.findByToken("TOK")).thenReturn(Optional.of(b));

    assertThatThrownBy(() -> service.updateStatus("TOK","NOT_A_REAL_STATUS"))
      .isInstanceOf(IllegalArgumentException.class)
      .satisfies(ex -> assertThat(ex.getMessage()).containsIgnoringCase("Estado inválido"));
  }
}
