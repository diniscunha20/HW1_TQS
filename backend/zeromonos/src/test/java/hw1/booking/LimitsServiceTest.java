package hw1.booking;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class LimitsServiceTest {

    private LimitsService service;

    @BeforeEach
    void setUp() {
        service = new LimitsService();
    }

    @Test
    void getMaxPerDay_returnsDefaultValue() {
        assertThat(service.getMaxPerDay()).isEqualTo(20);
    }

    @Test
    void setMaxPerDay_updatesValue() {
        service.setMaxPerDay(50);
        assertThat(service.getMaxPerDay()).isEqualTo(50);
    }

    @Test
    void setMaxPerDay_withOne_isValid() {
        service.setMaxPerDay(1);
        assertThat(service.getMaxPerDay()).isEqualTo(1);
    }

    @Test
    void setMaxPerDay_withLargeNumber_isValid() {
        service.setMaxPerDay(1000);
        assertThat(service.getMaxPerDay()).isEqualTo(1000);
    }

    @Test
    void setMaxPerDay_withZero_throwsException() {
        assertThatThrownBy(() -> service.setMaxPerDay(0))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("maxPerDay > 0");
    }

    @Test
    void setMaxPerDay_withNegativeValue_throwsException() {
        assertThatThrownBy(() -> service.setMaxPerDay(-1))
            .isInstanceOf(IllegalArgumentException.class)
            .hasMessageContaining("maxPerDay > 0");
    }

    @Test
    void setMaxPerDay_withNegativeValue_doesNotChangeValue() {
        service.setMaxPerDay(30);
        
        try {
            service.setMaxPerDay(-5);
        } catch (IllegalArgumentException e) {
            // Expected
        }
        
        assertThat(service.getMaxPerDay()).isEqualTo(30);
    }

    @Test
    void setMaxPerDay_multipleUpdates_keepsLatestValue() {
        service.setMaxPerDay(10);
        service.setMaxPerDay(20);
        service.setMaxPerDay(15);
        
        assertThat(service.getMaxPerDay()).isEqualTo(15);
    }
}
