package hw1;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
class Hello {
    @GetMapping("/api/hello")
    String hello() {
        return "Spring a funcionar!";
    }
}
