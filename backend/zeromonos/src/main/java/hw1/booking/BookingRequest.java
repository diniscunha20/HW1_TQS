package hw1.booking;

import java.time.LocalDate;

public class BookingRequest {
    private String name;
    private String municipalityCode;
    private LocalDate date;
    private String timeSlot;
    private String description;

    public BookingRequest(String name, String municipalityCode, LocalDate date, String timeSlot, String description) {
        this.name = name;
        this.municipalityCode = municipalityCode;
        this.date = date;
        this.timeSlot = timeSlot;
        this.description = description;
    }
    
    public String getName() {
        return name;
    }
    public String getMunicipalityCode() {
        return municipalityCode;
    }
    
    public LocalDate getDate() {
        return date;
    }
    
    public String getTimeSlot() {
        return timeSlot;
    }
    
    public String getDescription() {
        return description;
    }
}
