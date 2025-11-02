package hw1.booking;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class BookingTest {

  @Test
  void naoPermiteDataPassada() {
    var rules = new BookingRules(20, new String[]{"AM","PM"});
    var req = new BookingRequest("Jose", "LX", LocalDate.now().minusDays(1), "AM", "Colchão");
    assertThatThrownBy(() -> rules.validate(req))
      .hasMessageMatching("(?i).*data.*");
  }

  @Test
  void rejeitaDiaDeHoje() {
    var rules = new BookingRules(20, new String[]{"AM","PM"});
    var hoje = LocalDate.now();

    var reqHoje = new BookingRequest("Jose", "LX", hoje, "AM", "Colchão");
    assertThatThrownBy(() -> rules.validate(reqHoje))
      .hasMessageMatching("(?i).*data.*");

    var reqAmanha = new BookingRequest("Jose", "LX", hoje.plusDays(1), "AM", "Colchão");
    assertThatCode(() -> rules.validate(reqAmanha)).doesNotThrowAnyException();
  }

  @Test
  void recusaTimeSlotInvalido() {
    var rules = new BookingRules(20, new String[]{"AM","PM"});
    var req = new BookingRequest("Jose", "LX", LocalDate.now().plusDays(1), "NOITE", "Colchão");
    assertThatThrownBy(() -> rules.validate(req))
      .hasMessageMatching("(?i).*timeslot.*");
  }

  @Test
  void recusaCapacidadeExcedidaNoMesmoDiaESlotEMunicipio() {
    var rules = new BookingRules(1, new String[]{"AM","PM"});
    var dia = LocalDate.now().plusDays(2);

    rules.registerExisting(new Booking("tok1", "Jose", "LX", dia, "AM", BookingStatus.RECEIVED));

    var novo = new BookingRequest("Jose", "LX", dia, "AM", "Colchão");
    assertThatThrownBy(() -> rules.validate(novo))
      .hasMessageMatching("(?i).*capacidade.*");
  }

  @Test
  void capacidadeIndependentePorMunicipio_ePorSlot() {
    var rules = new BookingRules(1, new String[]{"AM","PM"});
    var dia = LocalDate.now().plusDays(2);

    // LX/AM já cheio
    rules.registerExisting(new Booking("tok1", "Jose", "LX", dia, "AM", BookingStatus.RECEIVED));

    // Mesmo slot e dia, mas município diferente → deve passar
    var reqOutroMuni = new BookingRequest("Ana", "PRT", dia, "AM", "Colchão");
    assertThatCode(() -> rules.validate(reqOutroMuni)).doesNotThrowAnyException();

    // Mesmo município e dia, mas slot diferente → deve passar
    var reqOutroSlot = new BookingRequest("Ana", "LX", dia, "PM", "Colchão");
    assertThatCode(() -> rules.validate(reqOutroSlot)).doesNotThrowAnyException();
  }

  @Test
  void aceitaNovasReservasDentroDaCapacidade() {
    var rules = new BookingRules(2, new String[]{"AM","PM"});
    var dia = LocalDate.now().plusDays(2);

    rules.registerExisting(new Booking("tok1", "Jose", "LX", dia, "AM", BookingStatus.RECEIVED));

    var novo = new BookingRequest("Ana", "LX", dia, "AM", "Colchão");
    assertThatCode(() -> rules.validate(novo)).doesNotThrowAnyException();
  }

  @Test
  void naoRegistraBookingsCancelados() {
    var rules = new BookingRules(3, new String[]{"AM","PM"});
    var dia = LocalDate.now().plusDays(2);

    var b1 = new Booking("tok1", "Jose", "LX", dia, "AM", BookingStatus.RECEIVED);
    var b2 = new Booking("tok2", "Ana", "LX", dia, "AM", BookingStatus.CONFIRMED);
    var b3 = new Booking("tok3", "Luis", "LX", dia, "AM", BookingStatus.CANCELLED);

    rules.registerExisting(b1);
    rules.registerExisting(b2);
    rules.registerExisting(b3); // não deve contar

    var novo = new BookingRequest("Maria", "LX", dia, "AM", "Colchão");
    assertThatCode(() -> rules.validate(novo)).doesNotThrowAnyException();
  }

  @Test
  void aceitaTimeSlotsValidos() {
    var rules = new BookingRules(20, new String[]{"AM","PM"});

    var req1 = new BookingRequest("Jose", "LX", LocalDate.now().plusDays(1), "AM", "Colchão");
    var req2 = new BookingRequest("Ana", "LX", LocalDate.now().plusDays(1), "PM", "Colchão");

    assertThatCode(() -> rules.validate(req1)).doesNotThrowAnyException();
    assertThatCode(() -> rules.validate(req2)).doesNotThrowAnyException();
  }

}
