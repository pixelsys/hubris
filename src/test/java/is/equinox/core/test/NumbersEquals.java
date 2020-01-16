package is.equinox.core.test;

import is.equinox.core.utils.numbers.BigDecimalCompare;
import java.math.BigDecimal;

public class NumbersEquals {

  private NumbersEquals() {}

  public static void assertEqualsBD(BigDecimal expected, BigDecimal actual) {
    if(null == expected || null == actual) {
      throw new IllegalArgumentException("Values can't be null");
    }
    if(!BigDecimalCompare.equal(expected, actual)) {
     throw new AssertionError("BigDecimal values don't match, it was: " + actual + ", expected: " + expected);
    }
  }

  public static void assertEqualsBD(Double expected, BigDecimal actual) {
    assertEqualsBD(new BigDecimal(expected), actual);
  }

}
