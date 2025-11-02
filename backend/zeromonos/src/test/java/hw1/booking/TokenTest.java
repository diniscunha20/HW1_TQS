package hw1.booking;

import org.junit.jupiter.api.Test;
import java.util.HashSet;
import java.util.Set;
import static org.assertj.core.api.Assertions.*;

class TokenTest {

  @Test
  void geraTokenCom8CharsAlfanumericos() {
    var svc = new SimpleTokenService();
    var t = svc.generate();
    assertThat(t).hasSize(8).matches("^[A-Z0-9]+$");
  }

    @Test
    void geraTokenUnico() {
        var svc = new SimpleTokenService();
        Set<String> tokens = new HashSet<>();
        for (int i = 0; i < 1000; i++) {
        tokens.add(svc.generate());
        }
        assertThat(tokens).hasSize(1000);
    }
}