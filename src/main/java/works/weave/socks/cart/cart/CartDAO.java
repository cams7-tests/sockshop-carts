package works.weave.socks.cart.cart;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import works.weave.socks.cart.entities.Cart;

public interface CartDAO {
  void delete(Cart cart);

  Cart save(Cart cart);

  List<Cart> findByCustomerId(String customerId);

  class Fake implements CartDAO {
    private Map<String, Cart> cartStore = new HashMap<>();

    @Override
    public void delete(Cart cart) {
      cartStore.remove(cart.getCustomerId());
    }

    @Override
    public Cart save(Cart cart) {
      return cartStore.put(cart.getCustomerId(), cart);
    }

    @Override
    public List<Cart> findByCustomerId(String customerId) {
      Cart cart = cartStore.get(customerId);
      if (cart != null) {
        return Collections.singletonList(cart);
      } else {
        return Collections.emptyList();
      }
    }
  }
}
