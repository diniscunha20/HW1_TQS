package hw1.booking;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;

import java.util.Set;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

@Service
public class BookingService {

    private final BookingRepository repo;
    private final BookingRules rules;
    private final MunicipalityClient muni;
    private final TokenService tokens;

    public BookingService(BookingRepository repo, BookingRules rules, MunicipalityClient muni, TokenService tokens) {
        this.repo = repo;
        this.rules = rules;
        this.muni = muni;
        this.tokens = tokens;
    }

    public CreateBookingResponse create(CreateBookingRequest req) {
        // 1) município válido
        Set<String> validCodes = muni.listAll().stream()
                .map(Municipality::code)
                .collect(Collectors.toSet());
        if (!validCodes.contains(req.getMunicipalityCode())) {
            throw new IllegalArgumentException("município inválido: " + req.getMunicipalityCode());
        }

        // 2) regras de negócio
        var domainReq = new BookingRequest(
                req.getName(),
                req.getMunicipalityCode(),
                req.getDate(),
                req.getTimeSlot(),
                req.getDescription()
        );
        rules.validate(domainReq);

        // 3) criar booking com estado inicial e token
        String token = tokens.generate();
        Booking booking = new Booking(
                token,
                req.getName(),
                req.getMunicipalityCode(),
                req.getDate(),
                req.getTimeSlot(),
                BookingStatus.RECEIVED
        );

        // 4) persistir
        repo.save(booking);

        // 5) resposta
        return new CreateBookingResponse(token);
    }

    public Booking getByToken(String token) {
        return repo.findByToken(token);
    }
}
