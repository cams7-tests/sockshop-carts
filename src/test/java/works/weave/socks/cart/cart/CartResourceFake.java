package works.weave.socks.cart.cart;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import works.weave.socks.cart.entities.Cart;

@RequiredArgsConstructor
class CartResourceFake implements Resource<Cart> {
  private final String customerId;
  private Cart cart;

  @Override
  public Runnable destroy() {
    return () -> cart = null;
  }

  @Override
  public Supplier<Cart> create() {
    return () -> cart = new Cart(customerId);
  }

  @Override
  public Supplier<Cart> value() {
    if (cart == null) {
      create().get();
    }
    return () -> cart;
  }

  @Override
  public Runnable merge(Cart toMerge) {
    return null;
  }
}
