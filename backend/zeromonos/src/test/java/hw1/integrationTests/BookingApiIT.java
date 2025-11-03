package hw1.integrationTests;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;


@AutoConfigureMockMvc
class BookingApiIT extends AbstractIntegrationTest {

  @Autowired MockMvc mvc;

  // Mock apenas do fornecedor externo (GeoAPI)
  @MockBean MunicipalityClient muni;

  @Test
  void POST_bookings_missingBody_returns400() throws Exception {
    mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON))
      .andExpect(status().isBadRequest());
  }

  @Test
  void POST_bookings_missingRequiredField_returns400() throws Exception {
    // Faltam municipalityCode e date/timeSlot
    String body = """
      {
        "name":"João",
        "description":"Colchão"
      }
      """;

    mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isBadRequest());
  }

  @Test
  void POST_bookings_valid_returns201WithToken() throws Exception {
    // disponibiliza municípios válidos para a validação do serviço
    when(muni.listAll()).thenReturn(List.of(
        new Municipality("lisboa", "Lisboa"),
        new Municipality("porto", "Porto")
    ));

    String body = """
      {
        "name":"João",
        "municipalityCode":"lisboa",
        "date":"2025-11-10",
        "timeSlot":"AM",
        "description":"Colchão"
      }
      """;

    mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(body))
      .andExpect(status().isCreated())
      .andExpect(content().contentTypeCompatibleWith("application/json"))
      .andExpect(jsonPath("$.token").exists());
  }

  @Test
  void PATCH_updateStatus_missingStatus_returns400() throws Exception {
    // Primeiro cria uma marcação válida para obter um token
    when(muni.listAll()).thenReturn(List.of(new Municipality("lisboa", "Lisboa")));
    String create = """
      {
        "name":"João",
        "municipalityCode":"lisboa",
        "date":"2025-11-10",
        "timeSlot":"AM",
        "description":"Colchão"
      }
      """;

    var res = mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(create))
      .andExpect(status().isCreated())
      .andReturn();

    // extrai token rápido (sem mapper)
    String json = res.getResponse().getContentAsString();
    String token = json.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");

    // PATCH sem "status" no body → 400
    mvc.perform(patch("/api/bookings/{token}/status", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{}"))
      .andExpect(status().isBadRequest());
  }

  @Test
  void PATCH_updateStatus_invalidStatus_returns422() throws Exception {
    when(muni.listAll()).thenReturn(List.of(new Municipality("lisboa", "Lisboa")));
    String create = """
      {
        "name":"João",
        "municipalityCode":"lisboa",
        "date":"2025-11-10",
        "timeSlot":"AM",
        "description":"Colchão"
      }
      """;

    var res = mvc.perform(post("/api/bookings")
        .contentType(MediaType.APPLICATION_JSON)
        .content(create))
      .andExpect(status().isCreated())
      .andReturn();

    String json = res.getResponse().getContentAsString();
    String token = json.replaceAll(".*\"token\"\\s*:\\s*\"([^\"]+)\".*", "$1");

    // "status" presente mas inválido → regra de negócio → 422
    mvc.perform(patch("/api/bookings/{token}/status", token)
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"status\":\"NOT_A_REAL_STATUS\"}"))
      .andExpect(status().isUnprocessableEntity());
  }
}
