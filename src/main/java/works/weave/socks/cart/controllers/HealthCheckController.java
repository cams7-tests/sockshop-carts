package works.weave.socks.cart.controllers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import works.weave.socks.cart.entities.HealthCheck;

@RestController
public class HealthCheckController {

  @Autowired private MongoTemplate mongoTemplate;

  @ResponseStatus(HttpStatus.OK)
  @RequestMapping(method = RequestMethod.GET, path = "/health")
  public @ResponseBody Map<String, List<HealthCheck>> getHealth() {
    Map<String, List<HealthCheck>> map = new HashMap<String, List<HealthCheck>>();
    List<HealthCheck> healthChecks = new ArrayList<HealthCheck>();

    HealthCheck app = new HealthCheck("carts", "OK");
    HealthCheck database = new HealthCheck("carts-db", "OK");

    try {
      mongoTemplate.executeCommand("{ buildInfo: 1 }");
    } catch (Exception e) {
      database.setStatus("err");
    }

    healthChecks.add(app);
    healthChecks.add(database);

    map.put("health", healthChecks);
    return map;
  }
}
