package works.weave.socks.cart.entities;

import static works.weave.socks.cart.entities.Cart.COLLECTION_NAME;

import io.swagger.v3.oas.annotations.media.Schema;
import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document(collection = COLLECTION_NAME)
public class Cart {

  public static final String COLLECTION_NAME = "cart";

  @Schema(example = "62f551ab0e00db6610aee1b4", description = "Cart id")
  @Id
  private String id;

  @Schema(example = "57a98d98e4b00679b4a830b2", description = "Customer id")
  @NotNull
  private String customerId;

  @Schema(description = "Items")
  @DBRef
  private List<Item> items;

  private Cart(String id, String customerId, List<Item> items) {
    super();
    this.id = id;
    this.customerId = customerId;
    this.items = items;
  }

  public Cart(String customerId) {
    this(null, customerId, new ArrayList<>());
  }

  public Cart() {
    this(null);
  }

  public List<Item> contents() {
    return items;
  }

  public Cart add(Item item) {
    items.add(item);
    return this;
  }

  public Cart remove(Item item) {
    items.remove(item);
    return this;
  }
}
