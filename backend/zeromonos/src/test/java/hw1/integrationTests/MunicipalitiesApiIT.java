package hw1.integrationTests;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
class MunicipalitiesApiIT {

  @Autowired MockMvc mvc;

  // Mock apenas do fornecedor externo
  @MockBean MunicipalityClient client;

  @Test
  void GET_municipalities_endToEnd_ok() throws Exception {
    when(client.listAll()).thenReturn(List.of(
      new Municipality("lisboa","Lisboa"),
      new Municipality("porto","Porto")
    ));

    mvc.perform(get("/api/municipalities"))
      .andExpect(status().isOk())
      .andExpect(content().contentTypeCompatibleWith("application/json"))
      .andExpect(jsonPath("$[0].code").value("lisboa"))
      .andExpect(jsonPath("$[0].name").value("Lisboa"))
      .andExpect(jsonPath("$[1].code").value("porto"));
  }

  @Test
  void GET_municipalities_clientErro_422_textPlain() throws Exception {
    when(client.listAll()).thenThrow(new RuntimeException("GeoAPI down"));

    mvc.perform(get("/api/municipalities"))
      .andExpect(status().isUnprocessableEntity())
      .andExpect(content().contentTypeCompatibleWith("text/plain"))
      .andExpect(content().string(org.hamcrest.Matchers.containsString("Erro ao obter municípios")));
  }
}
