package is.equinox.hubris.model

import org.scalatest.FunSpec
import java.time.LocalDate
import is.equinox.time.SemiAnnualCouponFrequency
import is.equinox.time.ModifiedFollowing
import is.equinox.time.`30/360`
import is.equinox.math._
import is.equinox.time.`Actual/360`
import is.equinox.util.SimpleCsvParser
import scala.collection.immutable.TreeMap

class TradeSpec extends FunSpec {
  
  def createFixedRateBond : FixedRateBond = {
      new FixedRateBond {
          lazy val id = ???
          def value(cob: LocalDate) = ???
          val effectiveDate = LocalDate.of(2011, 11, 14)
          val notional = 1000000.0
          val couponFrequency = SemiAnnualCouponFrequency
          val dayCon = ModifiedFollowing
          val dayCountCon = `30/360`
          val rate = 0.0124
          val maturity = LocalDate.of(2016, 11, 14)          
      }
  } 
  
  describe("FixedRateBond") {
    it("should be able to generate its coupons") {
      val frn = createFixedRateBond
      val coupons = frn.coupons
      implicit val precision = Precision(0.0001)
      val expected = List(6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6268.88888, 6200.0, 6131.11111)
      expected.foreach { x => {
        val coupon = coupons.next 
        assert(x ~== coupon.amount)
      }}
    }
  }
  
  def createYieldCurve : ZeroCurve = {
    val today = LocalDate.of(2011, 11, 10)
      val csvIs =  getClass.getResourceAsStream("/mktdata/usdlibor_2011-11-10.csv")
      val csv = scala.io.Source.fromInputStream(csvIs).mkString
      implicit val csvParser = SimpleCsvParser.fromString(_)
      implicit val interpolator = LinearInterpolator
      YieldCurve.loadFromCsv("usdlibor", today, csv, ModifiedFollowing, `30/360`)    
  }
  
  def createFloatingRateNote(yc: YieldCurve) : FloatingRateNote = {
    new FloatingRateNote {
      lazy val id = ???
      def value(cob: LocalDate) = ???
      val effectiveDate = LocalDate.of(2011, 11, 14)
      val notional = 1000000.0
      val couponFrequency = SemiAnnualCouponFrequency
      val dayCon = ModifiedFollowing
      val dayCountCon = `Actual/360`
      val yieldCurve = yc
      val maturity = LocalDate.of(2016, 11, 14)
      def rate(cob: LocalDate) = ???
    }    
  }
  
  describe("FloatingRateBond") {
    it("should be able to generate its coupons") {
      val yc =  createYieldCurve
      val floatingRateNote = createFloatingRateNote(yc)
      val coupons = floatingRateNote.coupons
      implicit val precision = Precision(0.0001)
      val expected = List(3306.33, 2492.65, 3049.19, 3152.14, 4498.43, 5131.08, 7807.48, 9216.90, 11294.90, 12708.55)
      expected.foreach { x => {
        val coupon = coupons.next 
        assert(x ~== coupon.amount)
      }}      
    }
  }
  
  describe("interest rate swap") {
    it("should have zero value at booking") {
      val yc =  createYieldCurve
      val fixedLeg = createFixedRateBond
      val floatingLeg = createFloatingRateNote(yc)
      //Console.println(fixedLeg.coupons.toList)
      //Console.println(floatingLeg.coupons.toList)
      val coupons = (fixedLeg.coupons.toList zip floatingLeg.coupons.toList)
                      .groupBy{ case(x, y) => x.endDate }
                      .map{ case(d, l) => d -> {
                        val fixedAmt = l.head._1.amount
                        val floatingAmt = l.head._2.amount
                        val net = floatingAmt - fixedAmt
                        val df = yc.discountFactor(d)
                        val pv = net * df
                        (fixedAmt, floatingAmt, net, df, pv)
                      }}
      implicit def dateTimeOrdering: Ordering[LocalDate] = Ordering.fromLessThan(_ isBefore _)                      
      val sorted = TreeMap(coupons.toArray:_*)
      sorted.foreach{case(d,c) => Console.println(d + ", " + c)}
      val pv = coupons.values.map{x => x._5}.foldRight(0.0)((a,b) => a + b)
      Console.println("PV: " + pv)
    }
  }
  
}