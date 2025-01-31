package works.weave.socks.cart.entities;

import static works.weave.socks.cart.entities.Item.COLLECTION_NAME;

import io.swagger.v3.oas.annotations.media.Schema;
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

  @Schema(example = "62f5521b0e00db6610aee1b5", description = "Item id")
  @Id
  private String id;

  @Schema(example = "3395a43e-2d88-40de-b95f-e00e1502085b", description = "Catalogue item id")
  @NotNull(message = "Item Id must not be null")
  private String itemId;

  @Schema(example = "3", description = "Item quantity")
  private int quantity;

  @Schema(example = "18.0", description = "Item unit price")
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
