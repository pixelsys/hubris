package is.equinox.hubris.model.trade.irs;

import static is.equinox.core.test.NumbersEquals.assertEqualsBD;
import static org.junit.Assert.assertEquals;

import is.equinox.hubris.context.MarketDataContext;
import is.equinox.hubris.dal.YieldCurveDao;
import is.equinox.hubris.model.calendar.DayCountConvention30360;
import is.equinox.hubris.model.calendar.DayCountConventionActual360;
import is.equinox.hubris.model.calendar.ModifiedFollowing;
import is.equinox.hubris.model.calendar.SemiAnnualCouponFrequency;
import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.Test;

public class IRSwapTest {

  @Test
  public void createAndPriceSwap_hasCouponsAndPVZeroAtInception() throws Exception {
    final var tradeId = "irswap1";
    final var notional = new BigDecimal(1000000);
    final var couponFrequency = new SemiAnnualCouponFrequency();
    final var fixedCouponAmount = new BigDecimal(0.0124); // 1.24%
    final var floatingCouponIndex = "usdlibor_6m";
    final var dayConvention = new ModifiedFollowing();
    final var fixedCouponDaycount = new DayCountConvention30360();
    final var floatingCouponDaycount = new DayCountConventionActual360();
    final var effectiveDate = LocalDate.of(2011, 11, 14);
    final var terminationDate = LocalDate.of(2016, 11, 14);
    final var creationDate = LocalDate.of(2011, 11, 10);
    var swap = IRSwap.create(tradeId, notional, couponFrequency, fixedCouponAmount,
        dayConvention, fixedCouponDaycount, floatingCouponDaycount, effectiveDate, terminationDate);

    // basic verification
    assert(null != swap);
    assertEquals(tradeId, swap.tradeId());
    assertEquals(effectiveDate, swap.effectiveDate());

    // add market data
    var floatingLeg = swap.getFloatingLeg();
    assert(null != floatingLeg);
    var mktDataCtx = new MarketDataContext(creationDate);
    floatingLeg.setMarketData(floatingCouponIndex, mktDataCtx);
    mktDataCtx.loadYieldCurve(floatingCouponIndex, floatingLeg.getDayCon(), floatingLeg.getDayCountCon());

    // coupons
    var couponPairs = swap.couponPairs();
    assert(null != couponPairs);
    assertEquals(10, couponPairs.size());

    // PV
    assertEqualsBD(BigDecimal.ZERO, swap.price(creationDate));
  }

}
