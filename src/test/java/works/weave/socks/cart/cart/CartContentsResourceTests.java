package works.weave.socks.cart.cart;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.times;
import static works.weave.socks.cart.template.CartTemplate.CUSTOMER_ID1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM1;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.template.DomainTemplateLoader;

@ExtendWith(MockitoExtension.class)
public class CartContentsResourceTests {

  private CartContentsResource contentsResource;

  @Mock private CartDAO cartDAO;
  @Spy private Resource<Cart> cartResource = new CartResourceFake(CUSTOMER_ID1);

  @Captor private ArgumentCaptor<Cart> cartCaptor;

  @BeforeAll
  static void loadTemplates() {
    FixtureFactoryLoader.loadTemplates(getPackageName(DomainTemplateLoader.class));
  }

  @Test
  void shouldAddAndReturnContents() {
    Item item = from(Item.class).gimme(VALID_ITEM1);

    contentsResource = new CartContentsResource(cartDAO, () -> cartResource);
    contentsResource.add(() -> item).run();
    assertThat(contentsResource.contents().get()).hasSize(1);
    assertThat(contentsResource.contents().get()).containsExactlyInAnyOrder(item);

    then(cartDAO).should(times(1)).save(cartCaptor.capture());

    var cart = cartCaptor.getValue();
    assertThat(cart.getCustomerId()).isEqualTo(CUSTOMER_ID1);
    assertThat(cart.getItems()).hasSize(1);
    assertThat(cart.getItems().get(0)).isEqualTo(item);
  }

  @Test
  void shouldStartEmpty() {
    contentsResource = new CartContentsResource(cartDAO, () -> cartResource);
    assertThat(contentsResource.contents().get()).hasSize(0);
  }

  @Test
  void shouldDeleteItemFromCart() {
    Item item1 = from(Item.class).gimme(VALID_ITEM1);
    var item2 = new Item(item1.getItemId());

    contentsResource = new CartContentsResource(cartDAO, () -> cartResource);
    contentsResource.add(() -> item1).run();
    assertThat(contentsResource.contents().get()).hasSize(1);
    assertThat(contentsResource.contents().get()).containsExactlyInAnyOrder(item1);
    contentsResource.delete(() -> item2).run();
    assertThat(contentsResource.contents().get()).hasSize(0);

    then(cartDAO).should(times(2)).save(cartCaptor.capture());

    var carts = cartCaptor.getAllValues();

    assertThat(carts).hasSize(2);
    assertThat(carts.get(0).getCustomerId()).isEqualTo(CUSTOMER_ID1);
    assertThat(carts.get(0).getItems()).isEmpty();
    assertThat(carts.get(1).getCustomerId()).isEqualTo(CUSTOMER_ID1);
    assertThat(carts.get(1).getItems()).isEmpty();
  }
}
