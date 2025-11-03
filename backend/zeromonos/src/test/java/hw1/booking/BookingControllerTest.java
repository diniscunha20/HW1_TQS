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
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
@WebMvcTest(controllers = BookingController.class)

class BookingControllerTest {
  @Autowired MockMvc mvc;
  @MockBean BookingService service;
  @MockBean LimitsService limitsService;

  @Test
  void GET_list_withoutQuery_returnsAll() throws Exception {
    var b1 = new Booking("T1","Ana","lx", LocalDate.of(2025,11,10),"AM", BookingStatus.RECEIVED);
    var b2 = new Booking("T2","Bruno","lx", LocalDate.of(2025,11,11),"PM", BookingStatus.CONFIRMED);
    when(service.getAll()).thenReturn(List.of(b1,b2));
    mvc.perform(get("/api/bookings"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$", hasSize(2)))
      .andExpect(jsonPath("$[0].token").value("T1"))
      .andExpect(jsonPath("$[1].token").value("T2"));
    verify(service).getAll();
    verify(service, never()).getByMunicipality(any());
  }
  @Test
  void GET_list_withMunicipality_filtersByMunicipality() throws Exception {
    var b = new Booking("T3","Carla","braga", LocalDate.of(2025,11,12),"AM", BookingStatus.RECEIVED);
    when(service.getByMunicipality("braga")).thenReturn(List.of(b));
    mvc.perform(get("/api/bookings").param("municipalityCode","braga"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$", hasSize(1)))
      .andExpect(jsonPath("$[0].municipalityCode").value("braga"));
    verify(service).getByMunicipality("braga");
    verify(service, never()).getAll();
  }
  @Test
  void PATCH_updateStatus_ok_returnsBookingUpdated() throws Exception {
    var updated = new Booking("TOK","Ana","lx", LocalDate.of(2025,11,10),"AM", BookingStatus.IN_PROGRESS);
    when(service.updateStatus(eq("TOK"), eq("IN_PROGRESS"))).thenReturn(updated);
    mvc.perform(patch("/api/bookings/TOK/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"status\":\"IN_PROGRESS\"}"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.token").value("TOK"))
      .andExpect(jsonPath("$.status").value("IN_PROGRESS"));
    verify(service).updateStatus("TOK","IN_PROGRESS");
  }
  @Test
  void PATCH_updateStatus_missingStatus_returns422() throws Exception {
    mvc.perform(patch("/api/bookings/TOK/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"foo\":\"bar\"}"))
      .andExpect(status().isBadRequest());
    mvc.perform(patch("/api/bookings/TOK/status")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isUnprocessableEntity());
    verify(service, never()).updateStatus(any(), any());
  }
  @Test
  void GET_limits_returnsCurrentValue() throws Exception {
    when(limitsService.getMaxPerDay()).thenReturn(25);
    mvc.perform(get("/api/bookings/limits"))
      .andExpect(status().isOk())
      .andExpect(content().contentType(MediaType.APPLICATION_JSON))
      .andExpect(jsonPath("$.maxPerDay").value(25));
    verify(limitsService).getMaxPerDay();
  }
  @Test
  void PUT_limits_updatesAndReturns204() throws Exception {
    mvc.perform(put("/api/bookings/limits")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"maxPerDay\":42}"))
      .andExpect(status().isNoContent());
    verify(limitsService).setMaxPerDay(42);
  }
  @Test
  void PUT_limits_missingBodyOrField_returns422() throws Exception {
    mvc.perform(put("/api/bookings/limits")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isUnprocessableEntity());
    mvc.perform(put("/api/bookings/limits")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isBadRequest());
    verify(limitsService, never()).setMaxPerDay(anyInt());
  }
}