package is.equinox.time

import java.time.LocalDate
import java.time.DayOfWeek
import java.time.temporal.TemporalAmount
import java.time.Duration
import java.time.Period
import java.time.temporal.ChronoUnit

sealed trait CouponFrequency { 
  def offset : Period 
}
case object ZeroCouponFrequency extends CouponFrequency {
  def offset = Period.ZERO
}
case object SemiAnnualCouponFrequency extends CouponFrequency {
  def offset = Period.ofMonths(6)
}

sealed trait BusinessDayConvention {
  def businessDate(date: LocalDate) : LocalDate  
}

case object NoAdjustment extends BusinessDayConvention {
  def businessDate(date: LocalDate) = date  
}

case object ModifiedFollowing extends BusinessDayConvention {
  
  import BusinessCalendar._
  
  def businessDate(date: LocalDate) = {
    if(isWeekend(date)) {
      val isSaturday = date.getDayOfWeek() == DayOfWeek.SATURDAY
      val dayOffset = 
        if(date.getDayOfMonth() == date.lengthOfMonth()) {
          if(isSaturday) -1 else -2        
        } else {
          if(isSaturday) 2 else 1
        }
      LocalDate.of(date.getYear(), date.getMonth(), date.getDayOfMonth() + dayOffset)
    } else {
      date
    }
  }
  
}

/*
 * TODO implement more variants of BusinessDayConvention
 * Previous
 * Following
 * Modified Previous
 * End of Month No Adjustment
 * End of Month Previous
 * End of Month Following
 * http://www.derivativepricing.com/blogpage.asp?id=19
 */

sealed trait DayCountConvention {  
  def factor(d1: LocalDate, d2: LocalDate) : Double
}
case object `30/360` extends DayCountConvention {
  def factor(d1: LocalDate, d2: LocalDate) = {
    val (md1, md2) = if(31 == d1.getDayOfMonth()) {
      (d1.minusDays(1), if(31 == d2.getDayOfMonth) { d2.minusDays(1) } else { d2 })
    } else {
      (d1, d2)
    }
    (360 * (md2.getYear() - md1.getYear()) + 30 * (md2.getMonthValue() - md1.getMonthValue()) + (md2.getDayOfMonth() - md1.getDayOfMonth())) / 360.0
  }
}
case object `Actual/360` extends DayCountConvention {
  def factor(d1: LocalDate, d2: LocalDate) = { 
    ChronoUnit.DAYS.between(d1, d2) / 360.0
  }
}

object BusinessCalendar {
  
  def isWeekend(date: LocalDate) = date.getDayOfWeek().getValue() > 5
  
  def couponStartSchedule(start: LocalDate, end: LocalDate, frequency: CouponFrequency, dayCon: BusinessDayConvention) : Iterator[LocalDate] = {
    val p = frequency.offset
    Iterator.iterate(start){_.plus(p)}.takeWhile{x => x.isBefore(end)}.map{x => dayCon.businessDate(x)}
  }
  
  def couponSchedule(start: LocalDate, end: LocalDate, frequency: CouponFrequency, dayCon: BusinessDayConvention, dayCountCon: DayCountConvention) : Iterator[(LocalDate, LocalDate, Double)] = {
    val p = frequency.offset
    Iterator.iterate(start){_.plus(p)}.takeWhile{x => x.isBefore(end)}.map{x => {
        val d1 = dayCon.businessDate(x)
        val d2 = dayCon.businessDate(x.plus(p))
        (d1, d2, dayCountCon.factor(d1, d2))
      }
    }
  }
  
}