package hw1.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

@Configuration
public class HttpClientsConfig {

    @Bean
    public WebClient geoApiWebClient(WebClient.Builder builder) {
        return builder
            .baseUrl("https://json.geoapi.pt")  // endpoint JSON oficial da GEO API PT
            .build();
    }
}
