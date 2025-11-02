package hw1.booking;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import java.time.LocalDate;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

class BookingServiceTest {

  MunicipalityClient muni = mock(MunicipalityClient.class);
  BookingRepository repo = mock(BookingRepository.class);
  BookingRules rules = mock(BookingRules.class);
  TokenService tokens = mock(TokenService.class);

  BookingService service = new BookingService(repo, rules, muni, tokens);

  @Test
  void create_validaMunicipioRegrasPersisteEGeraToken() {
    when(muni.listAll()).thenReturn(List.of(new Municipality("LX","Lisboa")));
    when(tokens.generate()).thenReturn("tok123");

    var req = new CreateBookingRequest("Jose", "LX", LocalDate.now().plusDays(1), "AM", "Colchão");
    var res = service.create(req);

    assertThat(res.token()).isEqualTo("tok123");
    verify(rules).validate(any());
    var captor = ArgumentCaptor.forClass(Booking.class);
    verify(repo).save(captor.capture());
    assertThat(captor.getValue().getStatus()).isEqualTo(BookingStatus.RECEIVED);
  }

  @Test
  void create_rejeitaMunicipioInexistente() {
    when(muni.listAll()).thenReturn(List.of(new Municipality("PRT","Porto"))); // LX não existe

    var req = new CreateBookingRequest("Jose", "LX", LocalDate.now().plusDays(1), "AM", "Colchão");
    assertThatThrownBy(() -> service.create(req))
      .hasMessageContaining("município");
  }
}