package works.weave.socks.cart.controllers;

import static br.com.six2six.fixturefactory.Fixture.from;
import static org.apache.commons.lang3.ClassUtils.getPackageName;
import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.patch;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static works.weave.socks.cart.template.CartTemplate.CUSTOMER_ID1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_CART1;
import static works.weave.socks.cart.template.DomainTemplateLoader.VALID_ITEM1;

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
public class ItemsControllerTests {

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
  void whenAddItem_thenReturns201() throws Exception {
    Cart cart = from(Cart.class).gimme(VALID_CART1);
    cart = getCopy(cart, Cart.class);
    cart.setItems(List.of());

    mongoTemplate.insert(cart, Cart.COLLECTION_NAME);

    Item item = from(Item.class).gimme(VALID_ITEM1);
    item = getCopy(item, Item.class);
    item.setId(null);

    mockMvc
        .perform(
            post(String.format("/carts/%s/items", CUSTOMER_ID1))
                .content(objectMapper.writeValueAsString(item))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", notNullValue()))
        .andExpect(jsonPath("$.itemId", is(item.getItemId())))
        .andExpect(jsonPath("$.quantity", is(item.getQuantity())))
        .andExpect(jsonPath("$.unitPrice", notNullValue()));

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).is(cart.getId())),
                Cart.COLLECTION_NAME))
        .isTrue();
    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).exists(true)),
                Item.COLLECTION_NAME))
        .isTrue();

    mongoTemplate.dropCollection(Cart.COLLECTION_NAME);
    mongoTemplate.dropCollection(Item.COLLECTION_NAME);
  }

  @Test
  void whenUpdateItem_thenReturns201() throws Exception {
    Cart cart = from(Cart.class).gimme(VALID_CART1);
    Item item = from(Item.class).gimme(VALID_ITEM1);

    mongoTemplate.insert(item, Item.COLLECTION_NAME);
    mongoTemplate.insert(cart, Cart.COLLECTION_NAME);

    mockMvc
        .perform(
            post(String.format("/carts/%s/items", CUSTOMER_ID1))
                .content(objectMapper.writeValueAsString(item))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().isCreated())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", is(item.getId())))
        .andExpect(jsonPath("$.itemId", is(item.getItemId())))
        .andExpect(jsonPath("$.quantity", is(item.getQuantity() + 1)))
        .andExpect(jsonPath("$.unitPrice", notNullValue()));

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
  void whenGetItems_thenReturns200() throws Exception {
    Cart cart = from(Cart.class).gimme(VALID_CART1);
    Item item = from(Item.class).gimme(VALID_ITEM1);

    mongoTemplate.insert(item, Item.COLLECTION_NAME);
    mongoTemplate.insert(cart, Cart.COLLECTION_NAME);

    mockMvc
        .perform(get(String.format("/carts/%s/items", CUSTOMER_ID1)))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$[0].id", is(item.getId())))
        .andExpect(jsonPath("$[0].itemId", is(item.getItemId())))
        .andExpect(jsonPath("$[0].quantity", is(item.getQuantity())))
        .andExpect(jsonPath("$[0].unitPrice", notNullValue()));

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
  void whenGetItem_thenReturns200() throws Exception {
    Cart cart = from(Cart.class).gimme(VALID_CART1);
    Item item = from(Item.class).gimme(VALID_ITEM1);

    mongoTemplate.insert(item, Item.COLLECTION_NAME);
    mongoTemplate.insert(cart, Cart.COLLECTION_NAME);

    mockMvc
        .perform(get(String.format("/carts/%s/items/%s", CUSTOMER_ID1, item.getItemId())))
        .andExpect(status().isOk())
        .andExpect(content().contentType(APPLICATION_JSON_VALUE))
        .andExpect(jsonPath("$.id", is(item.getId())))
        .andExpect(jsonPath("$.itemId", is(item.getItemId())))
        .andExpect(jsonPath("$.quantity", is(item.getQuantity())))
        .andExpect(jsonPath("$.unitPrice", notNullValue()));

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
  void whenDeleteItem_thenReturns202() throws Exception {
    Cart cart = from(Cart.class).gimme(VALID_CART1);
    Item item = from(Item.class).gimme(VALID_ITEM1);

    mongoTemplate.insert(item, Item.COLLECTION_NAME);
    mongoTemplate.insert(cart, Cart.COLLECTION_NAME);

    mockMvc
        .perform(delete(String.format("/carts/%s/items/%s", CUSTOMER_ID1, item.getItemId())))
        .andExpect(status().is2xxSuccessful());

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).is(cart.getId())),
                Cart.COLLECTION_NAME))
        .isTrue();
    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).is(item.getId())),
                Item.COLLECTION_NAME))
        .isFalse();

    mongoTemplate.dropCollection(Cart.COLLECTION_NAME);
    mongoTemplate.dropCollection(Item.COLLECTION_NAME);
  }

  @Test
  void whenUpdateItemQuantity_thenReturns202() throws Exception {
    Cart cart = from(Cart.class).gimme(VALID_CART1);
    Item item = from(Item.class).gimme(VALID_ITEM1);

    mongoTemplate.insert(item, Item.COLLECTION_NAME);
    mongoTemplate.insert(cart, Cart.COLLECTION_NAME);

    var newQuantity = 7;

    mockMvc
        .perform(
            patch(String.format("/carts/%s/items", CUSTOMER_ID1))
                .content(
                    objectMapper.writeValueAsString(
                        new Item(null, item.getItemId(), newQuantity, 0)))
                .contentType(APPLICATION_JSON_VALUE))
        .andExpect(status().is2xxSuccessful());

    assertThat(
            mongoTemplate.exists(
                new Query().addCriteria(Criteria.where(ID_FIELD).is(cart.getId())),
                Cart.COLLECTION_NAME))
        .isTrue();
    assertThat(
            mongoTemplate.exists(
                new Query()
                    .addCriteria(
                        new Criteria()
                            .andOperator(
                                Criteria.where(ID_FIELD).is(item.getId()),
                                Criteria.where("quantity").is(newQuantity))),
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
