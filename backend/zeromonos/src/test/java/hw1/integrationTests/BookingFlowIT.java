// src/test/java/hw1/integrationtests/BookingFlowIT.java
package hw1.integrationtests;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.assertj.core.api.Assertions.assertThat;

@AutoConfigureMockMvc
class BookingFlowIT extends hw1.integrationTests.AbstractIntegrationTest {

  @Autowired MockMvc mvc;

  @MockBean MunicipalityClient muni; // mock só do externo

  @Test
  void endToEnd_create_list_updateStatus() throws Exception {
    // 🔧 Garante que a GeoAPI “existe” e contém o código LX
    when(muni.listAll()).thenReturn(List.of(
      new Municipality("LX", "Lisboa"),
      new Municipality("PRT", "Porto")
    ));

    // 1) POST cria booking (usa código que existe na lista mockada!)
    String body = """
      {
        "name":"João",
        "municipalityCode":"LX",
        "date":"2025-11-10",
        "timeSlot":"AM",
        "description":"Colchão"
      }
      """;

    var post = mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())             // ✅ 201
      .andExpect(content().contentTypeCompatibleWith("application/json"))
      .andExpect(jsonPath("$.token").exists())
      .andReturn();

    // extrai token
    String json = post.getResponse().getContentAsString();
    Matcher m = Pattern.compile("\"token\"\\s*:\\s*\"([^\"]+)\"").matcher(json);
    assertThat(m.find()).isTrue();
    String token = m.group(1);

    // 2) GET por município (param name conforme o teu controller)
    mvc.perform(get("/api/bookings").param("municipality", "LX"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith("application/json"))
      .andExpect(jsonPath("$[0].municipalityCode").value("LX"))
      .andExpect(jsonPath("$[0].status").value("RECEIVED"));

    // 3) PATCH atualiza estado
    mvc.perform(patch("/api/bookings/{token}/status", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"status\":\"CONFIRMED\"}"))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("CONFIRMED"));

    // 4) GET por token confirma estado atualizado
    mvc.perform(get("/api/bookings/{token}", token))
      .andExpect(status().isOk())
      .andExpect(jsonPath("$.status").value("CONFIRMED"));
  }
}
