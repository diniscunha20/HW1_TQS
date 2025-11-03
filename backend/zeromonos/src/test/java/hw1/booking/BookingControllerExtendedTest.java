package hw1.booking;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingControllerExtendedTest {

    @Autowired
    private MockMvc mvc;

    @MockBean
    private BookingService service;

    @MockBean
    private LimitsService limitsService;

    @Test
    void getByToken_existingToken_returns200() throws Exception {
        Booking booking = new Booking("ABC123", "Maria", "LX", 
            LocalDate.now().plusDays(5), "AM", BookingStatus.RECEIVED);
        when(service.getByToken("ABC123")).thenReturn(booking);

        mvc.perform(get("/api/bookings/ABC123"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.token").value("ABC123"))
            .andExpect(jsonPath("$.name").value("Maria"))
            .andExpect(jsonPath("$.municipalityCode").value("LX"))
            .andExpect(jsonPath("$.status").value("RECEIVED"));
    }

    @Test
    void getByToken_nonExistingToken_returns422() throws Exception {
        when(service.getByToken("INVALID")).thenThrow(new BookingNotFoundException("Token não encontrado"));

        mvc.perform(get("/api/bookings/INVALID"))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void listByMunicipality_withoutFilter_returnsAll() throws Exception {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        Booking b2 = new Booking("T2", "Bruno", "PT", LocalDate.now().plusDays(2), "PM", BookingStatus.CONFIRMED);
        when(service.getAll()).thenReturn(List.of(b1, b2));

        mvc.perform(get("/api/bookings"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(2)))
            .andExpect(jsonPath("$[0].token").value("T1"))
            .andExpect(jsonPath("$[1].token").value("T2"));
    }

    @Test
    void listByMunicipality_withFilter_returnsFiltered() throws Exception {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        when(service.getByMunicipality("LX")).thenReturn(List.of(b1));

        mvc.perform(get("/api/bookings").param("municipalityCode", "LX"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$", hasSize(1)))
            .andExpect(jsonPath("$[0].municipalityCode").value("LX"));
    }

    @Test
    void listByMunicipality_withBlankFilter_returnsAll() throws Exception {
        Booking b1 = new Booking("T1", "Ana", "LX", LocalDate.now().plusDays(1), "AM", BookingStatus.RECEIVED);
        when(service.getAll()).thenReturn(List.of(b1));

        mvc.perform(get("/api/bookings").param("municipalityCode", ""))
            .andExpect(status().isOk());
        
        verify(service).getAll();
    }

    @Test
    void updateStatus_validRequest_returns200() throws Exception {
        Booking booking = new Booking("TOK", "Ana", "LX", 
            LocalDate.now().plusDays(2), "AM", BookingStatus.CONFIRMED);
        when(service.updateStatus("TOK", "CONFIRMED")).thenReturn(booking);

        mvc.perform(patch("/api/bookings/TOK/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.status").value("CONFIRMED"));
    }

    @Test
    void updateStatus_missingStatus_returns400() throws Exception {
        mvc.perform(patch("/api/bookings/TOK/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_nullPayload_returns422() throws Exception {
        mvc.perform(patch("/api/bookings/TOK/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateStatus_blankStatus_returns400() throws Exception {
        mvc.perform(patch("/api/bookings/TOK/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"\"}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateStatus_invalidStatus_returns422() throws Exception {
        when(service.updateStatus("TOK", "INVALID"))
            .thenThrow(new IllegalArgumentException("Estado inválido"));

        mvc.perform(patch("/api/bookings/TOK/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"INVALID\"}"))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateStatus_finalizedBooking_returns422() throws Exception {
        when(service.updateStatus("TOK", "CONFIRMED"))
            .thenThrow(new IllegalStateException("Marcação finalizada"));

        mvc.perform(patch("/api/bookings/TOK/status")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"status\":\"CONFIRMED\"}"))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void getGlobalLimit_returnsCurrentLimit() throws Exception {
        when(limitsService.getMaxPerDay()).thenReturn(25);

        mvc.perform(get("/api/bookings/limits"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.maxPerDay").value(25));
    }

    @Test
    void updateGlobalLimit_validValue_returns204() throws Exception {
        mvc.perform(put("/api/bookings/limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"maxPerDay\":30}"))
            .andExpect(status().isNoContent());

        verify(limitsService).setMaxPerDay(30);
    }

    @Test
    void updateGlobalLimit_nullDto_returns422() throws Exception {
        mvc.perform(put("/api/bookings/limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("null"))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void updateGlobalLimit_nullMaxPerDay_returns400() throws Exception {
        mvc.perform(put("/api/bookings/limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{}"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void updateGlobalLimit_invalidValue_returns422() throws Exception {
        doThrow(new IllegalArgumentException("maxPerDay > 0"))
            .when(limitsService).setMaxPerDay(0);

        mvc.perform(put("/api/bookings/limits")
                .contentType(MediaType.APPLICATION_JSON)
                .content("{\"maxPerDay\":0}"))
            .andExpect(status().isUnprocessableEntity());
    }

    @Test
    void countByDate_withDate_returnsCount() throws Exception {
        LocalDate date = LocalDate.of(2025, 11, 10);
        when(service.countActiveByDate(date)).thenReturn(15L);

        mvc.perform(get("/api/bookings/count")
                .param("date", "2025-11-10"))
            .andExpect(status().isOk())
            .andExpect(content().string("15"));
    }

    @Test
    void countByDate_withDateAndActiveOnly_returnsCount() throws Exception {
        LocalDate date = LocalDate.of(2025, 11, 10);
        when(service.countActiveByDate(date)).thenReturn(10L);

        mvc.perform(get("/api/bookings/count")
                .param("date", "2025-11-10")
                .param("activeOnly", "true"))
            .andExpect(status().isOk())
            .andExpect(content().string("10"));
    }

    @Test
    void countByDate_missingDate_returns400() throws Exception {
        mvc.perform(get("/api/bookings/count"))
            .andExpect(status().isBadRequest());
    }

    @Test
    void countByDate_invalidDateFormat_returns422() throws Exception {
        mvc.perform(get("/api/bookings/count")
                .param("date", "invalid-date"))
            .andExpect(status().isUnprocessableEntity());
    }
}
