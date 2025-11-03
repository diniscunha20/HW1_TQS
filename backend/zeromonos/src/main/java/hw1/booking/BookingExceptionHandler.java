package hw1.booking;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class BookingExceptionHandler {

    // Regras de negócio → 422
    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleBusinessErrors(RuntimeException ex) {
        return ex.getMessage();
    }

    // Pedido malformado/incompleto → 400
    @ExceptionHandler({
        HttpMessageNotReadableException.class,      // body vazio/JSON inválido
        MethodArgumentNotValidException.class,      // @Valid falhou
        BindException.class,                        // binding falhou
        MissingServletRequestParameterException.class // query param em falta
    })
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public String handleBadRequest(Exception ex) {
        return "invalid request";
    }

    // Falhas ao obter municípios da GeoAPI → 422 texto simples
    @ExceptionHandler(hw1.municipios.MunicipalityProviderException.class)
    public ResponseEntity<String> handleMunicipalityErrors(RuntimeException ex) {
        return ResponseEntity.unprocessableEntity()
            .body("Erro ao obter municípios da GeoAPI");
    }
}
