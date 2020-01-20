package is.equinox.hubris.model.trade;

import is.equinox.hubris.model.calendar.BusinessDayConvention;
import is.equinox.hubris.model.calendar.CouponFrequency;
import is.equinox.hubris.model.calendar.DayCountConvention;
import is.equinox.hubris.model.trade.data.Coupon;
import java.math.BigDecimal;
import java.util.List;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class Bond extends Instrument {

  protected BigDecimal notional;
  protected CouponFrequency couponFrequency;
  protected BusinessDayConvention dayCon;
  protected DayCountConvention dayCountCon;

  public abstract List<Coupon> coupons();

}
