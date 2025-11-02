package hw1.booking;

import java.time.LocalDate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public class CreateBookingRequest {
    @NotBlank
    private String name;

    @NotBlank
    private String municipalityCode;

    @NotNull
    private LocalDate date;

    @NotBlank
    private String timeSlot;

    @NotBlank
    private String description;

    public CreateBookingRequest(String name, String municipalityCode, LocalDate date, String timeSlot, String description) {
        this.name = name;
        this.municipalityCode = municipalityCode;
        this.date = date;
        this.timeSlot = timeSlot;
        this.description = description;
    }

    public String getName() { return name; }
    public String getMunicipalityCode() { return municipalityCode; }
    public LocalDate getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public String getDescription() { return description; }
}
