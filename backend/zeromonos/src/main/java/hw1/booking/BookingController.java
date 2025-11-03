package hw1.booking;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;
    private final LimitsService limitsService;

    public BookingController(BookingService service, LimitsService limitsService) {
        this.service = service;
        this.limitsService = limitsService;
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
    public ResponseEntity<List<Booking>> listByMunicipality(@RequestParam(required = false) String municipalityCode) {
        if (municipalityCode == null || municipalityCode.isBlank()) {
            return ResponseEntity.ok(service.getAll());
        }
        return ResponseEntity.ok(service.getByMunicipality(municipalityCode));
    }

    // Atualizar estado da marcação
    @PatchMapping("/{token}/status")
    public ResponseEntity<Booking> updateStatus(
            @PathVariable String token,
            @RequestBody Map<String, String> payload) {

        String newStatus = payload != null ? payload.get("status") : null;
        if (newStatus == null || newStatus.isBlank()) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(service.updateStatus(token, newStatus));
    }


    // DTO minimalista para request/response
    static record LimitDto(Integer maxPerDay) {}

    // Lê o limite global atual
    @GetMapping("/limits")
    public ResponseEntity<LimitDto> getGlobalLimit() {
        return ResponseEntity.ok(new LimitDto(limitsService.getMaxPerDay()));
    }

    // Atualiza o limite global
    @PutMapping("/limits")
    public ResponseEntity<Void> updateGlobalLimit(@RequestBody LimitDto dto) {
        if (dto == null || dto.maxPerDay() == null) {
            return ResponseEntity.badRequest().build();
        }
        limitsService.setMaxPerDay(dto.maxPerDay()); // valida internamente (1..1000)
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countByDate(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam(defaultValue = "true") boolean activeOnly) {

        long n = service.countActiveByDate(date);
        return ResponseEntity.ok(n);
    }
}
