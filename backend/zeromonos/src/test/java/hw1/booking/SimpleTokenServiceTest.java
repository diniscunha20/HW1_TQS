package hw1.booking;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.*;

class SimpleTokenServiceTest {

    private final SimpleTokenService service = new SimpleTokenService();

    @Test
    void generate_returnsNonNullToken() {
        String token = service.generate();
        assertThat(token).isNotNull();
    }

    @Test
    void generate_returnsEightCharacters() {
        String token = service.generate();
        assertThat(token).hasSize(8);
    }

    @Test
    void generate_containsOnlyValidCharacters() {
        String validChars = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
        String token = service.generate();
        
        for (char c : token.toCharArray()) {
            assertThat(validChars).contains(String.valueOf(c));
        }
    }

    @Test
    void generate_multipleCalls_returnsDifferentTokens() {
        String token1 = service.generate();
        String token2 = service.generate();
        String token3 = service.generate();
        
        assertThat(token1).isNotEqualTo(token2);
        assertThat(token2).isNotEqualTo(token3);
        assertThat(token1).isNotEqualTo(token3);
    }

    @Test
    void generate_isUpperCase() {
        for (int i = 0; i < 20; i++) {
            String token = service.generate();
            assertThat(token).isEqualTo(token.toUpperCase());
        }
    }

    @Test
    void generate_hasGoodRandomness() {
        // Generate multiple tokens and ensure they're sufficiently different
        java.util.Set<String> tokens = new java.util.HashSet<>();
        int count = 100;
        
        for (int i = 0; i < count; i++) {
            tokens.add(service.generate());
        }
        
        // All tokens should be unique (probability of collision is very low)
        assertThat(tokens).hasSize(count);
    }
}
