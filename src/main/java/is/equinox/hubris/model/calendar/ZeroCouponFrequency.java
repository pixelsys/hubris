package is.equinox.hubris.model.calendar;

import java.time.Period;

public final class ZeroCouponFrequency implements CouponFrequency {

  @Override
  public Period offset() {
    return Period.ZERO;
  }

}
