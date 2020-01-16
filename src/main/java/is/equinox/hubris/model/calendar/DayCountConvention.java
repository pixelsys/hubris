package is.equinox.hubris.model.calendar;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface DayCountConvention {

  BigDecimal factor(LocalDate d1, LocalDate d2);

}
