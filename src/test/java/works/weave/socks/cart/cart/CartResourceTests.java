package works.weave.socks.cart.cart;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.entities.Item;

@ExtendWith(MockitoExtension.class)
public class CartResourceTests {

  private final String customerId = "testId";
  private final CartDAO.Fake fake = new CartDAO.Fake();

  @Test
  void whenCartExistsUseThat() {
    var cart = new Cart(customerId);
    fake.save(cart);
    var cartResource = new CartResource(fake, customerId);
    assertThat(cartResource.value().get()).isEqualTo(cart);
  }

  @Test
  void whenCartDoesntExistCreateNew() {
    var cartResource = new CartResource(fake, customerId);
    assertThat(cartResource.value().get()).isNotNull();
    assertThat(cartResource.value().get().getCustomerId()).isEqualTo(customerId);
  }

  @Test
  void whenDestroyRemoveItem() {
    var cart = new Cart(customerId);
    fake.save(cart);
    var cartResource = new CartResource(fake, customerId);
    cartResource.destroy().run();
    assertThat(fake.findByCustomerId(customerId)).isEmpty();
  }

  @Test
  void whenDestroyOnEmptyStillEmpty() {
    var cartResource = new CartResource(fake, customerId);
    cartResource.destroy().run();
    assertThat(fake.findByCustomerId(customerId)).isEmpty();
  }

  @Test
  void whenCreateDoCreate() {
    var cartResource = new CartResource(fake, customerId);
    cartResource.create().get();
    assertThat(fake.findByCustomerId(customerId)).isNotEmpty();
  }

  @Test
  void contentsShouldBeEmptyWhenNew() {
    var cartResource = new CartResource(fake, customerId);
    cartResource.create().get();
    assertThat(cartResource.contents().get().contents().get()).isEmpty();
  }

  @Test
  void mergedItemsShouldBeInCart() {
    var person1 = "person1";
    var person2 = "person2";
    var person1Item = new Item("item1");
    var person2Item = new Item("item2");
    var cartResource = new CartResource(fake, person1);
    cartResource.contents().get().add(() -> person1Item).run();
    var cartResourceToMerge = new CartResource(fake, person2);
    cartResourceToMerge.contents().get().add(() -> person2Item).run();
    cartResource.merge(cartResourceToMerge.value().get()).run();
    assertThat(cartResource.contents().get().contents().get()).hasSize(2);
    assertThat(cartResource.contents().get().contents().get().get(0))
        .satisfiesAnyOf(
            item -> assertThat(item).isEqualTo(person1Item),
            item -> assertThat(item).isEqualTo(person2Item));
    assertThat(cartResource.contents().get().contents().get().get(1))
        .satisfiesAnyOf(
            item -> assertThat(item).isEqualTo(person1Item),
            item -> assertThat(item).isEqualTo(person2Item));
    assertThat(cartResourceToMerge.contents().get().contents().get()).hasSize(1);
  }
}
