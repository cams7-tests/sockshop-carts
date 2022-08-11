package works.weave.socks.cart.controllers;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.CREATED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import java.util.List;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import works.weave.socks.cart.cart.CartDAO;
import works.weave.socks.cart.cart.CartResource;
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.item.FoundItem;
import works.weave.socks.cart.item.ItemDAO;
import works.weave.socks.cart.item.ItemResource;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping(value = "/carts/{customerId:.*}/items", produces = APPLICATION_JSON_VALUE)
public class ItemsController {

  private final ItemDAO itemDAO;
  private final CartDAO cartDAO;
  private final CartsController cartsController;

  @ResponseStatus(OK)
  @GetMapping(value = "/{itemId:.*}")
  Item get(@PathVariable String customerId, @PathVariable String itemId) {
    return new FoundItem(() -> getItems(customerId), () -> new Item(itemId)).get();
  }

  @ResponseStatus(OK)
  @GetMapping
  List<Item> getItems(@PathVariable String customerId) {
    return cartsController.get(customerId).contents();
  }

  @ResponseStatus(CREATED)
  @PostMapping(consumes = APPLICATION_JSON_VALUE)
  Item addToCart(@PathVariable String customerId, @RequestBody Item item) {
    // If the item does not exist in the cart, create new one in the repository.
    var foundItem = new FoundItem(() -> cartsController.get(customerId).contents(), () -> item);
    if (!foundItem.hasItem()) {
      var newItem = new ItemResource(itemDAO, () -> item).create();
      log.debug("Did not find item. Creating item for user: {}, {}", customerId, newItem.get());
      getCartResource(customerId).contents().get().add(newItem).run();
      return item;
    } else {
      var newItem = new Item(foundItem.get(), foundItem.get().getQuantity() + 1);
      log.debug("Found item in cart. Incrementing for user: {}, {}", customerId, newItem);
      updateItem(customerId, newItem);
      return newItem;
    }
  }

  @ResponseStatus(ACCEPTED)
  @DeleteMapping(value = "/{itemId:.*}")
  void removeItem(@PathVariable String customerId, @PathVariable String itemId) {
    var foundItem = new FoundItem(() -> getItems(customerId), () -> new Item(itemId));
    var item = foundItem.get();

    log.debug("Removing item from cart: {}", item);
    getCartResource(customerId).contents().get().delete(() -> item).run();

    log.debug("Removing item from repository: {}", item);
    new ItemResource(itemDAO, () -> item).destroy().run();
  }

  @ResponseStatus(ACCEPTED)
  @PatchMapping(consumes = APPLICATION_JSON_VALUE)
  void updateItem(@PathVariable String customerId, @RequestBody Item item) {
    // Merge old and new items
    var itemResource = new ItemResource(itemDAO, () -> get(customerId, item.getItemId()));
    log.debug("Merging item in cart for user: {}, {}", customerId, item);
    itemResource.merge(item).run();
  }

  private CartResource getCartResource(String customerId) {
    return new CartResource(cartDAO, customerId);
  }
}
