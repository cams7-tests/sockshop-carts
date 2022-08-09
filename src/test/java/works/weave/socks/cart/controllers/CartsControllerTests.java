package works.weave.socks.cart.controllers;

import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import works.weave.socks.cart.cart.CartDAO;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.item.ItemDAO;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class CartsControllerTests {

  @Autowired private CartDAO cartDAO;
  @Autowired private CartsController cartsController;

  @Test
  void shouldGetCart() {
    var customerId = "customerIdGet";
    var cart = new Cart(customerId);
    cartDAO.save(cart);
    var gotCart = cartsController.get(customerId);
    assertThat(gotCart).isEqualTo(cart);
    assertThat(cartDAO.findByCustomerId(customerId).get(0)).isEqualTo(cart);
  }

  @Test
  void shouldDeleteCart() {
    var customerId = "customerIdGet";
    var cart = new Cart(customerId);
    cartDAO.save(cart);
    cartsController.delete(customerId);
    assertThat(cartDAO.findByCustomerId(customerId)).isEmpty();
  }

  @Test
  void shouldMergeItemsInCartsTogether() {
    var customerId1 = "customerId1";
    var cart1 = new Cart(customerId1);
    var itemId1 = new Item("itemId1");
    cart1.add(itemId1);
    cartDAO.save(cart1);
    var customerId2 = "customerId2";
    var cart2 = new Cart(customerId2);
    var itemId2 = new Item("itemId2");
    cart2.add(itemId2);
    cartDAO.save(cart2);

    cartsController.mergeCarts(customerId1, customerId2);
    assertThat(cartDAO.findByCustomerId(customerId1).get(0).contents()).hasSize(2);
    assertThat(cartDAO.findByCustomerId(customerId1).get(0).contents())
        .containsExactlyInAnyOrder(itemId1, itemId2);
    assertThat(cartDAO.findByCustomerId(customerId2)).isEmpty();
  }

  @Configuration
  static class ItemsControllerTestConfiguration {
    @Bean
    public ItemDAO itemDAO() {
      return new ItemDAO.Fake();
    }

    @Bean
    public CartDAO cartDAO() {
      return new CartDAO.Fake();
    }

    @Bean
    public CartsController cartsController(CartDAO cartDAO) {
      return new CartsController(cartDAO);
    }

    @Bean
    public ItemsController itemsController(
        ItemDAO itemDAO, CartDAO cartDAO, CartsController cartsController) {
      return new ItemsController(itemDAO, cartDAO, cartsController);
    }
  }
}
