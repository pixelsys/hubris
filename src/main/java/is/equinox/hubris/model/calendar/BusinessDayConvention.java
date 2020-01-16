package is.equinox.hubris.model.calendar;

import java.time.LocalDate;

public interface BusinessDayConvention {

  LocalDate businessDate(LocalDate date);

}
