package is.equinox.hubris.dal;

import is.equinox.core.utils.csv.SimpleCsvParser;
import is.equinox.hubris.model.calendar.BusinessDayConvention;
import is.equinox.hubris.model.calendar.DayCountConvention;
import is.equinox.hubris.model.marketdata.FRARate;
import is.equinox.hubris.model.marketdata.MoneyMarketRate;
import is.equinox.hubris.model.marketdata.ParSwapRate;
import is.equinox.hubris.model.marketdata.Term;
import is.equinox.hubris.model.marketdata.curve.ZeroCurve;
import is.equinox.hubris.model.marketdata.curve.bootstrap.NWayBootstrap;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;

public class YieldCurveDao {

  private YieldCurveDao() {}

  public static ZeroCurve loadFromCsv(String name, LocalDate cob, String csvString, BusinessDayConvention dayCon, DayCountConvention dayCountCon) {
    var csv = SimpleCsvParser.fromString(csvString);
    var points = csv.stream().skip(1).map(row -> {
      var instrument = row.get(0);
      var term = row.get(1);
      var rate = new BigDecimal(row.get(2));
      var startDate = dayCon.businessDate(cob.plusDays(Term.settleDays(term)));
      var endDate = dayCon.businessDate(Term.calculateTermEnd(startDate, term));
      if("cash".equalsIgnoreCase(instrument)) {
        return MoneyMarketRate.builder().tenor(term).startDate(startDate).endDate(endDate).rate(rate).build();
      } else if ("fra".equalsIgnoreCase(instrument)) {
        return FRARate.builder().contract(term).startDate(startDate).endDate(endDate).rate(rate).build();
      } else if ("swap".equalsIgnoreCase(instrument)) {
        return ParSwapRate.builder().tenor(term).startDate(startDate).endDate(endDate).rate(rate).build();
      } else {
        throw new IllegalStateException("Invalid instrument: " + instrument);
      }
    }).collect(Collectors.toList());
    var bootstrapper = new NWayBootstrap(cob, dayCountCon);
    var discountFactors = bootstrapper.bootstrap(points);
    //var ciCtx = new CurveInterpolationContext(discountFactors.map{ case(k, v) => k.endDate -> v }, interpolator)
    //new ZeroCurve(name, points, discountFactors, ciCtx.interpolate)
    return ZeroCurve.builder()
        .name(name)
        .points(points)
        .discountFactors(discountFactors)
        .build();
  }

}
