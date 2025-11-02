package hw1.booking;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import hw1.booking.StatusChange;

public class Booking {

    private final String token;
    private final String name;
    private final String municipalityCode;
    private final LocalDate date;
    private final String timeSlot;
    private String description;
    private BookingStatus status;
    private final List<StatusChange> timeline = new ArrayList<>();


    public Booking(String token, String name, String municipalityCode,
                   LocalDate date, String timeSlot, BookingStatus status) {
        this.token = token;
        this.name = name;
        this.municipalityCode = municipalityCode;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;

        this.timeline.add(new StatusChange(status, java.time.Instant.now()));
    }

    public List<StatusChange> getTimeline() {
        return Collections.unmodifiableList(timeline);
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

    public void updateStatus(BookingStatus newStatus) {
        if (this.status == newStatus) return; // evita duplicado opcional
        this.status = newStatus;
        this.timeline.add(new StatusChange(newStatus, java.time.Instant.now()));
    }
}
