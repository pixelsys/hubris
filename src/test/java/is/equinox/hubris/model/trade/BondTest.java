package is.equinox.hubris.model.trade;

import static is.equinox.core.test.NumbersEquals.assertEqualsBD;

import is.equinox.core.utils.lang.StreamUtil;
import is.equinox.hubris.model.calendar.DayCountConvention30360;
import is.equinox.hubris.model.calendar.ModifiedFollowing;
import is.equinox.hubris.model.calendar.SemiAnnualCouponFrequency;
import is.equinox.hubris.model.trade.data.Coupon;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collector;
import java.util.stream.Collectors;
import org.junit.Test;

public class BondTest {

  public static FixedRateBond createTestFixedRateBond() {
    return FixedRateBond.builder()
        .effectiveDate(LocalDate.of(2011, 11, 14))
        .notional(new BigDecimal(1000000.0))
        .couponFrequency(new SemiAnnualCouponFrequency())
        .dayCon(new ModifiedFollowing())
        .dayCountCon(new DayCountConvention30360())
        .rate(new BigDecimal(0.0124))
        .maturity(LocalDate.of(2016, 11, 14))
        .build();
  }

  @Test
  public void FixedRateBond_shouldHaveCorrectCoupons() {
    var frb = createTestFixedRateBond();
    var coupons = frb.coupons();
    var expected = List.of(6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6268.888888, 6200.0, 6131.11111);
    var ci = coupons.iterator();
    expected.forEach(x -> assertEqualsBD(x, ci.next().getAmount()));
  }

}
