package hw1.booking;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = BookingController.class)
class BookingApiTest {

  @Autowired MockMvc mvc;

  @MockBean BookingService service;

  @MockBean LimitsService limits;

  @Test
  void POST_bookings_returns201WithToken() throws Exception {
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
  void POST_bookings_invalidInput_returns422() throws Exception {
    mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content("""
          {"name":"","municipalityCode":"","date":"2020-01-01","timeSlot":"NOITE","description":""}
        """))
      .andExpect(status().isUnprocessableEntity());
  }
}
