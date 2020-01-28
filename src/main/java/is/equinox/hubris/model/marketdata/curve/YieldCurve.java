package is.equinox.hubris.model.marketdata.curve;

import is.equinox.hubris.model.marketdata.Rate;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class YieldCurve {

  protected String name;
  protected List<Rate> points;
  protected Map<Rate, BigDecimal> discountFactors;

  abstract BigDecimal interpolate(LocalDate date);

  public BigDecimal discountFactor(LocalDate date) {
    var dfPoints = discountFactors.entrySet().stream()
        .collect(Collectors.toMap(e -> e.getKey().getEndDate(), e -> e.getValue()));
    return dfPoints.getOrDefault(date, interpolate(date));
  }

  public String getName() { return name; }

  public List<Rate> points() {
    return points;
  }

  public BigDecimal getDiscountFactor(Rate rate) {
    return discountFactors.get(rate);
  }

}
