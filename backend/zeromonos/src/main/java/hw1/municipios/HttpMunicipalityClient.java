package hw1.municipios;

import org.springframework.core.ParameterizedTypeReference;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.text.Normalizer;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class HttpMunicipalityClient implements MunicipalityClient {

    private final WebClient geoApi;

    public HttpMunicipalityClient(WebClient geoApiWebClient) {
        this.geoApi = geoApiWebClient;
    }

    @Override
    public List<Municipality> listAll() {
        try {
            List<String> names = geoApi.get()
                .uri("/municipios?json=1")
                .retrieve()
                .bodyToMono(new ParameterizedTypeReference<List<String>>() {})
                .block();

            if (names == null || names.isEmpty()) {
                throw new IllegalStateException("GeoAPI respondeu vazio");
            }

            return names.stream()
                .filter(Objects::nonNull)
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .map(name -> new Municipality(slugify(name), name))
                .sorted(Comparator.comparing(Municipality::name))
                .toList();

        } catch (Exception ex) {
            System.err.println("[WARN] Falha ao obter municípios da GeoAPI: " + ex.getMessage());
            throw new IllegalStateException("Erro ao obter municípios da GeoAPI", ex);
        }
    }


    private Municipality mapToMunicipality(Map<String, Object> raw) {
        Object concelho = raw.get("concelho");
        Object municipio = raw.get("municipio");
        Object nome = raw.get("nome");
        String name = concelho != null ? concelho.toString()
                : municipio != null ? municipio.toString()
                : nome != null ? nome.toString()
                : null;

        if (name == null || name.isBlank()) return null;
        return new Municipality(slugify(name), name);
    }

    private String slugify(String name) {
        return Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}", "")
                .replaceAll("[^a-zA-Z0-9]+", "-")
                .toLowerCase(Locale.ROOT);
    }
}
