package hw1;

import hw1.municipios.MunicipalitiesController;
import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(controllers = MunicipalitiesController.class)
class MunicipalityApiTest {

    @Autowired MockMvc mvc;

    @MockBean MunicipalityClient client;

    @Test
    void GET_municipalities_returns200AndList() throws Exception {
        when(client.listAll()).thenReturn(List.of(
            new Municipality("LX","Lisboa"),
            new Municipality("PRT","Porto")
        ));

        mvc.perform(get("/api/municipalities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].code").value("LX"))
            .andExpect(jsonPath("$[0].name").value("Lisboa"))
            .andExpect(jsonPath("$[1].code").value("PRT"));
    }

    @Test
    void GET_municipalities_200_listaVazia() throws Exception {
        when(client.listAll()).thenReturn(List.of());

        mvc.perform(get("/api/municipalities"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(content().json("[]"));
    }
    @Test
    void GET_municipalities_erroNoClient_422_textPlain() throws Exception {
        when(client.listAll()).thenThrow(new RuntimeException("GeoAPI down"));

        mvc.perform(get("/api/municipalities"))
            .andExpect(status().isUnprocessableEntity())
            .andExpect(content().contentTypeCompatibleWith("text/plain"))
            .andExpect(content().string(org.hamcrest.Matchers.containsString("Erro ao obter municípios")));
    }

    @Test
    void GET_municipalities_sucesso_respondeJSON() throws Exception {
        when(client.listAll()).thenReturn(List.of(new Municipality("LX","Lisboa")));
        mvc.perform(get("/api/municipalities"))
        .andExpect(status().isOk())
        .andExpect(content().contentTypeCompatibleWith("application/json"))
        .andExpect(jsonPath("$[0].code").value("LX"))
        .andExpect(jsonPath("$[0].name").value("Lisboa"));
    }
}
