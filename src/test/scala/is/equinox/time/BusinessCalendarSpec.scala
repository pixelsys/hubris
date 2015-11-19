package is.equinox.time

import org.scalatest.FunSuite
import org.scalatest.FunSpec
import java.time.LocalDate
import java.time.Month

class BusinessCalendarSpec extends FunSpec {
  
  describe("BusinessDayConvention") {
    describe("NoAdjustment") {
      it("should return the same date which was passed in") {
        val dayCon = NoAdjustment
        assert(LocalDate.of(2015, 11, 19) == dayCon.businessDate(LocalDate.of(2015, 11, 19)))
      }
    }
    describe("ModifiedFollowing") {
      val dayCon = ModifiedFollowing
      it("should return the same date if weekday") {
        assert(LocalDate.of(2015, 11, 19) == dayCon.businessDate(LocalDate.of(2015, 11, 19)))
        assert(LocalDate.of(2008, 2, 28) == dayCon.businessDate(LocalDate.of(2008, 2, 28)))
        assert(LocalDate.of(2008, 8, 28) == dayCon.businessDate(LocalDate.of(2008, 8, 28)))
      }
      it("should return the next business day when not month end") {
        assert(LocalDate.of(2009, 2, 27) == dayCon.businessDate(LocalDate.of(2009, 2, 28)))
        assert(LocalDate.of(2010, 8, 30) == dayCon.businessDate(LocalDate.of(2010, 8, 28)))
      }
      it("should return the previous business day when month end") {
        assert(LocalDate.of(2010, 2, 26) == dayCon.businessDate(LocalDate.of(2010, 2, 28)))
      }
      it("should take holidays into account") {
        // TODO implement this
      }
    }
  }
  
  describe("Coupon Schedule"){
    it("should return a list of dates between start date and end date in steps by frequency taking day convention to account") {
      val startDate = LocalDate.of(2011, Month.NOVEMBER, 14)
      val endDate = LocalDate.of(2016, Month.NOVEMBER, 14)
      val expectedDates = List(LocalDate.of(2011, 11, 14), LocalDate.of(2012, 5, 14), LocalDate.of(2012, 11, 14), 
          LocalDate.of(2013, 5, 14), LocalDate.of(2013, 11, 14), LocalDate.of(2014, 5, 14), LocalDate.of(2014, 11, 14), 
          LocalDate.of(2015, 5, 14), LocalDate.of(2015, 11, 16), LocalDate.of(2016, 5, 16))
      val actualDates = BusinessCalendar.couponStartSchedule(startDate, endDate, SemiAnnualCouponFrequency, ModifiedFollowing).toList
      assert(expectedDates == actualDates)
    }
  }
  
}