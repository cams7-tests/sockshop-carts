package works.weave.socks.cart.item;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import works.weave.socks.cart.entities.Item;

@ExtendWith(MockitoExtension.class)
public class FoundItemTests {
  @Test
  void findOneItem() {
    var list = new ArrayList<Item>();
    var testId = "testId";
    var testAnswer = new Item(testId);
    list.add(testAnswer);
    var foundItem = new FoundItem(() -> list, () -> testAnswer);
    assertThat(foundItem.get()).isEqualTo(testAnswer);
  }
}
