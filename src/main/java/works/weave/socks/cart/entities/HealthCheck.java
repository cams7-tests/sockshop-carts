package works.weave.socks.cart.entities;

import java.time.LocalDateTime;
import lombok.Data;

@Data
public class HealthCheck {
  private String service;
  private String status;
  private LocalDateTime date;

  public HealthCheck(String service, String status) {
    this.service = service;
    this.status = status;
    this.date = LocalDateTime.now();
  }
}
