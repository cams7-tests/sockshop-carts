package works.weave.socks.cart.item;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;
import org.junit.Test;
import works.weave.socks.cart.entities.Item;

public class UnitFoundItem {
  @Test
  public void findOneItem() {
    List<Item> list = new ArrayList<>();
    String testId = "testId";
    Item testAnswer = new Item(testId);
    list.add(testAnswer);
    FoundItem foundItem = new FoundItem(() -> list, () -> testAnswer);
    assertThat(foundItem.get(), is(equalTo(testAnswer)));
  }
}
