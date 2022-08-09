package works.weave.socks.cart;

import static java.time.ZoneOffset.UTC;
import static java.util.TimeZone.getTimeZone;
import static java.util.TimeZone.setDefault;
import static org.springframework.boot.SpringApplication.run;

import javax.annotation.PostConstruct;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class CartApplication {

  public static void main(String... args) {
    run(CartApplication.class, args);
  }

  @PostConstruct
  public void init() {
    setDefault(getTimeZone(UTC));
  }
}
