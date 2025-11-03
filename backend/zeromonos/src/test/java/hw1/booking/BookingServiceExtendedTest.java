package hw1.booking;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class BookingServiceExtendedTest {

    private BookingRepository repo;
    private BookingRules rules;
    private MunicipalityClient muni;
    private TokenService tokens;
    private LimitsService limitsService;
    private BookingService service;

    @BeforeEach
    void setUp() {
        repo = mock(BookingRepository.class);
        rules = mock(BookingRules.class);
        muni = mock(MunicipalityClient.class);
        tokens = mock(TokenService.class);
        limitsService = mock(LimitsService.class);
        service = new BookingService(repo, rules, muni, tokens, limitsService);
    }

    @Test
    void create_validBooking_returnsResponse() {
        when(limitsService.getMaxPerDay()).thenReturn(20);
        when(repo.countActiveByDate(any())).thenReturn(5L);
        when(tokens.generate()).thenReturn("ABC123");
        when(muni.listAll()).thenReturn(List.of(
            new Municipality("LX", "Lisboa")
        ));

        CreateBookingRequest request = new CreateBookingRequest(
            "João Silva",
            "LX",
            LocalDate.now().plusDays(5),
            "AM",
            "Test booking"
        );

        CreateBookingResponse response = service.create(request);

        assertThat(response.token()).isEqualTo("ABC123");
        assertThat(response.status()).isEqualTo("RECEIVED");
        verify(repo).save(any(Booking.class));
        verify(rules).registerExisting(any(Booking.class));
    }

    @Test
    void create_capacityReached_throwsException() {
        when(limitsService.getMaxPerDay()).thenReturn(20);
        when(repo.countActiveByDate(any())).thenReturn(20L);

        CreateBookingRequest request = new CreateBookingRequest(
            "Maria",
            "LX",
            LocalDate.now().plusDays(5),
            "AM",
            "Description"
        );

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Capacidade diária global esgotada");

        verify(repo, never()).save(any());
    }

    @Test
    void create_invalidMunicipality_throwsException() {
        when(limitsService.getMaxPerDay()).thenReturn(20);
        when(repo.countActiveByDate(any())).thenReturn(5L);
        when(muni.listAll()).thenReturn(List.of(
            new Municipality("LX", "Lisboa")
        ));

        CreateBookingRequest request = new CreateBookingRequest(
            "Pedro",
            "INVALID",
            LocalDate.now().plusDays(5),
            "AM",
            "Description"
        );

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResponseStatusException.class);

        verify(repo, never()).save(any());
    }

    @Test
    void create_geoApiFailure_throwsException() {
        when(limitsService.getMaxPerDay()).thenReturn(20);
        when(repo.countActiveByDate(any())).thenReturn(5L);
        when(muni.listAll()).thenThrow(new RuntimeException("API unavailable"));

        CreateBookingRequest request = new CreateBookingRequest(
            "Ana",
            "LX",
            LocalDate.now().plusDays(5),
            "AM",
            "Description"
        );

        assertThatThrownBy(() -> service.create(request))
            .isInstanceOf(ResponseStatusException.class)
            .hasMessageContaining("Erro ao obter municípios da GeoAPI");

        verify(repo, never()).save(any());
    }

    @Test
    void getByToken_existingToken_returnsBooking() {
        Booking booking = new Booking("TOK123", "Maria", "LX", 
            LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        when(repo.findByToken("TOK123")).thenReturn(Optional.of(booking));

        Booking result = service.getByToken("TOK123");

        assertThat(result).isEqualTo(booking);
        assertThat(result.getName()).isEqualTo("Maria");
    }

    @Test
    void getByToken_nonExistingToken_throwsException() {
        when(repo.findByToken("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.getByToken("INVALID"))
            .isInstanceOf(BookingNotFoundException.class)
            .hasMessageContaining("Token não encontrado");
    }

    @Test
    void getAll_returnsAllBookings() {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "PT", LocalDate.now().plusDays(2), "PM", BookingStatus.CONFIRMED);
        when(repo.findAll()).thenReturn(List.of(b1, b2));

        List<Booking> result = service.getAll();

        assertThat(result).hasSize(2);
        assertThat(result).extracting(Booking::getName).containsExactly("Ana", "Bruno");
    }

    @Test
    void getByMunicipality_returnsFilteredBookings() {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        when(repo.findByMunicipality("LX")).thenReturn(List.of(b1));

        List<Booking> result = service.getByMunicipality("LX");

        assertThat(result).hasSize(1);
        assertThat(result.get(0).getMunicipalityCode()).isEqualTo("LX");
    }

    @Test
    void updateStatus_validTransition_updatesBooking() {
        Booking booking = new Booking("TOK", "Ana", "LX", 
            LocalDate.now().plusDays(2), "AM", BookingStatus.RECEIVED);
        when(repo.findByToken("TOK")).thenReturn(Optional.of(booking));

        Booking result = service.updateStatus("TOK", "CONFIRMED");

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
        verify(repo).save(booking);
    }

    @Test
    void updateStatus_invalidStatus_throwsException() {
        Booking booking = new Booking("TOK", "Ana", "LX", 
            LocalDate.now().plusDays(2), "AM", BookingStatus.RECEIVED);
        when(repo.findByToken("TOK")).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.updateStatus("TOK", "INVALID_STATUS"))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("Estado inválido");
    }

    @Test
    void updateStatus_cancelledBooking_throwsException() {
        Booking booking = new Booking("TOK", "Ana", "LX", 
            LocalDate.now().plusDays(2), "AM", BookingStatus.CANCELLED);
        when(repo.findByToken("TOK")).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.updateStatus("TOK", "CONFIRMED"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Marcação finalizada/cancelada");
    }

    @Test
    void updateStatus_completedBooking_throwsException() {
        Booking booking = new Booking("TOK", "Ana", "LX", 
            LocalDate.now().plusDays(2), "AM", BookingStatus.COMPLETED);
        when(repo.findByToken("TOK")).thenReturn(Optional.of(booking));

        assertThatThrownBy(() -> service.updateStatus("TOK", "CONFIRMED"))
            .isInstanceOf(IllegalStateException.class)
            .hasMessageContaining("Marcação finalizada/cancelada");
    }

    @Test
    void updateStatus_caseInsensitive_updatesBooking() {
        Booking booking = new Booking("TOK", "Ana", "LX", 
            LocalDate.now().plusDays(2), "AM", BookingStatus.RECEIVED);
        when(repo.findByToken("TOK")).thenReturn(Optional.of(booking));

        Booking result = service.updateStatus("TOK", "confirmed");

        assertThat(result.getStatus()).isEqualTo(BookingStatus.CONFIRMED);
    }

    @Test
    void updateStatus_nonExistingToken_throwsException() {
        when(repo.findByToken("INVALID")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> service.updateStatus("INVALID", "CONFIRMED"))
            .isInstanceOf(BookingNotFoundException.class)
            .hasMessageContaining("Token não encontrado");
    }

    @Test
    void countActiveByDate_delegatesToRepository() {
        LocalDate date = LocalDate.now().plusDays(5);
        when(repo.countActiveByDate(date)).thenReturn(10L);

        long result = service.countActiveByDate(date);

        assertThat(result).isEqualTo(10L);
        verify(repo).countActiveByDate(date);
    }
}
