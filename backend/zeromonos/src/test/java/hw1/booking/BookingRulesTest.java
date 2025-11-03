package hw1.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.time.LocalDate;
import static org.assertj.core.api.Assertions.*;

class BookingRulesTest {

  private BookingRules rules;

  @BeforeEach
  void setUp() {
    rules = new BookingRules(10, new String[]{"AM","PM","EVENING"});
  }

  @Test
  void validate_rejectsPastOrToday() {
    var past = new BookingRequest("Ana","LX", LocalDate.now().minusDays(1),"AM","x");
    var today = new BookingRequest("Ana","LX", LocalDate.now(),"AM","x");
    assertThatThrownBy(() -> rules.validate(past)).satisfies(ex -> assertThat(ex.getMessage()).containsIgnoringCase("data"));
    assertThatThrownBy(() -> rules.validate(today)).satisfies(ex -> assertThat(ex.getMessage()).containsIgnoringCase("data"));
    var tomorrow = new BookingRequest("Ana","LX", LocalDate.now().plusDays(1),"AM","x");
    assertThatCode(() -> rules.validate(tomorrow)).doesNotThrowAnyException();
  }

  @Test
  void validate_rejectsInvalidTimeSlot() {
    var req = new BookingRequest("Ana","LX", LocalDate.now().plusDays(3),"NOITE","x");
    assertThatThrownBy(() -> rules.validate(req)).satisfies(ex -> assertThat(ex.getMessage()).containsIgnoringCase("time"));
  }

  @Test
  void capacity_respectsActiveOnly() {
    var day = LocalDate.now().plusDays(5);
    var b1 = new Booking("t1","Ana","LX",day,"AM",BookingStatus.RECEIVED);
    var b2 = new Booking("t2","Bruno","LX",day,"AM",BookingStatus.CONFIRMED);
    var b3 = new Booking("t3","Cris","LX",day,"AM",BookingStatus.CANCELLED);
    rules.registerExisting(b1);
    rules.registerExisting(b2);
    rules.registerExisting(b3); // não conta
    // ainda cabem 8
    assertThatCode(() -> rules.validate(new BookingRequest("Z","LX",day,"AM","x"))).doesNotThrowAnyException();
  }

  @Test
  void differentDatesOrSlots_areIndependent() {
    var d1 = LocalDate.now().plusDays(5);
    var d2 = d1.plusDays(1);
    rules.registerExisting(new Booking("a","N","LX",d1,"AM",BookingStatus.RECEIVED));
    rules.registerExisting(new Booking("b","N","LX",d1,"PM",BookingStatus.RECEIVED));
    rules.registerExisting(new Booking("c","N","LX",d2,"AM",BookingStatus.RECEIVED));
    assertThatCode(() -> rules.validate(new BookingRequest("ok","LX",d1,"AM","x"))).doesNotThrowAnyException();
    assertThatCode(() -> rules.validate(new BookingRequest("ok","LX",d1,"PM","x"))).doesNotThrowAnyException();
    assertThatCode(() -> rules.validate(new BookingRequest("ok","LX",d2,"AM","x"))).doesNotThrowAnyException();
  }
}
