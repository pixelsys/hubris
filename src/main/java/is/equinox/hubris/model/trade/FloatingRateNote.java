package is.equinox.hubris.model.trade;

import static is.equinox.core.utils.numbers.BigDecimalCompare.DECIMAL_PRECISION;
import static is.equinox.core.utils.numbers.BigDecimalCompare.ROUNDING_MODE;

import is.equinox.hubris.context.MarketDataContext;
import is.equinox.hubris.model.YieldCurve;
import is.equinox.hubris.model.ZeroCurve;
import is.equinox.hubris.model.trade.data.Coupon;
import java.io.IOException;
import java.math.BigDecimal;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import lombok.Getter;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
public class FloatingRateNote extends Bond {

  private List<Coupon> coupons;
  private Optional<MarketDataContext> mktDataCtx;
  private Optional<String> zeroCurve;

  public void setMarketData(String zeroCurveName, MarketDataContext mktDataCtx) {
    this.zeroCurve = Optional.of(zeroCurveName);
    this.mktDataCtx = Optional.of(mktDataCtx);
    try {
      mktDataCtx.loadYieldCurve(zeroCurveName, dayCon, dayCountCon);
    } catch (URISyntaxException | IOException e) {
      var errMsg = "Error while loading yield curve: " + e.getMessage();
      System.err.println(errMsg);
      e.printStackTrace(System.err);
      throw new IllegalStateException(errMsg);
    }
  }

  @Override
  public List<Coupon> coupons() {
    var couponSchedule = couponSchedule();
    BigDecimal df1 = BigDecimal.ZERO;
    BigDecimal df2 = BigDecimal.ZERO;
    var coupons = new ArrayList<Coupon>();
    for(var x : couponSchedule) {
      BigDecimal amount = null;
      if (zeroCurve.isPresent() && mktDataCtx.isPresent()) {
        var yc = mktDataCtx.get().getYieldCurve(zeroCurve.get());
        df2 = yc.discountFactor(x._1());
        System.out.println("FRN df: " + df2 + " for " + x._1());
        if(!BigDecimal.ZERO.equals(df1)) {
          var df = df2.divide(df1, DECIMAL_PRECISION, ROUNDING_MODE);
          System.out.println("df2/df1=" + df);
          var forwardRate = (BigDecimal.ONE.subtract(df)).divide(df.multiply(x._3()), DECIMAL_PRECISION, ROUNDING_MODE);
          System.out.println("forward rate: " + forwardRate);
          amount = forwardRate.multiply(x._3()).multiply(notional);
        }
        df1 = df2;
      }
      var coupon = new Coupon(x._1(), x._2(), x._3(), amount);
      coupons.add(coupon);
    }
    return coupons;
  }

}
