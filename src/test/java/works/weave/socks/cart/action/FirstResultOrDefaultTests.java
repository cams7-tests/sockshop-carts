package works.weave.socks.cart.action;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.Arrays;
import java.util.Collections;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class FirstResultOrDefaultTests {
  @Test
  void whenEmptyUsesDefault() {
    var defaultValue = "test";
    var CUT = new FirstResultOrDefault<>(Collections.emptyList(), () -> defaultValue);
    assertThat(CUT.get()).isEqualTo(defaultValue);
  }

  @Test
  void whenNotEmptyUseFirst() {
    var testValue = "test";
    var CUT = new FirstResultOrDefault<>(Arrays.asList(testValue), () -> "nonDefault");
    assertThat(CUT.get()).isEqualTo(testValue);
  }

  @Test
  void whenMultipleNotEmptyUseFirst() {
    var testValue = "test";
    var CUT = new FirstResultOrDefault<>(Arrays.asList(testValue, "test2"), () -> "nonDefault");
    assertThat(CUT.get()).isEqualTo(testValue);
  }
}
