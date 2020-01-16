package is.equinox.hubris.model.calendar;

import java.time.LocalDate;

public final class NoAdjustment implements BusinessDayConvention {

  @Override
  public LocalDate businessDate(LocalDate date) {
    return date;
  }

}
