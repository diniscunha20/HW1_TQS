package hw1.booking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingConfig {
    @Bean
    public BookingRules bookingRules() {
        // capacidade por dia/slot/município e slots válidos
        return new BookingRules(50, new String[]{"AM","PM"});
    }
}