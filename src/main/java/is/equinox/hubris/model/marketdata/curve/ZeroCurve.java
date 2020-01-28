package is.equinox.hubris.model.marketdata.curve;

import is.equinox.core.utils.numbers.CubicInterpolator;
import is.equinox.core.utils.numbers.CurveInterpolator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.stream.Collectors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public class ZeroCurve extends YieldCurve {

  private final CurveInterpolator interpolator = new CubicInterpolator();

  @Override
  BigDecimal interpolate(LocalDate date) {
    var dfMap = discountFactors.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().getEndDate(), e -> e.getValue()));
    return interpolator.interpolateBD(dfMap, date);
  }

}
