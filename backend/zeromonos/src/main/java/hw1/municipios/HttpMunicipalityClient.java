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

    // IMPORTANTE: guardar a referência do bean
    public HttpMunicipalityClient(WebClient geoApiWebClient) {
        this.geoApi = geoApiWebClient;
    }

    @Override
    public List<Municipality> listAll() {
        try {
            // JSON público: https://json.geoapi.pt/municipios?json=1
            List<Map<String,Object>> rows = geoApi.get()
                    .uri("/municipios?json=1")
                    .retrieve()
                    .bodyToMono(new ParameterizedTypeReference<List<Map<String,Object>>>() {})
                    .block();

            if (rows == null) return List.of();

            return rows.stream()
                    .map(this::asMunicipality)
                    .filter(Objects::nonNull)
                    .sorted(Comparator.comparing(Municipality::name))
                    .collect(Collectors.toList());

        } catch (Exception ex) {
            // fallback resiliente para não quebrares a demo se a Geo API falhar
            return List.of(
                    new Municipality("lisboa","Lisboa"),
                    new Municipality("porto","Porto"),
                    new Municipality("coimbra","Coimbra"),
                    new Municipality("braga","Braga")
            );
        }
    }

    private Municipality asMunicipality(Map<String,Object> m) {
        String name = firstNonBlank(
                getStr(m,"concelho"),
                getStr(m,"municipio"),
                getStr(m,"nome"),
                getStr(m,"name")
        );
        if (name == null || name.isBlank()) return null;
        return new Municipality(toCode(name), name);
    }

    private String getStr(Map<String,Object> m, String k) {
        Object v = m.get(k);
        return v == null ? null : String.valueOf(v);
    }

    private String firstNonBlank(String... vals) {
        for (var v : vals) if (v != null && !v.isBlank()) return v;
        return null;
    }

    private String toCode(String name) {
        // slug estável: sem acentos, minúsculas, hífens
        String n = Normalizer.normalize(name, Normalizer.Form.NFD)
                .replaceAll("\\p{M}+", "");
        return n.toLowerCase(Locale.ROOT).replaceAll("[^a-z0-9]+", "-");
    }
}
