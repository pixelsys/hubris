package is.equinox.hubris.model

import java.util.Date

trait Instrument {
  
  val id : String
  val effectiveDate : Date
  def value(cob: Date) : Double
  
}

object CouponFrequency extends Enumeration {
  type CouponFrequency = Value
  val Zero, Quarterly, SemiAnnual = Value
}

object DayCountConvention extends Enumeration {
  type DayCountConvention = Value
  val `30/360`, `Actual/360` = Value
}

import CouponFrequency._
import DayCountConvention._

trait Bond extends Instrument {
  
  val notional : Double
  val couponFrequency : CouponFrequency
  val dayCount : DayCountConvention
  
}

trait FixedRateBond extends Bond {
  
  val rate : Double
  val maturity : Date
  
}

trait FloatingRateNote extends Bond {
  
  val benchmark : Benchmark
  val maturity : Date
  def rate(cob: Date) : Double
  
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

class IRSwap(val id: String, val notional: BigDecimal, val effectiveDate: Date, fixedLeg: FixedRateSwapLeg, floatingLeg: FloatingRateSwapLeg, side: PayReceive) extends Instrument {
  
  // coupon frequency - quarterly
  // business day convention - modified following
  // 
  
  def value(cob: Date): Double = {
    // http://www.derivativepricing.com/blogpage.asp?id=8
    ???
  }
  
}
  
  
  
