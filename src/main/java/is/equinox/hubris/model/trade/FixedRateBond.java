package is.equinox.hubris.model.trade;

import is.equinox.hubris.model.calendar.BusinessCalendar;
import is.equinox.hubris.model.trade.data.Coupon;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@Getter
@SuperBuilder
public class FixedRateBond extends Bond {

  protected BigDecimal rate;
  protected LocalDate maturity;

  /**
   * Creates coupons based on the economics of the trade
   * @return Returns an Iterator in which every item is a coupon as a tuple (Start Date, End Date, Factor, Amount)
   */
  public List<Coupon> coupons() {
    var couponSchedule = BusinessCalendar.couponSchedule(effectiveDate, maturity, couponFrequency, dayCon, dayCountCon);
    return couponSchedule.stream().map(x -> {
        var amount = rate.multiply(notional).multiply(x._3());
        return new Coupon(x._1(), x._2(), x._3(), amount);
    }).collect(Collectors.toList());
  }

  @Override
  BigDecimal value(LocalDate cob) {
    return null;
  }

}
