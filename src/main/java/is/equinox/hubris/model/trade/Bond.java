package is.equinox.hubris.model.trade;

import is.equinox.hubris.model.calendar.BusinessCalendar;
import is.equinox.hubris.model.calendar.BusinessDayConvention;
import is.equinox.hubris.model.calendar.CouponFrequency;
import is.equinox.hubris.model.calendar.DayCountConvention;
import is.equinox.hubris.model.trade.data.Coupon;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import lombok.Getter;
import lombok.experimental.SuperBuilder;
import scala.Tuple3;

@SuperBuilder
@Getter
public abstract class Bond implements Instrument {

  protected String tradeId;
  protected LocalDate effectiveDate;
  protected LocalDate maturity;
  protected BigDecimal notional;
  protected CouponFrequency couponFrequency;
  protected BusinessDayConvention dayCon;
  protected DayCountConvention dayCountCon;

  public abstract List<Coupon> coupons();

  @Override
  public String tradeId() {
    return tradeId;
  }

  @Override
  public LocalDate effectiveDate() {
    return effectiveDate;
  }

  public List<Tuple3<LocalDate, LocalDate, BigDecimal>> couponSchedule() {
    return BusinessCalendar.couponSchedule(effectiveDate, maturity, couponFrequency, dayCon, dayCountCon);
  }

}
