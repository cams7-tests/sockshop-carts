package works.weave.socks.cart.item;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.times;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM2;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.template.DomainTemplateLoader;

@ExtendWith(MockitoExtension.class)
public class ItemResourceTests {

  private ItemResource itemResource;
  @Mock private ItemDAO itemDAO;

  @Captor private ArgumentCaptor<Item> itemCaptor;

  @BeforeAll
  static void loadTemplates() {
    FixtureFactoryLoader.loadTemplates(getPackageName(DomainTemplateLoader.class));
  }

  @Test
  void testCreateAndDestroy() {
    Item item = from(Item.class).gimme(VALID_ITEM1);

    given(itemDAO.save(any(Item.class))).willReturn(item);
    doNothing().when(itemDAO).destroy(any(Item.class));

    itemResource = new ItemResource(itemDAO, () -> item);
    itemResource.create().get();

    then(itemDAO).should(times(1)).save(itemCaptor.capture());
    assertThat(itemCaptor.getValue()).isEqualTo(item);

    itemResource.destroy().run();

    then(itemDAO).should(times(1)).destroy(itemCaptor.capture());
    assertThat(itemCaptor.getValue()).isEqualTo(item);
  }

  @Test
  void mergedItemShouldHaveNewQuantity() {
    Item item1 = from(Item.class).gimme(VALID_ITEM1);
    Item item2 = from(Item.class).gimme(VALID_ITEM2);

    var mergedItem = new Item(item1, item2.getQuantity());

    given(itemDAO.save(any(Item.class))).willReturn(mergedItem);

    itemResource = new ItemResource(itemDAO, () -> item1);
    assertThat(itemResource.value().get()).isEqualTo(item1);
    itemResource.merge(item2).run();

    then(itemDAO).should(times(1)).save(itemCaptor.capture());
    assertThat(itemCaptor.getValue()).isEqualTo(mergedItem);
  }
}
