package hw1.booking;

import java.time.LocalDate;

public class Booking {
    private String token;
    private String name;
    private String municipalityCode;
    private LocalDate date;
    private String timeSlot;
    private BookingStatus status;
    
    public Booking(String token, String name, String municipalityCode, LocalDate date, String timeSlot, BookingStatus status) {
        this.name = name;
        this.token = token;
        this.municipalityCode = municipalityCode;
        this.date = date;
        this.timeSlot = timeSlot;
        this.status = status;
    }
    
    public String getToken() {
        return token;
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
    
    public BookingStatus getStatus() {
        return status;
    }
}
