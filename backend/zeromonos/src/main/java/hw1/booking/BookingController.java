package hw1.booking;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/bookings")
public class BookingController {

    private final BookingService service;

    public BookingController(BookingService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<CreateBookingResponse> create(@RequestBody CreateBookingRequest req) {
        return ResponseEntity.status(201).body(service.create(req));
    }

    @GetMapping("/{token}")
    public ResponseEntity<Booking> getByToken(@PathVariable String token) {
        return ResponseEntity.ok(service.getByToken(token));
    }
}
