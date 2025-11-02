package hw1.booking;

import java.time.LocalDate;

public class Booking {

    private final String token;
    private final String name;
    private final String municipalityCode;
    private final LocalDate date;
    private final String timeSlot;
    private String description;
    private BookingStatus status;

    public Booking(String token, String name, String municipalityCode,
                   LocalDate date, String timeSlot, BookingStatus status) {
        this.token = token;
        this.name = name;
        this.municipalityCode = municipalityCode;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
    }

    public String getToken() { return token; }
    public String getName() { return name; }
    public String getMunicipalityCode() { return municipalityCode; }
    public LocalDate getDate() { return date; }
    public String getTimeSlot() { return timeSlot; }
    public BookingStatus getStatus() { return status; }
    public String getDescription() { return description; }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setStatus(BookingStatus status) {
        this.status = status;
    }
}
