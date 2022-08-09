package works.weave.socks.cart.cart;

import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.entities.Item;

@RequiredArgsConstructor
@Log4j2
public class CartContentsResource implements Contents<Item> {
  private final CartDAO cartRepository;
  private final Supplier<Resource<Cart>> parent;

  @Override
  public Supplier<List<Item>> contents() {
    return () -> parentCart().contents();
  }

  @Override
  public Runnable add(Supplier<Item> item) {
    return () -> {
      log.debug("Adding for user: {}, {}", parent.get().value().get(), item.get());
      cartRepository.save(parentCart().add(item.get()));
    };
  }

  @Override
  public Runnable delete(Supplier<Item> item) {
    return () -> {
      log.debug("Deleting for user: {}, {}", parent.get().value().get(), item.get());
      cartRepository.save(parentCart().remove(item.get()));
    };
  }

  private Cart parentCart() {
    return parent.get().value().get();
  }
}
