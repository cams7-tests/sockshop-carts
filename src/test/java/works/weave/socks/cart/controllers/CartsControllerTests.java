package works.weave.socks.cart.controllers;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.data.mongodb.core.query.Criteria.where;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static works.weave.socks.cart.template.CartTemplate.CUSTOMER_ID1;
import static works.weave.socks.cart.template.CartTemplate.CUSTOMER_ID2;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_CART1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_CART2;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM2;

import br.com.six2six.fixturefactory.loader.FixtureFactoryLoader;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.List;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import works.weave.socks.cart.entities.Cart;
import works.weave.socks.cart.entities.Item;
import works.weave.socks.cart.template.DomainTemplateLoader;

@SpringBootTest
@TestPropertySource(properties = "spring.mongodb.embedded.version=3.5.5")
public class CartsControllerTests {

  private static final String ID_FIELD = "_id";

  @Autowired private ObjectMapper objectMapper;

  @Autowired private WebApplicationContext applicationContext;

  @Autowired private MongoTemplate mongoTemplate;

  private MockMvc mockMvc;

  @BeforeAll
  static void loadTemplates() {
    FixtureFactoryLoader.loadTemplates(getPackageName(DomainTemplateLoader.class));
  }

  @BeforeEach
  void setup() {
    mockMvc = MockMvcBuilders.webAppContextSetup(applicationContext).build();
  }

  @Test
  void whenGetCart_thenReturns200() throws Exception {
    mockMvc
        .perform(get(String.format("/carts/%s", CUSTOMER_ID1)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.customerId", is(CUSTOMER_ID1)))
        .andExpect(jsonPath("$.items", empty()));

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where("customerId").is(CUSTOMER_ID1)),
                Cart.COLLECTION_NAME))
        .isTrue();
    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).exists(true)),
                Item.COLLECTION_NAME))
        .isFalse();

    mongoTemplate.dropCollection(Cart.COLLECTION_NAME);
  }

  @Test
  void whenGetCartWithItems_thenReturns200() throws Exception {
    Cart cart = from(Cart.class).gimme(VALID_CART1);
    Item item = from(Item.class).gimme(VALID_ITEM1);

    mongoTemplate.insert(item, Item.COLLECTION_NAME);
    mongoTemplate.insert(cart, Cart.COLLECTION_NAME);

    mockMvc
        .perform(get(String.format("/carts/%s", CUSTOMER_ID1)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.customerId", is(CUSTOMER_ID1)))
        .andExpect(jsonPath("$.items", hasSize(1)))
        .andExpect(jsonPath("$.items[0].id", is(item.getId())));

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).is(cart.getId())),
                Cart.COLLECTION_NAME))
        .isTrue();
    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).is(item.getId())),
                Item.COLLECTION_NAME))
        .isTrue();

    mongoTemplate.dropCollection(Cart.COLLECTION_NAME);
    mongoTemplate.dropCollection(Item.COLLECTION_NAME);
  }

  @Test
  void whenDeleteCart_thenReturns202() throws Exception {
    Cart cart = from(Cart.class).gimme(VALID_CART1);
    cart = getCopy(cart, Cart.class);
    cart.setItems(List.of());

    mongoTemplate.insert(cart, Cart.COLLECTION_NAME);

    mockMvc
        .perform(delete(String.format("/carts/%s", CUSTOMER_ID1)))
        .andExpect(status().is2xxSuccessful());

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).is(cart.getId())),
                Cart.COLLECTION_NAME))
        .isFalse();
    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).exists(true)),
                Item.COLLECTION_NAME))
        .isFalse();

    mongoTemplate.dropCollection(Cart.COLLECTION_NAME);
  }

  @Test
  void whenMergeItemsInCarts_thenReturns202() throws Exception {
    Cart cart1 = from(Cart.class).gimme(VALID_CART1);
    Item item1 = from(Item.class).gimme(VALID_ITEM1);
    Cart cart2 = from(Cart.class).gimme(VALID_CART2);
    Item item2 = from(Item.class).gimme(VALID_ITEM2);

    mongoTemplate.insert(List.of(item1, item2), Item.COLLECTION_NAME);
    mongoTemplate.insert(List.of(cart1, cart2), Cart.COLLECTION_NAME);

    mockMvc
        .perform(get(String.format("/carts/%s/merge?sessionId=%s", CUSTOMER_ID1, CUSTOMER_ID2)))
        .andExpect(status().is2xxSuccessful());

    assertThat(
            mongoTemplate.exists(
                new Query()
                    .addCriteria(new Criteria().andOperator(where(ID_FIELD).is(cart1.getId()))),
                Cart.COLLECTION_NAME))
        .isTrue();
    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(where(ID_FIELD).is(cart2.getId())), Cart.COLLECTION_NAME))
        .isFalse();
    assertThat(
            mongoTemplate.exists(
                new Query()
                    .addCriteria(
                        new Criteria()
                            .andOperator(
                                where(ID_FIELD).is(item1.getId()),
                                where("unitPrice").is(item1.getUnitPrice()))),
                Item.COLLECTION_NAME))
        .isTrue();
    assertThat(
            mongoTemplate.exists(
                new Query()
                    .addCriteria(
                        new Criteria()
                            .andOperator(
                                where(ID_FIELD).is(item2.getId()),
                                where("unitPrice").is(item2.getUnitPrice()))),
                Item.COLLECTION_NAME))
        .isTrue();

    mongoTemplate.dropCollection(Cart.COLLECTION_NAME);
    mongoTemplate.dropCollection(Item.COLLECTION_NAME);
  }

  private <T> T getCopy(T object, Class<T> type)
      throws JsonMappingException, JsonProcessingException {
    return objectMapper.readValue(objectMapper.writeValueAsString(object), type);
  }
}
