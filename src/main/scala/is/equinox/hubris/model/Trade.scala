package is.equinox.hubris.model

import java.time.LocalDate
import is.equinox.time.CouponFrequency
import is.equinox.time.BusinessDayConvention
import is.equinox.time.BusinessCalendar
import is.equinox.time.DayCountConvention
import java.util.concurrent.atomic.AtomicReference

trait Instrument {
  
  val id : String
  val effectiveDate : LocalDate
  def value(cob: LocalDate) : Double
  
}

case class Coupon(startDate: LocalDate, endDate: LocalDate, factor: Double, amount: Double)

trait Bond extends Instrument {
  
  val notional : Double
  val couponFrequency : CouponFrequency
  val dayCon : BusinessDayConvention
  val dayCountCon : DayCountConvention
  def coupons : Iterator[Coupon]
  
}

trait FixedRateBond extends Bond {
  
  val rate : Double
  val maturity : LocalDate
  
  /** Creates coupons based on the economics of the trade
   * 
   * @return Returns an Iterator in which every item is a coupon as a tuple (Start Date, End Date, Factor, Amount)
   */
  override def coupons = {
    val couponSchedule = BusinessCalendar.couponSchedule(effectiveDate, maturity, couponFrequency, dayCon, dayCountCon)
    couponSchedule.map{x => Coupon(x._1, x._2, x._3, rate * notional * x._3)}
  }
  
}

trait FloatingRateNote extends Bond {
  
  val yieldCurve : YieldCurve
  val maturity : LocalDate
  def rate(cob: LocalDate) : Double
  override def coupons : Iterator[Coupon] = {
    val couponSchedule = BusinessCalendar.couponSchedule(effectiveDate, maturity, couponFrequency, dayCon, dayCountCon).toList
    //Console.println(couponSchedule)
    //Console.println("+ " + couponSchedule.head._1)
    val df1 = new AtomicReference[Double](yieldCurve.discountFactor(couponSchedule.head._1))
    //Console.println("initial df: " + df1)
    val coupons = couponSchedule.map{x => {
      val df2 = yieldCurve.discountFactor(x._2)
      val df = df2 / df1.get()
      val rate = (1 - df) / (df * x._3)
      df1.set(df2) // store for the next coupon
      //Console.println("Coupon date " + x._2 + " + | T= " + x._3 + " | df1= " + df1.get + " | df2= " + df2 + " |  R= " + rate)
      Coupon(x._1, x._2, x._3, rate * notional * x._3)
    }}
    //Console.println(coupons)
    coupons.iterator
  }
  
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
  
  
  
