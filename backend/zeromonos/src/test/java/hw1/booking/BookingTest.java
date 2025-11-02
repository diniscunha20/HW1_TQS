package hw1.booking;

import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class BookingRulesTest {

  @Test
  void naoPermiteDataPassada() {
    var rules = new BookingRules(/*capacidadeDia=*/20, new String[]{"AM","PM"});
    var req = new BookingRequest("Jose", "LX", LocalDate.now().minusDays(1), "AM", "Colchão");
    assertThatThrownBy(() -> rules.validate(req))
      .hasMessageContaining("data");
  }

  @Test
  void recusaTimeSlotInvalido() {
    var rules = new BookingRules(20, new String[]{"AM","PM"});
    var req = new BookingRequest("Jose", "LX", LocalDate.now().plusDays(1), "NOITE", "Colchão");
    assertThatThrownBy(() -> rules.validate(req))
      .hasMessageContaining("timeSlot");
  }

  @Test
  void recusaCapacidadeExcedidaNoMesmoDiaESlotEMunicipio() {
    var rules = new BookingRules(1, new String[]{"AM","PM"});
    var dia = LocalDate.now().plusDays(2);

    // simula que já existe 1 marcação para LX, dia e slot "AM"
    rules.registerExisting(new Booking("tok1", "Jose", "LX", dia, "AM", BookingStatus.RECEIVED));

    var novo = new BookingRequest("Jose", "LX", dia, "AM", "Colchão");
    assertThatThrownBy(() -> rules.validate(novo))
      .hasMessageContaining("capacidade");
  }
}
