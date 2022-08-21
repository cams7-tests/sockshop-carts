package works.weave.socks.cart.cart;

import java.util.function.Supplier;

public interface Resource<T> {
  Runnable destroy();

  Supplier<T> create();

  Supplier<T> value();

  Runnable merge(T toMerge);
}
