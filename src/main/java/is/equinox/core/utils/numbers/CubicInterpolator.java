package is.equinox.core.utils.numbers;

import breeze.linalg.DenseVector;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.TreeMap;
import java.util.stream.Collectors;
import scala.Tuple2;
import scala.collection.JavaConverters;
import scala.collection.immutable.Map;
import scala.collection.immutable.Map$;
import scala.collection.immutable.Seq;


public class CubicInterpolator implements CurveInterpolator {

  @Override
  public Double interpolate(List<Double> xCoords, List<Double> yCoords, Double x) {
    var xVector = new DenseVector(xCoords.toArray(Double[]::new));
    System.out.println("X vector: " + xVector);
    var yVector = new DenseVector(yCoords.toArray(Double[]::new));
    System.out.println("y vector: " + yVector);
    var interpolator = new breeze.interpolation.CubicInterpolator(xVector, yVector);
    //var interpolator = new breeze.interpolation.LinearInterpolator(xVector, yVector);
    var value = interpolator.interpolate(x);
    System.out.println("value when x=" + x + " is " + value);
    return (Double)value;
  }

  public static <K, V> scala.collection.immutable.Map<K, Object> toScalaImmutableMap(java.util.Map<K, V> jmap) {
    List<Tuple2<K, V>> tuples = jmap.entrySet()
        .stream()
        .map(e -> Tuple2.apply(e.getKey(), e.getValue()))
        .collect(Collectors.toList());

    Seq<Tuple2<K, V>> scalaSeq = JavaConverters.asScalaBuffer(tuples).toSeq();

    return (Map<K, Object>) Map$.MODULE$.apply(scalaSeq);
  }

}
