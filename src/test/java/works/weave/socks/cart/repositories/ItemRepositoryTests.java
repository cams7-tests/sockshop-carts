package works.weave.socks.cart.repositories;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import works.weave.socks.cart.entities.Item;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
public class ItemRepositoryTests {
  @Autowired private ItemRepository itemRepository;

  @BeforeEach
  void removeAllData() {
    itemRepository.deleteAll();
  }

  @Test
  void testCustomerSave() {
    var original = new Item("id", "itemId", 1, 0.99F);
    var saved = itemRepository.save(original);

    assertThat(itemRepository.count()).isEqualTo(1);
    assertThat(original).isEqualTo(saved);
  }
}
