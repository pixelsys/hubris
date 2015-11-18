package is.equinox.time

import java.time.LocalDate

object CouponFrequency extends Enumeration {
  type CouponFrequency = Value
  val Zero, Quarterly, SemiAnnual = Value
}

trait BusinessDayConvention {
  
  def businessDate(date: LocalDate) : LocalDate
  
}

class NoAdjustment extends BusinessDayConvention {
  
  override def businessDate(date: LocalDate) = date
  
}

import CouponFrequency._

object BusinessCalendar {
  
  def couponSchedule(endDate: LocalDate, frequency: CouponFrequency, dayCon: BusinessDayConvention) : List[LocalDate] = {
    ???
  }
  
}