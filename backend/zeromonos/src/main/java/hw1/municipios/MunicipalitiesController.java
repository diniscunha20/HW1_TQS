package hw1.municipios;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
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

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntimeException(RuntimeException ex) {
        return ResponseEntity
            .status(HttpStatus.UNPROCESSABLE_ENTITY)
            .body("Erro ao obter municípios: " + ex.getMessage());
    }
}
