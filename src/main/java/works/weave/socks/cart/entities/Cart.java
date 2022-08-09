package works.weave.socks.cart.entities;

import java.util.ArrayList;
import java.util.List;
import javax.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@Document
public class Cart {
  @Id private String id;

  @NotNull private String customerId;

  @DBRef private List<Item> items;

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
