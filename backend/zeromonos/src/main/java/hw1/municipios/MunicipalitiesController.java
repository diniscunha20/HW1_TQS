package hw1.municipios;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;

@RestController
@RequestMapping("/api")
public class MunicipalitiesController {
    private final MunicipalityClient client;

    public MunicipalitiesController(MunicipalityClient client) {
        this.client = client;
    }

    @GetMapping("/municipalities")
    public List<Municipality> listMunicipalities() {
        return client.listAll();
    }
}
