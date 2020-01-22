package is.equinox.hubris.model.marketdata;

import java.time.LocalDate;
import java.util.regex.Pattern;

public class Term {

  public static String DaysRE = "([0-9]+)D";
  public static String WeeksRE = "([0-9]+)W";
  public static String MonthsRE = "([0-9]+)M";
  public static String YearsRE = "([0-9]+)Y";

  public static boolean isOvernightTerm(String term) {
    return term.equalsIgnoreCase("ON")
        || term.equalsIgnoreCase("0D")
        ? true
        : false;
  }

  public static boolean isTomorrowTerm(String term) {
    return term.equalsIgnoreCase("T/N")
        || term.equalsIgnoreCase("TN")
        || term.equalsIgnoreCase("1D")
        ? true
        : false;
  }

  public static int settleDays(String term) {
    if (isOvernightTerm(term)) {
      return 0;
    } else if (isTomorrowTerm(term)) {
      return 1;
    } else {
      return 2;
    }
  }

  public static LocalDate calculateTermEnd(LocalDate start, String term) {
    if(isOvernightTerm(term) || isTomorrowTerm(term)) {
      return start.plusDays(1);
    } else if (term.matches(DaysRE)) {
      var matcher = Pattern.compile(DaysRE).matcher(term);
      matcher.find();
      return start.plusDays(Long.valueOf(matcher.group(1)));
    } else if (term.matches(WeeksRE)) {
      var matcher = Pattern.compile(WeeksRE).matcher(term);
      matcher.find();
      return start.plusWeeks(Long.valueOf(matcher.group(1)));
    } else if (term.matches(MonthsRE)) {
      var matcher = Pattern.compile(MonthsRE).matcher(term);
      matcher.find();
      return start.plusMonths(Long.valueOf(matcher.group(1)));
    } else if (term.matches(YearsRE)) {
      var matcher = Pattern.compile(YearsRE).matcher(term);
      matcher.find();
      return start.plusYears(Long.valueOf(matcher.group(1)));
    }
    throw new IllegalArgumentException(term);
  }

}
