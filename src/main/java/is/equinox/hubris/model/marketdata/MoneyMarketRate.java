package is.equinox.hubris.model.marketdata;

import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
@ToString(callSuper = true)
public final class MoneyMarketRate extends Rate {

  private final String tenor;

}
