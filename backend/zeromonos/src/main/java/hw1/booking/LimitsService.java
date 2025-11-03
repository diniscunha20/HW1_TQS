package hw1.booking;

import org.springframework.stereotype.Service;

@Service
public class LimitsService {
  private volatile int maxPerDay = 20; // valor por omissão

  public int getMaxPerDay() { return maxPerDay; }

  public void setMaxPerDay(int maxPerDay) {
    if (maxPerDay < 1)
      throw new IllegalArgumentException("maxPerDay > 0");
    this.maxPerDay = maxPerDay;
  }
}
