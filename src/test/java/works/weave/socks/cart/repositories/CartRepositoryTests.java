package works.weave.socks.cart.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import works.weave.socks.cart.entities.Cart;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
public class CartRepositoryTests {
  @Autowired private CartRepository cartRepository;

  @BeforeEach
  void removeAllData() {
    cartRepository.deleteAll();
  }

  @Test
  void testCartSave() {
    var original = new Cart("customerId");
    var saved = cartRepository.save(original);

    assertThat(cartRepository.count()).isEqualTo(1);
    assertThat(original).isEqualTo(saved);
  }

  @Test
  void testCartGetDefault() {
    var original = new Cart("customerId");
    var saved = cartRepository.save(original);

    assertThat(cartRepository.count()).isEqualTo(1);
    assertThat(original).isEqualTo(saved);
  }

  @Test
  void testSearchCustomerById() {
    var original = new Cart("customerId");
    cartRepository.save(original);

    var found = cartRepository.findByCustomerId(original.getCustomerId());
    assertThat(found.size()).isEqualTo(1);
    assertThat(original).isEqualTo(found.get(0));
  }
}
