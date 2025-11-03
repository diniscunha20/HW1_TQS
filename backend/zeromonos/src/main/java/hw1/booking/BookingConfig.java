package hw1.booking;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class BookingConfig {
    @Bean
    public BookingRules bookingRules(LimitsService limitsService) {
        // capacidade por dia/slot/município e slots válidos
        return new BookingRules(limitsService, new String[]{"AM","PM"});
    }
}