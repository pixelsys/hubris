package is.equinox.hubris.model.calendar;

import java.time.DayOfWeek;
import java.time.LocalDate;

public final class ModifiedFollowing implements BusinessDayConvention {

  @Override
  public LocalDate businessDate(LocalDate date) {
    if(BusinessCalendar.isWeekend(date)) {
      var isSaturday = date.getDayOfWeek() == DayOfWeek.SATURDAY;
      var dayOffset = 0;
      if(date.getDayOfMonth() == date.lengthOfMonth()) {
        dayOffset = isSaturday ? -1 : -2;
      } else {
        dayOffset = isSaturday ? 2 : 1;
      }
      return LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth() + dayOffset);
    } else {
      return date;
    }
  }

}
