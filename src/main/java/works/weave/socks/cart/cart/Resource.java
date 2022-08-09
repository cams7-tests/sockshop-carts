package works.weave.socks.cart.cart;

import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import works.weave.socks.cart.entities.Cart;

public interface Resource<T> {
  Runnable destroy();

  Supplier<T> create();

  Supplier<T> value();

  Runnable merge(T toMerge);

  @RequiredArgsConstructor
  class CartFake implements Resource<Cart> {
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
}
