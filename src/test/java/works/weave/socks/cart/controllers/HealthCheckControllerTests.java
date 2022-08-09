package works.weave.socks.cart.controllers;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.mock;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class HealthCheckControllerTests {

  @Autowired private HealthCheckController healthCheckController;

  @Test
  void shouldGetHealth() {
    var results = this.healthCheckController.getHealth();
    assertThat(results.get("health").size()).isEqualTo(2);
  }

  @Configuration
  static class HealthCheckControllerTestConfiguration {
    @Bean
    public MongoTemplate mongoTemplate() {
      return mock(MongoTemplate.class);
    }

    @Bean
    public HealthCheckController healthCheckController(MongoTemplate mongoTemplate) {
      return new HealthCheckController(mongoTemplate);
    }
  }
}
