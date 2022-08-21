package works.weave.socks.cart.template;

import static br.com.six2six.fixturefactory.Fixture.from;
import static br.com.six2six.fixturefactory.Fixture.of;
import static java.util.List.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_CART1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_CART2;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM2;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.entities.Item;

@NoArgsConstructor(access = PRIVATE)
public class CartTemplate {

  public static final String CART_ID1 = "62f2e157d42dd339a300c578";
  public static final String CUSTOMER_ID1 = "57a98d98e4b00679b4a830b2";
  public static final String CART_ID2 = "6300f0357156d342e1e5d91c";
  public static final String CUSTOMER_ID2 = "6300f07d7156d342e1e5d91d";

  public static void loadTemplates() {
    of(Cart.class)
        .addTemplate(
            VALID_CART1,
            new Rule() {
              {
                add("id", CART_ID1);
                add("customerId", CUSTOMER_ID1);
                add(
                    "items",
                    of(new Item(((Item) from(Item.class).gimme(VALID_ITEM1)).getId(), null, 0, 0)));
              }
            })
        .addTemplate(
            VALID_CART2,
            new Rule() {
              {
                add("id", CART_ID2);
                add("customerId", CUSTOMER_ID2);
                add(
                    "items",
                    of(new Item(((Item) from(Item.class).gimme(VALID_ITEM2)).getId(), null, 0, 0)));
              }
            });
  }
}
