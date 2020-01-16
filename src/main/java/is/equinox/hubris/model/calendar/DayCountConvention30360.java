package is.equinox.hubris.model.calendar;

import java.math.BigDecimal;
import java.time.LocalDate;

public final class DayCountConvention30360 implements DayCountConvention {

  @Override
  public BigDecimal factor(LocalDate d1, LocalDate d2) {
    LocalDate md1, md2;
    if(31 == d1.getDayOfMonth()) {
      md1 = d1.minusDays(1);
      md2 = 31 == d2.getDayOfMonth() ? d2.minusDays(1) : d2;
    } else {
      md1 = d1;
      md2 = d2;
    }
    var res = (360 * (md2.getYear() - md1.getYear()) + 30 * (md2.getMonthValue() - md1.getMonthValue()) + (md2.getDayOfMonth() - md1.getDayOfMonth())) / 360.0;
    return new BigDecimal(res);
  }

}
