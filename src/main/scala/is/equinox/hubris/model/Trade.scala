package is.equinox.hubris.model

import java.time.LocalDate
import is.equinox.time.CouponFrequency
import is.equinox.time.BusinessDayConvention
import is.equinox.time.BusinessCalendar
import is.equinox.time.DayCountConvention

trait Instrument {
  
  val id : String
  val effectiveDate : LocalDate
  def value(cob: LocalDate) : Double
  
}

trait Bond extends Instrument {
  
  val notional : Double
  val couponFrequency : CouponFrequency
  val dayCon : BusinessDayConvention
  val dayCountCon : DayCountConvention
  
}

trait FixedRateBond extends Bond {
  
  val rate : Double
  val maturity : LocalDate
  
  /** Creates coupons based on the economics of the trade
   * 
   * @return Returns an Iterator in which every item is a coupon as a tuple (Start Date, End Date, Factor, Amount)
   */
  def coupons : Iterator[(LocalDate, LocalDate, Double, Double)] = {
    val couponSchedule = BusinessCalendar.couponSchedule(effectiveDate, maturity, couponFrequency, dayCon, dayCountCon)
    couponSchedule.map{x => (x._1, x._2, x._3, rate * notional * x._3)}
  }
  
}

trait FloatingRateNote extends Bond {
  
  val yieldcurve : YieldCurve
  val maturity : LocalDate
  def rate(cob: LocalDate) : Double
  
}

trait Counterparty {
  
  val counterparty : String
  
}

trait FixedRateSwapLeg extends FixedRateBond with Counterparty

trait FloatingRateSwapLeg extends FloatingRateNote with Counterparty {
  
  val fixedRate : Double
  
}

object PayReceive extends Enumeration {
  type PayReceive = Value
  val Pay, Receive = Value  
}

import PayReceive._

class IRSwap(val id: String, val notional: BigDecimal, val effectiveDate: LocalDate, fixedLeg: FixedRateSwapLeg, floatingLeg: FloatingRateSwapLeg, side: PayReceive) extends Instrument {
  
  // coupon frequency - quarterly
  // business day convention - modified following
  // 
  
  def value(cob: LocalDate): Double = {
    // http://www.derivativepricing.com/blogpage.asp?id=8
    ???
  }
  
}
  
  
  
