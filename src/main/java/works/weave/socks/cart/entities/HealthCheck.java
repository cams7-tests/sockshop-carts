package works.weave.socks.cart.entities;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.time.LocalDateTime;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class HealthCheck {
  private String service;
  private String status;

  @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
  private LocalDateTime date;

  public HealthCheck(String service, String status) {
    this.service = service;
    this.status = status;
    this.date = LocalDateTime.now();
  }
}
