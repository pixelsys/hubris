package is.equinox.hubris.model

import org.scalatest.FunSpec
import java.time.LocalDate
import is.equinox.time.SemiAnnualCouponFrequency
import is.equinox.time.ModifiedFollowing
import is.equinox.time.`30/360`
import is.equinox.math._

class TradeSpec extends FunSpec {
  
  describe("FixedRateBond") {
    it("should be able to generate its coupons") {
      val frn = new FixedRateBond {
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
      val coupons = frn.coupons
      implicit val precision = Precision(0.0001)
      val expected = List(6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6200.0, 6268.88888, 6200.0, 6131.11111)
      expected.foreach { x => {
        val coupon = coupons.next 
        assert(x ~== coupon._4)
      }}
    }
  }
  
}