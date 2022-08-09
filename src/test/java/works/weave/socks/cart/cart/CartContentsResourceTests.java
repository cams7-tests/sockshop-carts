package works.weave.socks.cart.cart;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.entities.Item;

@ExtendWith(MockitoExtension.class)
public class CartContentsResourceTests {
  private final String customerId = "testId";
  private final CartDAO.Fake fakeDAO = new CartDAO.Fake();
  private final Resource<Cart> fakeCartResource = new Resource.CartFake(customerId);

  @Test
  void shouldAddAndReturnContents() {
    var contentsResource = new CartContentsResource(fakeDAO, () -> fakeCartResource);
    var item = new Item("testId");
    contentsResource.add(() -> item).run();
    assertThat(contentsResource.contents().get()).hasSize(1);
    assertThat(contentsResource.contents().get()).containsExactlyInAnyOrder(item);
  }

  @Test
  void shouldStartEmpty() {
    var contentsResource = new CartContentsResource(fakeDAO, () -> fakeCartResource);
    assertThat(contentsResource.contents().get()).hasSize(0);
  }

  @Test
  void shouldDeleteItemFromCart() {
    var contentsResource = new CartContentsResource(fakeDAO, () -> fakeCartResource);
    var item = new Item("testId");
    contentsResource.add(() -> item).run();
    assertThat(contentsResource.contents().get()).hasSize(1);
    assertThat(contentsResource.contents().get()).containsExactlyInAnyOrder(item);
    var item2 = new Item(item.getItemId());
    contentsResource.delete(() -> item2).run();
    assertThat(contentsResource.contents().get()).hasSize(0);
  }
}
