package hw1.booking;

import hw1.municipios.Municipality;
import hw1.municipios.MunicipalityClient;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

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

    // Criar uma nova reserva
    public CreateBookingResponse create(CreateBookingRequest req) {
        // 1) validar município
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

        // 3) criar booking com token e estado inicial
        String token = tokens.generate();
        Booking booking = new Booking(
                token,
                req.getName(),
                req.getMunicipalityCode(),
                req.getDate(),
                req.getTimeSlot(),
                BookingStatus.RECEIVED
        );

        // 4) guardar
        repo.save(booking);

        // 5) devolver resposta
        return new CreateBookingResponse(token);
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
}
