package hw1.config;

import java.util.List;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.http.MediaType;

@Configuration
public class HttpClientsConfig {

    @Bean
    public WebClient geoApiWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl("https://json.geoapi.pt")  // endpoint JSON oficial da GEO API PT
            .defaultHeaders(h -> h.setAccept(List.of(MediaType.APPLICATION_JSON)))
            .build();
    }
}
