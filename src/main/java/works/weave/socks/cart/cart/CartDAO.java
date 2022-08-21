package works.weave.socks.cart.cart;

import java.util.List;
import works.weave.socks.cart.entities.Cart;

public interface CartDAO {
  void delete(Cart cart);

  Cart save(Cart cart);

  List<Cart> findByCustomerId(String customerId);
}
