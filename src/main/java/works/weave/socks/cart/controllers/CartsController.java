package works.weave.socks.cart.controllers;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import works.weave.socks.cart.cart.CartDAO;
import works.weave.socks.cart.cart.CartResource;
import works.weave.socks.cart.entities.Cart;

@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping(path = "/carts", produces = APPLICATION_JSON_VALUE)
public class CartsController {
  private final CartDAO cartDAO;

  @ResponseStatus(OK)
  @GetMapping(value = "/{customerId}")
  Cart get(@PathVariable String customerId) {
    return getCartResource(customerId).value().get();
  }

  @ResponseStatus(ACCEPTED)
  @DeleteMapping(value = "/{customerId}")
  void delete(@PathVariable String customerId) {
    getCartResource(customerId).destroy().run();
  }

  @ResponseStatus(ACCEPTED)
  @GetMapping(value = "/{customerId}/merge")
  void mergeCarts(
      @PathVariable String customerId, @RequestParam(value = "sessionId") String sessionId) {
    log.debug("Merge carts request received for ids: {} and {}", customerId, sessionId);
    var sessionCart = getCartResource(sessionId);
    var customerCart = getCartResource(customerId);
    customerCart.merge(sessionCart.value().get()).run();
    delete(sessionId);
  }

  private CartResource getCartResource(String customerId) {
    return new CartResource(cartDAO, customerId);
  }
}
