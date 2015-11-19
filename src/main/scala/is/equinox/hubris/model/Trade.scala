package is.equinox.hubris.model

import java.util.Date
import is.equinox.time.CouponFrequency
import is.equinox.time.BusinessDayConvention

trait Instrument {
  
  val id : String
  val effectiveDate : Date
  def value(cob: Date) : Double
  
}

trait Bond extends Instrument {
  
  val notional : Double
  val couponFrequency : CouponFrequency
  val dayCount : BusinessDayConvention
  
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
  
  
  
