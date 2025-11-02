package hw1.booking;

import com.fasterxml.jackson.annotation.JsonFormat;
import java.time.Instant;

public class StatusChange {
  private BookingStatus status;

  @JsonFormat(shape = JsonFormat.Shape.STRING)
  private Instant at;

  public StatusChange() { } // para Jackson

  public StatusChange(BookingStatus status, Instant at) {
    this.status = status;
    this.at = at;
  }

  public BookingStatus getStatus() { return status; }
  public Instant getAt() { return at; }
}
