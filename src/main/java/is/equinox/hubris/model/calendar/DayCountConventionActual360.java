package is.equinox.hubris.model.calendar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

public class DayCountConventionActual360 implements DayCountConvention {

  @Override
  public BigDecimal factor(LocalDate d1, LocalDate d2) {
    var factor = ChronoUnit.DAYS.between(d1, d2) / 360.0;
    return new BigDecimal(factor);
  }
}
