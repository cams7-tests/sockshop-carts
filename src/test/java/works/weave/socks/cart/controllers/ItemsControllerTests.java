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
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.item.ItemDAO;

@ExtendWith(SpringExtension.class)
@ContextConfiguration
public class ItemsControllerTests {

  @Autowired private ItemDAO itemDAO;
  @Autowired private ItemsController itemsController;

  @Test
  void whenNewItemAdd() {
    var item = new Item("id", "itemId", 1, 0F);
    var customerId = "customerIdAdd";
    itemsController.addToCart(customerId, item);
    assertThat(itemsController.getItems(customerId)).hasSize(1);
    assertThat(itemsController.getItems(customerId)).contains(item);
  }

  @Test
  void whenExistIncrementQuantity() {
    var item = new Item("id", "itemId", 1, 0F);
    var customerId = "customerIdIncrement";
    itemsController.addToCart(customerId, item);
    itemsController.addToCart(customerId, item);
    assertThat(itemsController.getItems(customerId)).hasSize(1);
    assertThat(itemsController.getItems(customerId)).contains(item);
    assertThat(itemDAO.findOne(item.getId()).getQuantity()).isEqualTo(2);
  }

  @Test
  void shouldRemoveItemFromCart() {
    var item = new Item("id", "itemId", 1, 0F);
    var customerId = "customerIdRemove";
    itemsController.addToCart(customerId, item);
    assertThat(itemsController.getItems(customerId)).hasSize(1);
    itemsController.removeItem(customerId, item.getItemId());
    assertThat(itemsController.getItems(customerId)).hasSize(0);
  }

  @Test
  void shouldSetQuantity() {
    var item = new Item("id", "itemId", 1, 0F);
    var customerId = "customerIdQuantity";
    itemsController.addToCart(customerId, item);
    assertThat(itemsController.getItems(customerId).get(0).getQuantity())
        .isEqualTo(item.getQuantity());
    var anotherItem = new Item(item, 15);
    itemsController.updateItem(customerId, anotherItem);
    assertThat(itemDAO.findOne(item.getId()).getQuantity()).isEqualTo(anotherItem.getQuantity());
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
