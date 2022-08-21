package works.weave.socks.cart.template;

import static br.com.six2six.fixturefactory.Fixture.of;
import static lombok.AccessLevel.PRIVATE;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM2;

import br.com.six2six.fixturefactory.Rule;
import lombok.NoArgsConstructor;
import works.weave.socks.cart.entities.Item;

@NoArgsConstructor(access = PRIVATE)
public class ItemTemplate {

  public static final String ITEM_ID1 = "62f5521b0e00db6610aee1b5";
  public static final String ITEM_ID2 = "6300f0b67156d342e1e5d91e";

  public static void loadTemplates() {
    of(Item.class)
        .addTemplate(
            VALID_ITEM1,
            new Rule() {
              {
                add("id", ITEM_ID1);
                add("itemId", "3395a43e-2d88-40de-b95f-e00e1502085b");
                add("quantity", 3);
                add("unitPrice", 18.0f);
              }
            })
        .addTemplate(
            VALID_ITEM2,
            new Rule() {
              {
                add("id", ITEM_ID2);
                add("itemId", "837ab141-399e-4c1f-9abc-bace40296bac");
                add("quantity", 5);
                add("unitPrice", 15.0f);
              }
            });
  }
}
