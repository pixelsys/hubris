package is.equinox.core.utils.numbers;

import is.equinox.math.LinearInterpolator;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.stream.Collectors;
import scala.Predef;
import scala.Tuple2;
import scala.collection.JavaConverters;

public interface CurveInterpolator {

  default BigDecimal interpolateBD(Map<LocalDate, BigDecimal> points, LocalDate date) {
    var doubleMap = points.entrySet().stream().collect(Collectors.toMap(e -> e.getKey(), e -> e.getValue().doubleValue()));
    var doubleValue = interpolate(doubleMap, date);
    return new BigDecimal(doubleValue);
  }

  default Double interpolate(Map<LocalDate, Double> points, LocalDate date) {
    /*
    var sortedMap = new TreeMap<>(points);
    var xCoords = sortedMap.keySet().stream().map(d -> ChronoUnit.DAYS.between(sortedMap.firstKey(), d) / 1.0).collect(Collectors.toList());
    var yCoords = sortedMap.values().stream().collect(Collectors.toList());
    var x = ChronoUnit.DAYS.between(sortedMap.firstKey(), date);
    return interpolate(xCoords, yCoords, new Double(x));*/
    var pointsScala = CubicInterpolator.toScalaImmutableMap(points);
    return LinearInterpolator.interpolate(pointsScala, date);
  }



  Double interpolate(List<Double> xCoords, List<Double> yCoords, Double x);

}
