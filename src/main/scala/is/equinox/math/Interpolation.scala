package is.equinox.math

import java.time.LocalDate
import java.time.temporal.ChronoUnit
import scala.collection.immutable.TreeMap
import breeze.linalg.DenseVector
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator

sealed trait CurveInterpolator {

  def interpolate(points : Map[LocalDate, Double], date: LocalDate) : Double
  
}

object LinearInterpolator extends CurveInterpolator {
  
  implicit def dateTimeOrdering: Ordering[LocalDate] = Ordering.fromLessThan(_ isBefore _)
  
  override def interpolate(points : Map[LocalDate, Double], date: LocalDate) = {
    if(points.contains(date)) {
      points(date)
    } else {
      val (beforeList, afterList) = points.keys.toSeq.sorted.partition { x => x.isBefore(date) }
      val beforeDate = beforeList.last
      val afterDate = afterList.head
      val intervalLeft = ChronoUnit.DAYS.between(beforeDate, date).toDouble
      val intervalRight = ChronoUnit.DAYS.between(date, afterDate).toDouble
      //Console.println("Interpolation gecco beforeList " + beforeList)
      //Console.println("Interpolation gecco afterList " + afterList)
      //Console.println("Interpolation gecco beforeDate " + beforeDate + " afterDate " + afterDate)
      //Console.println("Interpolation gecco bal " + intervalLeft + " jobb " + intervalRight + " arany " + (intervalLeft / (intervalLeft + intervalRight)))
      val pointPct = intervalLeft / (intervalLeft + intervalRight)
      val leftValue = points(beforeDate)
      val rightValue = points(afterDate)
      leftValue + (pointPct * (rightValue - leftValue))
    }
  }
  
}

object SplineInterpolator extends CurveInterpolator {
  
  import breeze.interpolation._
  
  implicit def dateTimeOrdering: Ordering[LocalDate] = Ordering.fromLessThan(_ isBefore _)
  
  override def interpolate(points : Map[LocalDate, Double], date: LocalDate) = {                          
    val sorted = TreeMap(points.toArray:_*)
    Console.println(sorted)    
    val daysBetween = ChronoUnit.DAYS.between(sorted.firstKey, sorted.lastKey).toDouble
    //Console.println(daysBetween)
    val xCoords = sorted.keys.map{d => {
      //Console.println("Days between: " + ChronoUnit.DAYS.between(sorted.firstKey, d))
      //Console.println("Value: " + (ChronoUnit.DAYS.between(sorted.firstKey, d) / daysBetween))
      ChronoUnit.DAYS.between(sorted.firstKey, d).toDouble
    }}.toArray.sorted
    //Console.println("X Coords: " + xCoords.sorted)
    val cubicInt = CubicInterpolator(DenseVector(xCoords), DenseVector(sorted.values.toArray))
    val value = ChronoUnit.DAYS.between(sorted.firstKey, date) 
    val x = DenseVector(xCoords)
    val y = DenseVector(sorted.values.toArray)
    (0 to x.length - 1).map{i => Console.println("[" + x(i) + ";" + y(i) + "]")}
    Console.println("Returning interpolated value: " + cubicInt(value) + " for date: " + date + ", value: " + value)
    val interpolator = new SplineInterpolator()
    val function = interpolator.interpolate(xCoords, sorted.values.toArray);
    Console.println("Commons3 interpolated value: " + function.value(value) + " for date: " + date + ", value: " + value)
    ???
    cubicInt(value)
  }
  
}

class CurveInterpolationContext(points : Map[LocalDate, Double], interpolator: CurveInterpolator) {
  
  def interpolate(date: LocalDate) : Double = {
    interpolator.interpolate(points, date)
  }
  
}

