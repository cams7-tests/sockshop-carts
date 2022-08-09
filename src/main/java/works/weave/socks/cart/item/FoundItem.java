package works.weave.socks.cart.item;

import java.util.List;
import java.util.function.Supplier;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import works.weave.socks.cart.entities.Item;

@RequiredArgsConstructor
@Log4j2
public class FoundItem implements Supplier<Item> {
  private final Supplier<List<Item>> items;
  private final Supplier<Item> item;

  @Override
  public Item get() {
    return items.get().stream()
        .filter(item.get()::equals)
        .findFirst()
        .orElseThrow(() -> new IllegalArgumentException("Cannot find item in cart"));
  }

  public boolean hasItem() {
    boolean present = items.get().stream().filter(item.get()::equals).findFirst().isPresent();
    if (present) log.debug("Found");
    else log.debug("Didn't find item: {}, in: {}", item.get(), items.get());
    return present;
  }
}
