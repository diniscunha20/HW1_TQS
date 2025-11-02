package hw1.booking;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import org.springframework.validation.BindException;
import org.springframework.http.ResponseEntity;

@RestControllerAdvice
public class BookingExceptionHandler {

    @ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleBusinessErrors(RuntimeException ex) {
        return ex.getMessage();
    }

    @ExceptionHandler({ MethodArgumentNotValidException.class, BindException.class })
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public String handleValidationErrors(Exception ex) {
        return "invalid request";
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleMunicipalityErrors(RuntimeException ex) {
        return ResponseEntity.unprocessableEntity()
            .body("Erro ao obter municípios da GeoAPI");
    }
}
