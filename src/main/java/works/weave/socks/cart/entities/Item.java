package works.weave.socks.cart.entities;

import static works.weave.socks.cart.entities.Item.COLLECTION_NAME;

import javax.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Data
@EqualsAndHashCode(exclude = {"id", "quantity", "unitPrice"})
@Document(collection = COLLECTION_NAME)
public class Item {

  public static final String COLLECTION_NAME = "item";

  private static final String DEFAULT_ID = null;
  private static final String DEFAULT_ITEM_ID = "";
  private static final int DEFAULT_QUANTITY = 1;
  private static final float DEFAULT_UNIT_PRICE = 0F;

  @Id private String id;

  @NotNull(message = "Item Id must not be null")
  private String itemId;

  private int quantity;
  private float unitPrice;

  public Item(String id, String itemId, int quantity, float unitPrice) {
    this.id = id;
    this.itemId = itemId;
    this.quantity = quantity;
    this.unitPrice = unitPrice;
  }

  public Item() {
    this(DEFAULT_ID, DEFAULT_ITEM_ID, DEFAULT_QUANTITY, DEFAULT_UNIT_PRICE);
  }

  public Item(String itemId) {
    this(DEFAULT_ID, itemId, DEFAULT_QUANTITY, DEFAULT_UNIT_PRICE);
  }

  public Item(Item item, String id) {
    this(id, item.itemId, item.quantity, item.unitPrice);
  }

  public Item(Item item, int quantity) {
    this(item.id, item.itemId, quantity, item.unitPrice);
  }
}
