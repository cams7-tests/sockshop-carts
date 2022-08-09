package works.weave.socks.cart.item;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import works.weave.socks.cart.entities.Item;

@ExtendWith(MockitoExtension.class)
public class ItemResourceTests {
  private ItemDAO itemDAO = new ItemDAO.Fake();

  @Test
  void testCreateAndDestroy() {
    var item = new Item("itemId", "testId", 1, 0F);
    var itemResource = new ItemResource(itemDAO, () -> item);
    itemResource.create().get();
    assertThat(itemDAO.findOne(item.getId())).isEqualTo(item);
    itemResource.destroy().run();
    assertThat(itemDAO.findOne(item.getId())).isNull();
  }

  @Test
  void mergedItemShouldHaveNewQuantity() {
    var item = new Item("itemId", "testId", 1, 0F);
    var itemResource = new ItemResource(itemDAO, () -> item);
    assertThat(itemResource.value().get()).isEqualTo(item);
    var newItem = new Item(item, 10);
    itemResource.merge(newItem).run();
    assertThat(itemDAO.findOne(item.getId()).getQuantity()).isEqualTo(newItem.getQuantity());
  }
}
