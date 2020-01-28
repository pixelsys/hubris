package is.equinox.core.utils.numbers;

import java.math.BigDecimal;
import java.math.RoundingMode;

public class BigDecimalCompare {

  public static RoundingMode ROUNDING_MODE = RoundingMode.HALF_UP;
  public static int DECIMAL_PRECISION = 10;

  public static boolean equal(Double value1, BigDecimal value2) {
    return equal(new BigDecimal(value1), value2, DECIMAL_PRECISION);
  }

  public static boolean equal(BigDecimal value1, BigDecimal value2) {
    return equal(value1, value2, DECIMAL_PRECISION);
  }

  /**
   * Compares two BigDecimal values up to a given @scale.
   * @param value1 first value
   * @param value2 second value
   * @param scale precision
   * @return true if they match up with the given scale
   */
  public static boolean equal(BigDecimal value1, BigDecimal value2, int scale) {
    var v1 = value1.setScale(scale, RoundingMode.HALF_UP);
    var v2 = value2.setScale(scale, RoundingMode.HALF_UP);
    return 0 == v1.compareTo(v2);
  }

}
