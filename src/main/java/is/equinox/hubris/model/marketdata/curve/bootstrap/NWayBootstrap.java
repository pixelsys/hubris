package is.equinox.hubris.model.marketdata.curve.bootstrap;

import static is.equinox.core.utils.numbers.BigDecimalCompare.DECIMAL_PRECISION;
import static is.equinox.core.utils.numbers.BigDecimalCompare.ROUNDING_MODE;

import is.equinox.hubris.model.calendar.DayCountConvention;
import is.equinox.hubris.model.calendar.DayCountConvention30360;
import is.equinox.hubris.model.marketdata.FRARate;
import is.equinox.hubris.model.marketdata.MoneyMarketRate;
import is.equinox.hubris.model.marketdata.ParSwapRate;
import is.equinox.hubris.model.marketdata.Rate;
import java.math.BigDecimal;
import java.time.Duration;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import scala.NotImplementedError;

@RequiredArgsConstructor
public class NWayBootstrap implements CurveBootstrap {

  private final LocalDate cob;
  private final DayCountConvention dayCountCon;

  public BigDecimal df(BigDecimal bootstrap, BigDecimal rate, LocalDate start, LocalDate end) {
    var value = rate
        .multiply(dayCountCon.factor(start, end))
        .add(new BigDecimal(1));
    return bootstrap.divide(value, DECIMAL_PRECISION, ROUNDING_MODE);
  }

  public static LocalDate intervalMiddle(LocalDate start, LocalDate end) {
    var days = ChronoUnit.DAYS.between(start, end);
    return start.plusDays((days / 2) - 1);
  }

  public BigDecimal dfSwap(BigDecimal swapRate, BigDecimal dfMiddle, LocalDate start, LocalDate middle, LocalDate end) {
    var x1 = BigDecimal.ONE.subtract(swapRate.multiply(dfMiddle.multiply(dayCountCon.factor(start, middle))));
    var x2 = BigDecimal.ONE.add(swapRate.multiply(dayCountCon.factor(middle, end)));
    return x1.divide(x2, DECIMAL_PRECISION, ROUNDING_MODE);
  }

  public BigDecimal dfSwap(BigDecimal bootstrap, BigDecimal swapRate, LocalDate start, LocalDate end) {
    var x1 = BigDecimal.ONE.subtract(swapRate.multiply(bootstrap));
    var x2 = new BigDecimal(1).add(swapRate.multiply(dayCountCon.factor(start, end)));
    return x1.divide(x2, DECIMAL_PRECISION, ROUNDING_MODE);
  }

  public Map<Rate, BigDecimal> bootstrap(List<Rate> points) {
    var dfMap = new HashMap<Rate, BigDecimal>();
    var bootstrapMap = new HashMap<LocalDate, BigDecimal>();
    var swapBootstrapValue = new BigDecimal(0);
    var lastPoint = points.get(0);
    for(int i = 0; i < points.size(); i++) {
      var p = points.get(i);
      BigDecimal dfValue;
      if (p instanceof MoneyMarketRate) {
        var bootstrap = p.getStartDate().equals(cob) ? new BigDecimal(1) : bootstrapMap.get(p.getStartDate());
        dfValue = df(bootstrap, p.getRate(), p.getStartDate(), p.getEndDate());
      } else if (p instanceof FRARate) {
        throw new NotImplementedError();
      } else if (p instanceof ParSwapRate) {
        if (swapBootstrapValue.compareTo(BigDecimal.ZERO) == 0) {
          // first time
          var middleDate = intervalMiddle(p.getStartDate(), p.getEndDate());
          var dfMiddle = bootstrapMap.get(middleDate);
          dfValue = dfSwap(p.getRate(), dfMiddle, p.getStartDate(), middleDate, p.getEndDate());
          if (points.get(0).getStartDate().isBefore(p.getStartDate())) {
            // the first point is before the swap start date, need to discount that
            var dfFirstDate = bootstrapMap.get(p.getStartDate());
            dfValue = dfValue.multiply(dfFirstDate);
          }
        } else {
          dfValue = dfSwap(swapBootstrapValue, p.getRate(), lastPoint.getEndDate(), p.getEndDate());
        }
        swapBootstrapValue = dfValue;
      } else {
        throw new IllegalArgumentException("Unsupported rate type: " + p);
      }
      bootstrapMap.put(p.getEndDate(), dfValue);
      lastPoint = p;
      dfMap.put(p, dfValue);
    }
    return dfMap;
  }

}
