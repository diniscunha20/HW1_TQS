package hw1.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    // Criar uma marcação
    @PostMapping
    public ResponseEntity<CreateBookingResponse> create(@Valid @RequestBody CreateBookingRequest req) {
        return ResponseEntity.status(201).body(service.create(req));
    }

    // Consultar por token
    @GetMapping("/{token}")
    public ResponseEntity<Booking> getByToken(@PathVariable String token) {
        return ResponseEntity.ok(service.getByToken(token));
    }

    // Listar todas ou filtrar por município
    @GetMapping
    public ResponseEntity<List<Booking>> listByMunicipality(@RequestParam(required = false) String municipality) {
        if (municipality == null || municipality.isBlank()) {
            return ResponseEntity.ok(service.getAll());
        }
        return ResponseEntity.ok(service.getByMunicipality(municipality));
    }

    // Atualizar estado da marcação
    @PatchMapping("/{token}/status")
    public ResponseEntity<Booking> updateStatus(
            @PathVariable String token,
            @RequestBody Map<String, String> payload) {
        String newStatus = payload.get("status");
        return ResponseEntity.ok(service.updateStatus(token, newStatus));
    }
}
