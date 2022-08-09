package works.weave.socks.cart.action;

import static org.hamcrest.core.IsEqual.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.Test;

public class UnitFirstResultOrDefault {
  @Test
  public void whenEmptyUsesDefault() {
    String defaultValue = "test";
    FirstResultOrDefault<String> CUT =
        new FirstResultOrDefault<>(Collections.emptyList(), () -> defaultValue);
    assertThat(CUT.get(), equalTo(defaultValue));
  }

  @Test
  public void whenNotEmptyUseFirst() {
    String testValue = "test";
    FirstResultOrDefault<String> CUT =
        new FirstResultOrDefault<>(Arrays.asList(testValue), () -> "nonDefault");
    assertThat(CUT.get(), equalTo(testValue));
  }

  @Test
  public void whenMultipleNotEmptyUseFirst() {
    String testValue = "test";
    FirstResultOrDefault<String> CUT =
        new FirstResultOrDefault<>(Arrays.asList(testValue, "test2"), () -> "nonDefault");
    assertThat(CUT.get(), equalTo(testValue));
  }
}
