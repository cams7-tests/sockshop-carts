package works.weave.socks.cart.controllers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.collection.IsCollectionWithSize.hasSize;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import works.weave.socks.cart.cart.CartDAO;
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.item.ItemDAO;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration
public class UnitItemsController {

  @Autowired private ItemsController itemsController;

  @Autowired private ItemDAO itemDAO;

  @Test
  public void whenNewItemAdd() {
    Item item = new Item("id", "itemId", 1, 0F);
    String customerId = "customerIdAdd";
    itemsController.addToCart(customerId, item);
    assertThat(itemsController.getItems(customerId), is(hasSize(1)));
    assertThat(itemsController.getItems(customerId), is(org.hamcrest.CoreMatchers.hasItem(item)));
  }

  @Test
  public void whenExistIncrementQuantity() {
    Item item = new Item("id", "itemId", 1, 0F);
    String customerId = "customerIdIncrement";
    itemsController.addToCart(customerId, item);
    itemsController.addToCart(customerId, item);
    assertThat(itemsController.getItems(customerId), is(hasSize(1)));
    assertThat(itemsController.getItems(customerId), is(org.hamcrest.CoreMatchers.hasItem(item)));
    assertThat(itemDAO.findOne(item.getId()).getQuantity(), is(equalTo(2)));
  }

  @Test
  public void shouldRemoveItemFromCart() {
    Item item = new Item("id", "itemId", 1, 0F);
    String customerId = "customerIdRemove";
    itemsController.addToCart(customerId, item);
    assertThat(itemsController.getItems(customerId), is(hasSize(1)));
    itemsController.removeItem(customerId, item.getItemId());
    assertThat(itemsController.getItems(customerId), is(hasSize(0)));
  }

  @Test
  public void shouldSetQuantity() {
    Item item = new Item("id", "itemId", 1, 0F);
    String customerId = "customerIdQuantity";
    itemsController.addToCart(customerId, item);
    assertThat(
        itemsController.getItems(customerId).get(0).getQuantity(), is(equalTo(item.getQuantity())));
    Item anotherItem = new Item(item, 15);
    itemsController.updateItem(customerId, anotherItem);
    assertThat(itemDAO.findOne(item.getId()).getQuantity(), is(equalTo(anotherItem.getQuantity())));
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
