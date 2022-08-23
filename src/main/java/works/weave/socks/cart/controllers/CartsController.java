package works.weave.socks.cart.controllers;

import static org.springframework.http.HttpStatus.ACCEPTED;
import static org.springframework.http.HttpStatus.OK;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
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

@Tag(name = "Cart Service")
@RequiredArgsConstructor
@Log4j2
@RestController
@RequestMapping(path = "/carts", produces = APPLICATION_JSON_VALUE)
public class CartsController {
  private final CartDAO cartDAO;

  @Operation(description = "Create an new cart when it doesn't exist")
  @ApiResponses({@ApiResponse(responseCode = "200", description = "Ok")})
  @ResponseStatus(OK)
  @GetMapping(value = "/{customerId}")
  Cart get(
      @Parameter(name = "customerId", required = true, description = "Customer id") @PathVariable
          String customerId) {
    return getCartResource(customerId).value().get();
  }

  @Operation(description = "Delete cart")
  @ApiResponses({@ApiResponse(responseCode = "202", description = "Accepted")})
  @ResponseStatus(ACCEPTED)
  @DeleteMapping(value = "/{customerId}")
  void delete(
      @Parameter(name = "customerId", required = true, description = "Customer id") @PathVariable
          String customerId) {
    getCartResource(customerId).destroy().run();
  }

  @Operation(description = "Merge session cart items with customer cart items")
  @ApiResponses({@ApiResponse(responseCode = "202", description = "Accepted")})
  @ResponseStatus(ACCEPTED)
  @GetMapping(value = "/{customerId}/merge")
  void mergeCarts(
      @Parameter(name = "customerId", required = true, description = "Customer id") @PathVariable
          String customerId,
      @Parameter(name = "sessionId", required = true, description = "Session id")
          @RequestParam(value = "sessionId")
          String sessionId) {
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
