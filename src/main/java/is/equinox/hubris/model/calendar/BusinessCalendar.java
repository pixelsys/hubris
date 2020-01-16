package is.equinox.hubris.model.calendar;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import scala.Tuple3;
import shapeless.Tuple;

public class BusinessCalendar {

  private BusinessCalendar() {}

  public static boolean isWeekend(LocalDate date) {
    return date.getDayOfWeek().getValue() > 5;
  }

  public static List<LocalDate> couponStartSchedule(LocalDate start, LocalDate end, CouponFrequency frequency, BusinessDayConvention dayCon) {
    var p = frequency.offset();
    var s = Stream.iterate(start, x -> x.plus(p))
        .takeWhile(x -> x.isBefore(end))
        .map(x -> dayCon.businessDate(x))
        .collect(Collectors.toList());
    return s;
  }

  public static List<Tuple3<LocalDate, LocalDate, BigDecimal>> couponSchedule(LocalDate start, LocalDate end, CouponFrequency frequency, BusinessDayConvention dayCon, DayCountConvention dayCountCon) {
    var p = frequency.offset();
    var s = Stream.iterate(start, x -> x.plus(p))
        .takeWhile(x -> x.isBefore(end))
        .map(x -> {
          var d1 = dayCon.businessDate(x);
          var d2 = dayCon.businessDate(x.plus(p));
          return new Tuple3<>(d1, d2, dayCountCon.factor(d1, d2));
        })
        .collect(Collectors.toList());
    return s;
  }

}
