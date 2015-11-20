package is.equinox.time

import org.scalatest.FunSuite
import org.scalatest.FunSpec
import java.time.LocalDate
import java.time.Month
import is.equinox.math._

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
  
  describe("Acrrual and Daycount convention") {
    describe("30/360") {
      it("should calculate the value time as a fraction of a year between two dates") {
        implicit val precision = Precision(0.0001)
        assert(0.5 ~== `30/360`.factor(LocalDate.of(2011, 11, 14), LocalDate.of(2012, 5, 14)))
        assert(0.5 ~== `30/360`.factor(LocalDate.of(2012, 5, 14), LocalDate.of(2012, 11, 14)))
        assert(0.5 ~== `30/360`.factor(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 7, 1)))
        assert(0.505555 ~== `30/360`.factor(LocalDate.of(2015, 5, 14), LocalDate.of(2015, 11, 16)))
        assert(0.494444 ~== `30/360`.factor(LocalDate.of(2016, 5, 16), LocalDate.of(2016, 11, 14)))
      }
    }
    describe("Actual/360") {
      it("should calculate the value time as a fraction of a year between two dates") {
        implicit val precision = Precision(0.0001)
        assert(0.505556 ~== `Actual/360`.factor(LocalDate.of(2011, 11, 14), LocalDate.of(2012, 5, 14)))
        assert(0.511111 ~== `Actual/360`.factor(LocalDate.of(2012, 5, 14), LocalDate.of(2012, 11, 14)))
        assert(0.502778 ~== `Actual/360`.factor(LocalDate.of(2012, 11, 14), LocalDate.of(2013, 5, 14)))
        assert(0.511111 ~== `Actual/360`.factor(LocalDate.of(2013, 5, 14), LocalDate.of(2013, 11, 14)))
        assert(0.502778 ~== `Actual/360`.factor(LocalDate.of(2013, 11, 14), LocalDate.of(2014, 5, 14)))
        assert(0.511111 ~== `Actual/360`.factor(LocalDate.of(2014, 5, 14), LocalDate.of(2014, 11, 14)))
        assert(0.502778 ~== `Actual/360`.factor(LocalDate.of(2014, 11, 14), LocalDate.of(2015, 5, 14)))
        assert(0.516667 ~== `Actual/360`.factor(LocalDate.of(2015, 5, 14), LocalDate.of(2015, 11, 16)))
        assert(0.505556 ~== `Actual/360`.factor(LocalDate.of(2015, 11, 16), LocalDate.of(2016, 5, 16)))
        assert(0.505556 ~== `Actual/360`.factor(LocalDate.of(2016, 5, 16), LocalDate.of(2016, 11, 14)))
      }
    }    
  }
  
  describe("Coupon Start Schedule"){
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
  
  describe("Coupon Schedule") {
    it("should return coupon start date, end date and the factor taking day and day count convention into account") {
      val start = LocalDate.of(2011, Month.NOVEMBER, 14)
      val end = LocalDate.of(2016, Month.NOVEMBER, 14)
      val expected = List(
          (LocalDate.of(2011,11,14),LocalDate.of(2012,5,14),0.5), (LocalDate.of(2012,5,14),LocalDate.of(2012,11,14),0.5),
          (LocalDate.of(2012,11,14),LocalDate.of(2013,5,14),0.5), (LocalDate.of(2013,5,14),LocalDate.of(2013,11,14),0.5), 
          (LocalDate.of(2013,11,14),LocalDate.of(2014,5,14),0.5), (LocalDate.of(2014,5,14),LocalDate.of(2014,11,14),0.5),
          (LocalDate.of(2014,11,14),LocalDate.of(2015,5,14),0.5), (LocalDate.of(2015,5,14),LocalDate.of(2015,11,16),0.505555),
          (LocalDate.of(2015,11,16),LocalDate.of(2016,5,16),0.5), (LocalDate.of(2016,5,16),LocalDate.of(2016,11,14),0.494444))
      val couponSchedule = BusinessCalendar.couponSchedule(start, end, SemiAnnualCouponFrequency, ModifiedFollowing, `30/360`)
      implicit val precision = Precision(0.0001)
      expected.foreach{x => {
        val actual = couponSchedule.next
        assert(x._1 == actual._1) 
        assert(x._2 == actual._2) 
        assert(x._3 ~== actual._3)
      }}
    }
  }
  
}