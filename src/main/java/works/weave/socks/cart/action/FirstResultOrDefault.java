package works.weave.socks.cart.action;

import java.util.Collection;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class FirstResultOrDefault<T> implements Supplier<T> {
  private final Collection<T> collection;
  private final Supplier<T> nonePresent;

  @Override
  public T get() {
    return collection.stream().findFirst().orElseGet(nonePresent);
  }
}
