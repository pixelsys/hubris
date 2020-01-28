package is.equinox.hubris.model.trade.irs;

import is.equinox.core.utils.lang.StreamUtil;
import is.equinox.hubris.context.MarketDataContext;
import is.equinox.hubris.model.calendar.BusinessDayConvention;
import is.equinox.hubris.model.calendar.CouponFrequency;
import is.equinox.hubris.model.calendar.DayCountConvention;
import is.equinox.hubris.model.trade.FixedRateBond;
import is.equinox.hubris.model.trade.FloatingRateNote;
import is.equinox.hubris.model.trade.Instrument;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import lombok.Builder;
import lombok.Getter;

@Getter
public class IRSwap implements Instrument {

  @Builder
  @Getter
  public static class CouponPair {

    private final LocalDate date;
    private final BigDecimal fixedAmount;
    private final BigDecimal floatingAmount;
    private BigDecimal discountFactor;

    public BigDecimal netCouponValue() {
      return floatingAmount.subtract(fixedAmount);
    }

    public BigDecimal presentValue() {
      return discountFactor.multiply(netCouponValue());
    }

    public void setDiscountFactor(BigDecimal df) {
      this.discountFactor = df;
    }

  }

  private final String tradeId;
  private final LocalDate effectiveDate;
  private final FixedRateBond fixedLeg;
  private final FloatingRateNote floatingLeg;
  private List<CouponPair> couponPairList;

  public IRSwap(String tradeId, LocalDate effectiveDate, FixedRateBond fixedLeg, FloatingRateNote floatingLeg) {
    this.tradeId = tradeId;
    this.effectiveDate = effectiveDate;
    this.fixedLeg = fixedLeg;
    this.floatingLeg = floatingLeg;
  }

  public static IRSwap create(String tradeId, BigDecimal notional, CouponFrequency cf, BigDecimal fixedCouponAmount,
      BusinessDayConvention dayCon, DayCountConvention fixedDcc, DayCountConvention floatingDcc, LocalDate effectiveDate,
      LocalDate termination) {
    var fixedLeg = FixedRateBond.builder()
        .tradeId(tradeId + "_fixedLeg")
        .effectiveDate(effectiveDate)
        .maturity(termination)
        .notional(notional)
        .couponFrequency(cf)
        .dayCon(dayCon)
        .dayCountCon(fixedDcc)
        .rate(fixedCouponAmount)
        .build();
    var floatingLeg = FloatingRateNote.builder()
        .tradeId(tradeId + "_floatingLeg")
        .effectiveDate(effectiveDate)
        .maturity(termination)
        .notional(notional)
        .couponFrequency(cf)
        .dayCon(dayCon)
        .dayCountCon(floatingDcc)
        .zeroCurve(Optional.empty())
        .mktDataCtx(Optional.empty())
        .build();
    return new IRSwap(tradeId, effectiveDate, fixedLeg, floatingLeg);
  }

  @Override
  public String tradeId() {
    return tradeId;
  }

  @Override
  public LocalDate effectiveDate() {
    return effectiveDate;
  }

  public List<CouponPair> couponPairs() {
    if(null == couponPairList) {
      couponPairList = generateCouponPairList();
    }
    return couponPairList;
  }

  private List<CouponPair> generateCouponPairList() {
    var fixedLegCoupons = fixedLeg.coupons();
    System.out.println("fixed Leg coupons: " + fixedLegCoupons);
    var floatingLegCoupons = floatingLeg.coupons();
    System.out.println("floating leg coupons: " + floatingLegCoupons);
    var result = new ArrayList<CouponPair>();
    StreamUtil.zip(fixedLegCoupons.stream(), floatingLegCoupons.stream()).map(t -> {
      var cp = CouponPair.builder()
          .date(t._1.getStartDate())
          .fixedAmount(t._1.getAmount())
          .floatingAmount(t._2.getAmount())
          //.discountFactor()
          .build();
      return cp;
    });
    return result;
  }

  public BigDecimal price(LocalDate asOfDate) {
    return generateCouponPairList().stream()
        .map(cp -> cp.netCouponValue())
        .reduce(BigDecimal.ZERO, (v1, v2) -> v1.add(v2));
  }

}
