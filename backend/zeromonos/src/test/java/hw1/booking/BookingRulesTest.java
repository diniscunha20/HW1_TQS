package hw1.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;

import static org.assertj.core.api.Assertions.*;

class BookingRulesTest {

    private BookingRules rules;

    @BeforeEach
    void setUp() {
        rules = new BookingRules(10, new String[]{"AM", "PM", "EVENING"});
    }

    @Test
    void validate_futureDate_passes() {
        BookingRequest request = new BookingRequest(
            "Ana Silva",
            "LX",
            LocalDate.now().plusDays(5),
            "AM",
            "Test booking"
        );

        assertThatCode(() -> rules.validate(request)).doesNotThrowAnyException();
    }

    @Test
    void validate_pastDate_throwsException() {
        BookingRequest request = new BookingRequest(
            "Ana Silva",
            "LX",
            LocalDate.now().minusDays(1),
            "AM",
            "Test booking"
        );

        assertThatThrownBy(() -> rules.validate(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("data inválida (passada)");
    }

    @Test
    void validate_todayDate_throwsException() {
        BookingRequest request = new BookingRequest(
            "Ana Silva",
            "LX",
            LocalDate.now(),
            "AM",
            "Test booking"
        );

        assertThatThrownBy(() -> rules.validate(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("data inválida (passada)");
    }

    @Test
    void validate_validTimeSlot_passes() {
        BookingRequest requestAM = new BookingRequest(
            "Ana", "LX", LocalDate.now().plusDays(5), "AM", "Test"
        );
        BookingRequest requestPM = new BookingRequest(
            "Bruno", "PT", LocalDate.now().plusDays(5), "PM", "Test"
        );
        BookingRequest requestEvening = new BookingRequest(
            "Carlos", "BR", LocalDate.now().plusDays(5), "EVENING", "Test"
        );

        assertThatCode(() -> rules.validate(requestAM)).doesNotThrowAnyException();
        assertThatCode(() -> rules.validate(requestPM)).doesNotThrowAnyException();
        assertThatCode(() -> rules.validate(requestEvening)).doesNotThrowAnyException();
    }

    @Test
    void validate_invalidTimeSlot_throwsException() {
        BookingRequest request = new BookingRequest(
            "Ana Silva",
            "LX",
            LocalDate.now().plusDays(5),
            "NIGHT",
            "Test booking"
        );

        assertThatThrownBy(() -> rules.validate(request))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("timeSlot inválido");
    }

    @Test
    void registerExisting_receivedBooking_registersBooking() {
        Booking booking = new Booking(
            "TOK1",
            "Ana",
            "LX",
            LocalDate.now().plusDays(5),
            "AM",
            BookingStatus.RECEIVED
        );

        assertThatCode(() -> rules.registerExisting(booking)).doesNotThrowAnyException();
    }

    @Test
    void registerExisting_confirmedBooking_registersBooking() {
        Booking booking = new Booking(
            "TOK1",
            "Ana",
            "LX",
            LocalDate.now().plusDays(5),
            "AM",
            BookingStatus.CONFIRMED
        );

        assertThatCode(() -> rules.registerExisting(booking)).doesNotThrowAnyException();
    }

    @Test
    void registerExisting_cancelledBooking_doesNotRegister() {
        Booking booking = new Booking(
            "TOK1",
            "Ana",
            "LX",
            LocalDate.now().plusDays(5),
            "AM",
            BookingStatus.CANCELLED
        );

        assertThatCode(() -> rules.registerExisting(booking)).doesNotThrowAnyException();
    }

    @Test
    void registerExisting_multipleBookingsSameMunicipalityDateSlot_registersAll() {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "LX", LocalDate.now().plusDays(5), "AM", BookingStatus.CONFIRMED);
        Booking b3 = new Booking("T3", "Carlos", "LX", LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);

        rules.registerExisting(b1);
        rules.registerExisting(b2);
        rules.registerExisting(b3);

        // Should not throw any exceptions
        assertThatCode(() -> {
            rules.registerExisting(b1);
            rules.registerExisting(b2);
            rules.registerExisting(b3);
        }).doesNotThrowAnyException();
    }

    @Test
    void registerExisting_differentMunicipalities_registersIndependently() {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "PT", LocalDate.now().plusDays(5), "AM", BookingStatus.CONFIRMED);

        rules.registerExisting(b1);
        rules.registerExisting(b2);

        assertThatCode(() -> {
            rules.registerExisting(b1);
            rules.registerExisting(b2);
        }).doesNotThrowAnyException();
    }

    @Test
    void registerExisting_differentDates_registersIndependently() {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "LX", LocalDate.now().plusDays(6), "AM", BookingStatus.CONFIRMED);

        rules.registerExisting(b1);
        rules.registerExisting(b2);

        assertThatCode(() -> {
            rules.registerExisting(b1);
            rules.registerExisting(b2);
        }).doesNotThrowAnyException();
    }

    @Test
    void registerExisting_differentTimeSlots_registersIndependently() {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "LX", LocalDate.now().plusDays(5), "PM", BookingStatus.CONFIRMED);

        rules.registerExisting(b1);
        rules.registerExisting(b2);

        assertThatCode(() -> {
            rules.registerExisting(b1);
            rules.registerExisting(b2);
        }).doesNotThrowAnyException();
    }
}
