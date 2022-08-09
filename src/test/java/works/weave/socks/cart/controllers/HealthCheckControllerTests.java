package works.weave.socks.cart.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class HealthCheckControllerTests {

  @Autowired private HealthCheckController healthCheckController;

  @Test
  public void shouldGetHealth() {
    var results = this.healthCheckController.getHealth();
    assertThat(results.get("health").size(), is(equalTo(2)));
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
