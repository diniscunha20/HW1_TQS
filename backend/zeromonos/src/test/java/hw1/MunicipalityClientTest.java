package hw1.municipios;

import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.web.reactive.function.client.WebClient;

import java.io.IOException;
import java.util.List;

import static org.assertj.core.api.Assertions.*;

class HttpMunicipalityClientTest {

  private MockWebServer server;
  private HttpMunicipalityClient client;

  @BeforeEach
  void setUp() throws IOException {
    server = new MockWebServer();
    server.start();
    String baseUrl = server.url("/").toString(); // ex: http://127.0.0.1:XXXXX/
    WebClient webClient = WebClient.builder().baseUrl(baseUrl).build();
    client = new HttpMunicipalityClient(webClient);
  }

  @AfterEach
  void tearDown() throws IOException {
    server.shutdown();
  }

  @Test
  void listAll_success_returnsSortedAndSlugified() {
    // A API devolve uma lista de strings (nomes)
    // inclui acentos e espaços para validar o slugify e o filtro de vazios
    String body = """
      ["Vila Nova de Famalicão","Braga","Lisboa","  ","Bragança","Évora"]
    """;
    server.enqueue(new MockResponse()
        .setResponseCode(200)
        .addHeader("Content-Type","application/json")
        .setBody(body));

    List<Municipality> items = client.listAll();

    // Verificações:
    // 1) removidos vazios e ordenação por name ascendente
    assertThat(items).extracting(Municipality::name)
      .containsExactly("Braga","Bragança","Évora","Lisboa","Vila Nova de Famalicão");

    // 2) slugs gerados sem acentos/espacos, em minúsculas
    assertThat(items).extracting(Municipality::code)
      .containsExactly("braga","braganca","evora","lisboa","vila-nova-de-famalicao");
  }

  @Test
  void listAll_emptyArray_throwsIllegalState() {
    server.enqueue(new MockResponse()
        .setResponseCode(200)
        .addHeader("Content-Type","application/json")
        .setBody("[]"));

    assertThatThrownBy(() -> client.listAll())
      .isInstanceOf(MunicipalityProviderException.class)
      .hasMessageContaining("GeoAPI down");
  }

  @Test
  void listAll_apiError_throwsIllegalState_wrapped() {
    server.enqueue(new MockResponse().setResponseCode(500).setBody("oops"));

    assertThatThrownBy(() -> client.listAll())
      .isInstanceOf(MunicipalityProviderException.class)
      .hasMessageContaining("GeoAPI down");
  }
}
