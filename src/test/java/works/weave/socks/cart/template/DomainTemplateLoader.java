package works.weave.socks.cart.template;

import br.com.six2six.fixturefactory.loader.TemplateLoader;

public class DomainTemplateLoader implements TemplateLoader {

  public static final String GET_APP_HEALTH = "GET_APP_HEALTH";
  public static final String GET_DB_HEALTH = "GET_DB_HEALTH";
  public static final String GET_DB_HEALTH_WITH_ERROR = "GET_DB_HEALTH_WITH_ERROR";
  public static final String VALID_ITEM1 = "VALID_ITEM1";
  public static final String VALID_ITEM2 = "VALID_ITEM2";
  public static final String VALID_CART1 = "VALID_CART1";
  public static final String VALID_CART2 = "VALID_CART2";

  @Override
  public void load() {
    HealthCheckTemplate.loadTemplates();
    ItemTemplate.loadTemplates();
    CartTemplate.loadTemplates();
  }
}
