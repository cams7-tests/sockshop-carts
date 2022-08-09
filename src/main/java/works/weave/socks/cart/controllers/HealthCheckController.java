package works.weave.socks.cart.controllers;

import static org.springframework.http.HttpStatus.OK;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import works.weave.socks.cart.entities.HealthCheck;

@RequiredArgsConstructor
@RestController
public class HealthCheckController {

  private static final String STATUS_OK = "OK";

  private final MongoTemplate mongoTemplate;

  @ResponseStatus(OK)
  @GetMapping(path = "/health")
  public @ResponseBody Map<String, List<HealthCheck>> getHealth() {
    Map<String, List<HealthCheck>> map = new HashMap<String, List<HealthCheck>>();
    List<HealthCheck> healthChecks = new ArrayList<HealthCheck>();

    HealthCheck app = new HealthCheck("carts", STATUS_OK);
    HealthCheck database = new HealthCheck("carts-db", STATUS_OK);

    try {
      mongoTemplate.executeCommand("{ buildInfo: 1 }");
    } catch (RuntimeException e) {
      database.setStatus("err");
    }

    healthChecks.add(app);
    healthChecks.add(database);

    map.put("health", healthChecks);
    return map;
  }
}
