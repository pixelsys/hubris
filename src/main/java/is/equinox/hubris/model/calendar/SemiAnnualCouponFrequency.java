package is.equinox.hubris.model.calendar;

import java.time.Period;

public final class SemiAnnualCouponFrequency implements CouponFrequency {

  @Override
  public Period offset() {
    return Period.ofMonths(6);
  }

}
