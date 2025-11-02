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
}
