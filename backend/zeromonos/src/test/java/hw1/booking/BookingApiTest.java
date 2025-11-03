package hw1.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import static org.hamcrest.Matchers.hasSize;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingApiTest {

  @Autowired MockMvc mvc;
  @MockBean BookingService service;
  @MockBean LimitsService limits;

  @Test
  void POST_bookings_201_returnsToken() throws Exception {
    when(service.create(any())).thenReturn(new CreateBookingResponse("abc123token", "ignored"));
    mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"name":"João","municipalityCode":"LX","date":"2025-11-10","timeSlot":"AM","description":"Colchão"}
        """))
      .andExpect(status().isCreated())
      .andExpect(jsonPath("$.token").value("abc123token"));
  }
  @Test
  void POST_bookings_400_invalidPayload() throws Exception {
    mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"name":"","municipalityCode":"","date":"2020-01-01","timeSlot":"NOITE","description":""}
        """))
      .andExpect(status().isBadRequest());
  }
  @Test
  void GET_bookingByToken_200() throws Exception {
    var b = new Booking("TOK","Ana","LX", LocalDate.of(2025,11,10),"AM", BookingStatus.RECEIVED);
    when(service.getByToken("TOK")).thenReturn(b);
    mvc.perform(get("/api/bookings/TOK"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.token").value("TOK"))
      .andExpect(jsonPath("$.status").value("RECEIVED"));
  }
  @Test
  void PATCH_updateStatus_200_returnsUpdated() throws Exception {
    var updated = new Booking("TOK","Ana","LX", LocalDate.of(2025,11,10),"AM", BookingStatus.CONFIRMED);
    when(service.updateStatus(eq("TOK"), eq("CONFIRMED"))).thenReturn(updated);
    mvc.perform(patch("/api/bookings/TOK/status")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"status\":\"CONFIRMED\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("CONFIRMED"));
  }
  @Test
  void GET_countByDate_200_plainNumber() throws Exception {
    var date = LocalDate.of(2025,11,10);
    when(service.countActiveByDate(date)).thenReturn(15L);
    mvc.perform(get("/api/bookings/count").param("date","2025-11-10"))
      .andExpect(status().isOk())
      .andExpect(content().string("15"));
  }
  @Test
  void GET_countByDate_400_missingDate() throws Exception {
    mvc.perform(get("/api/bookings/count")).andExpect(status().isBadRequest());
  }
  @Test
  void GET_countByDate_422_invalidDate() throws Exception {
    mvc.perform(get("/api/bookings/count").param("date","bad"))
      .andExpect(status().isUnprocessableEntity());
  }
}