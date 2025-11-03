package hw1.booking;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class BookingService {

    private final BookingRepository repo;
    private final BookingRules rules;
    private final MunicipalityClient muni;
    private final TokenService tokens;
    private final LimitsService limitsService;

    public BookingService(BookingRepository repo, BookingRules rules, MunicipalityClient muni, TokenService tokens, LimitsService limitsService) {
        this.repo = repo;
        this.rules = rules;
        this.muni = muni;
        this.tokens = tokens;
        this.limitsService = limitsService;
    }

    public CreateBookingResponse create(CreateBookingRequest req) {
        // 1) regras locais (não precisam da GeoAPI)
        rules.validate(new BookingRequest(
            req.getName(),
            req.getMunicipalityCode(),
            req.getDate(),
            req.getTimeSlot(),
            req.getDescription()
        ));

        int max = limitsService.getMaxPerDay();
        long ativosNoDia = repo.countActiveByDate(req.getDate());
        if (ativosNoDia >= max) {
            throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Capacidade diária global esgotada para " + req.getDate()
            );
        }

        // 3) Só agora valida município (pode chamar GeoAPI)
        try {
            Set<String> validCodes = muni.listAll().stream()
                .map(Municipality::code)
                .collect(Collectors.toSet());
            if (!validCodes.contains(req.getMunicipalityCode())) {
                throw new IllegalArgumentException("município inválido: " + req.getMunicipalityCode());
            }
        } catch (Exception e) {
            // Mapeia falha da GeoAPI para 422 (ou 503 se preferires)
            throw new ResponseStatusException(
                HttpStatus.UNPROCESSABLE_ENTITY,
                "Erro ao obter municípios da GeoAPI"
            );
        }

        // 4) criar + guardar
        String token = tokens.generate();
        Booking b = new Booking(
            token, req.getName(), req.getMunicipalityCode(),
            req.getDate(), req.getTimeSlot(), BookingStatus.RECEIVED
        );
        b.setDescription(req.getDescription());
        repo.save(b);
        rules.registerExisting(b);

        return new CreateBookingResponse(token, BookingStatus.RECEIVED.name());
    }

    // Obter por token
    public Booking getByToken(String token) {
        return repo.findByToken(token)
                .orElseThrow(() -> new BookingNotFoundException("Token não encontrado: " + token));
    }

    // Listar tudo
    public List<Booking> getAll() {
        return repo.findAll();
    }

    // Listar por município
    public List<Booking> getByMunicipality(String municipalityCode) {
        return repo.findByMunicipality(municipalityCode);
    }

    public Booking updateStatus(String token, String statusText) {
        var b = repo.findByToken(token).orElseThrow(() ->
            new BookingNotFoundException("Token não encontrado: " + token));

        BookingStatus newStatus;
        try {
        newStatus = BookingStatus.valueOf(statusText.toUpperCase(Locale.ROOT));
        } catch (IllegalArgumentException e) {
        throw new IllegalArgumentException("Estado inválido: " + statusText);
        }

        // (Opcional) regra: não mudar após CANCELLED/COMPLETED
        if (b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.COMPLETED) {
        throw new IllegalStateException("Marcação finalizada/cancelada; não pode transitar.");
        }

        b.updateStatus(newStatus);
        repo.save(b);
        return b;
    }

    public long countActiveByDate(java.time.LocalDate date) { return repo.countActiveByDate(date); }

}
