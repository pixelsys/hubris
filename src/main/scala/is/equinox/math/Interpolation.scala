package is.equinox.math

import java.time.LocalDate
import java.time.temporal.ChronoUnit

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
  
  override def interpolate(points : Map[LocalDate, Double], date: LocalDate) = ???
  
}

class CurveInterpolationContext(points : Map[LocalDate, Double], interpolator: CurveInterpolator) {
  
  def interpolate(date: LocalDate) : Double = {
    interpolator.interpolate(points, date)
  }
  
}

