package is.equinox.hubris.model.marketdata;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import lombok.Getter;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@ToString
public abstract class Rate implements MarketData {

  protected LocalDate startDate;
  protected LocalDate endDate;
  protected BigDecimal rate;

  public Long days() {
    return ChronoUnit.DAYS.between(startDate, endDate);
  }

}
