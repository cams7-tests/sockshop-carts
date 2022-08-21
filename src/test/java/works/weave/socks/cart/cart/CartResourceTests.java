package works.weave.socks.cart.cart;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static works.weave.socks.cart.template.CartTemplate.CUSTOMER_ID1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_CART1;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.template.DomainTemplateLoader;

@ExtendWith(MockitoExtension.class)
public class CartResourceTests {

  private CartResource cartResource;
  @Mock private CartDAO cartDAO;

  @Captor private ArgumentCaptor<Cart> cartCaptor;

  @BeforeAll
  static void loadTemplates() {
    FixtureFactoryLoader.loadTemplates(getPackageName(DomainTemplateLoader.class));
  }

  @Test
  void whenCartExistsUseThat() {
    Cart cart = from(Cart.class).gimme(VALID_CART1);

    given(cartDAO.findByCustomerId(anyString())).willReturn(List.of(cart));
    cartResource = new CartResource(cartDAO, CUSTOMER_ID1);
    assertThat(cartResource.value().get()).isEqualTo(cart);

    then(cartDAO).should(times(1)).findByCustomerId(eq(CUSTOMER_ID1));
  }

  @Test
  void whenCartDoesntExistCreateNew() {
    Cart cart = from(Cart.class).gimme(VALID_CART1);

    given(cartDAO.save(any(Cart.class))).willReturn(cart);
    given(cartDAO.findByCustomerId(anyString())).willReturn(List.of()).willReturn(List.of(cart));

    cartResource = new CartResource(cartDAO, CUSTOMER_ID1);
    assertThat(cartResource.value().get()).isNotNull();
    assertThat(cartResource.value().get().getCustomerId()).isEqualTo(CUSTOMER_ID1);

    then(cartDAO).should(times(1)).save(cartCaptor.capture());
    assertThat(cartCaptor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID1);

    then(cartDAO).should(times(3)).findByCustomerId(eq(CUSTOMER_ID1));
  }

  @Test
  void whenDestroyRemoveItem() {
    Cart cart = from(Cart.class).gimme(VALID_CART1);

    given(cartDAO.findByCustomerId(anyString())).willReturn(List.of(cart));
    doNothing().when(cartDAO).delete(any(Cart.class));

    cartResource = new CartResource(cartDAO, CUSTOMER_ID1);
    cartResource.destroy().run();

    then(cartDAO).should(times(1)).findByCustomerId(eq(CUSTOMER_ID1));
    then(cartDAO).should(times(1)).delete(cartCaptor.capture());
    assertThat(cartCaptor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID1);
  }

  @Test
  void whenDestroyOnEmptyStillEmpty() {
    Cart cart = from(Cart.class).gimme(VALID_CART1);

    given(cartDAO.save(any(Cart.class))).willReturn(cart);
    given(cartDAO.findByCustomerId(anyString())).willReturn(List.of()).willReturn(List.of(cart));
    doNothing().when(cartDAO).delete(any(Cart.class));

    cartResource = new CartResource(cartDAO, CUSTOMER_ID1);
    cartResource.destroy().run();

    then(cartDAO).should(times(2)).findByCustomerId(eq(CUSTOMER_ID1));

    then(cartDAO).should(times(1)).save(cartCaptor.capture());
    assertThat(cartCaptor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID1);

    then(cartDAO).should(times(1)).delete(cartCaptor.capture());
    assertThat(cartCaptor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID1);
  }

  @Test
  void whenCreateDoCreate() {
    Cart cart = from(Cart.class).gimme(VALID_CART1);

    given(cartDAO.save(any(Cart.class))).willReturn(cart);

    cartResource = new CartResource(cartDAO, CUSTOMER_ID1);
    cartResource.create().get();

    then(cartDAO).should(times(1)).save(cartCaptor.capture());
    assertThat(cartCaptor.getValue().getCustomerId()).isEqualTo(CUSTOMER_ID1);

    then(cartDAO).should(never()).findByCustomerId(anyString());
  }
}
