package hw1.booking;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;

class TokenServiceTest {

  private final SimpleTokenService service = new SimpleTokenService();

  @Test
  void generate_returnsNonNull_andHas8ValidChars() {
    String t = service.generate();
    assertThat(t).isNotNull().hasSize(8).matches("^[A-Z0-9]+$");
  }

  @Test
  void generate_containsOnlyAllowedAlphabet() {
    String valid = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    String t = service.generate();
    for (char c : t.toCharArray()) assertThat(valid).contains(String.valueOf(c));
  }

  @Test
  void generate_isUniqueInReasonableSamples() {
    Set<String> tokens = new HashSet<>();
    for (int i = 0; i < 1000; i++) tokens.add(service.generate());
    assertThat(tokens).hasSize(1000);
  }
}
