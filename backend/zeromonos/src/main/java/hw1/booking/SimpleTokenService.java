// hw1/booking/SimpleTokenService.java
package hw1.booking;

import org.springframework.stereotype.Service;
import java.security.SecureRandom;

@Service
public class SimpleTokenService implements TokenService {
    private static final String ALPH = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
    private final SecureRandom rnd = new SecureRandom();
    @Override public String generate() {
        StringBuilder sb = new StringBuilder(8);
        for (int i=0;i<8;i++) sb.append(ALPH.charAt(rnd.nextInt(ALPH.length())));
        return sb.toString();
    }
}
