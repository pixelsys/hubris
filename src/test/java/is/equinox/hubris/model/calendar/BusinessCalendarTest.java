package is.equinox.hubris.model.calendar;

import static is.equinox.core.test.NumbersEquals.assertEqualsBD;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import is.equinox.core.utils.numbers.BigDecimalCompare;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import org.junit.Test;
import scala.Tuple3;

public class BusinessCalendarTest {

  @Test
  public void NoAdjustment_shouldReturnSameDate() {
    var dayCon = new NoAdjustment();
    assertEquals(LocalDate.of(2015, 11, 19), dayCon.businessDate(LocalDate.of(2015, 11, 19)));
  }

  @Test
  public void ModifiedFollowing_shouldReturnSameIfWeekday() {
    var dayCon = new ModifiedFollowing();
    assertEquals(LocalDate.of(2015, 11, 19), dayCon.businessDate(LocalDate.of(2015, 11, 19)));
    assertEquals(LocalDate.of(2008, 2, 28), dayCon.businessDate(LocalDate.of(2008, 2, 28)));
    assertEquals(LocalDate.of(2008, 8, 28), dayCon.businessDate(LocalDate.of(2008, 8, 28)));
  }

  @Test
  public void ModifiedFollowing_shouldReturnNextBusinessDayWhenNotMonthEnd() {
    var dayCon = new ModifiedFollowing();
    assertEquals(LocalDate.of(2009, 2, 27), dayCon.businessDate(LocalDate.of(2009, 2, 28)));
    assertEquals(LocalDate.of(2010, 8, 30), dayCon.businessDate(LocalDate.of(2010, 8, 28)));
  }

  @Test
  public void ModifiedFollowing_shouldReturnPreviousBusinessDayWhenMonthEnd() {
    var dayCon = new ModifiedFollowing();
    assertEquals(LocalDate.of(2010, 2, 26), dayCon.businessDate(LocalDate.of(2010, 2, 28)));
  }

  @Test
  public void  ModifiedFollowing_shouldTakeHolidaysIntoAccount() {
    // TODO implement this
  }

  @Test
  public void DayCountConvention_30360_shouldCalculateTheTimeAsFractionOfAYear() {
    var dcc = new DayCountConvention30360();
    assertEqualsBD(0.5, dcc.factor(LocalDate.of(2011, 11, 14), LocalDate.of(2012, 5, 14)));
    assertEqualsBD(0.5, dcc.factor(LocalDate.of(2012, 5, 14), LocalDate.of(2012, 11, 14)));
    assertEqualsBD(0.5, dcc.factor(LocalDate.of(2015, 1, 1), LocalDate.of(2015, 7, 1)));
    assertEqualsBD(0.5055555, dcc.factor(LocalDate.of(2015, 5, 14), LocalDate.of(2015, 11, 16)));
    assertEqualsBD(0.4944444, dcc.factor(LocalDate.of(2016, 5, 16), LocalDate.of(2016, 11, 14)));
  }

  @Test
  public void DayCountConvention_Actual360_shouldCalculateTheTimeAsFractionOfAYear() {
    var dcc = new DayCountConventionActual360();
    assertEqualsBD(0.505556, dcc.factor(LocalDate.of(2011, 11, 14), LocalDate.of(2012, 5, 14)));
    assertEqualsBD(0.511111, dcc.factor(LocalDate.of(2012, 5, 14), LocalDate.of(2012, 11, 14)));
    assertEqualsBD(0.502778, dcc.factor(LocalDate.of(2012, 11, 14), LocalDate.of(2013, 5, 14)));
    assertEqualsBD(0.511111, dcc.factor(LocalDate.of(2013, 5, 14), LocalDate.of(2013, 11, 14)));
    assertEqualsBD(0.502778, dcc.factor(LocalDate.of(2013, 11, 14), LocalDate.of(2014, 5, 14)));
    assertEqualsBD(0.511111, dcc.factor(LocalDate.of(2014, 5, 14), LocalDate.of(2014, 11, 14)));
    assertEqualsBD(0.502778, dcc.factor(LocalDate.of(2014, 11, 14), LocalDate.of(2015, 5, 14)));
    assertEqualsBD(0.516667, dcc.factor(LocalDate.of(2015, 5, 14), LocalDate.of(2015, 11, 16)));
    assertEqualsBD(0.505556, dcc.factor(LocalDate.of(2015, 11, 16), LocalDate.of(2016, 5, 16)));
    assertEqualsBD(0.505556, dcc.factor(LocalDate.of(2016, 5, 16), LocalDate.of(2016, 11, 14)));
  }

  @Test
  public void CouponStartSchedule_shouldReturnAListOfCouponDates() {
    var startDate = LocalDate.of(2011, Month.NOVEMBER, 14);
    var endDate = LocalDate.of(2016, Month.NOVEMBER, 14);
    var expectedDates = List.of(LocalDate.of(2011, 11, 14), LocalDate.of(2012, 5, 14), LocalDate.of(2012, 11, 14),
        LocalDate.of(2013, 5, 14), LocalDate.of(2013, 11, 14), LocalDate.of(2014, 5, 14), LocalDate.of(2014, 11, 14),
        LocalDate.of(2015, 5, 14), LocalDate.of(2015, 11, 16), LocalDate.of(2016, 5, 16));
    var actualDates = BusinessCalendar.couponStartSchedule(startDate, endDate, new SemiAnnualCouponFrequency(), new ModifiedFollowing());
    assertEquals("Coupon Start Schedule should return a list of dates between start date and end date in steps by frequency taking day convention to account",
        expectedDates, actualDates);
  }

  @Test
  public void CouponSchedule_shouldReturnCouponDatesAndFactors() {
    var start = LocalDate.of(2011, Month.NOVEMBER, 14);
    var end = LocalDate.of(2016, Month.NOVEMBER, 14);
    var expected = List.of(
        new Tuple3<>(LocalDate.of(2011,11,14), LocalDate.of(2012,5,14), 0.5),
        new Tuple3<>(LocalDate.of(2012,5,14), LocalDate.of(2012,11,14), 0.5),
        new Tuple3<>(LocalDate.of(2012,11,14), LocalDate.of(2013,5,14), 0.5),
        new Tuple3<>(LocalDate.of(2013,5,14), LocalDate.of(2013,11,14), 0.5),
        new Tuple3<>(LocalDate.of(2013,11,14), LocalDate.of(2014,5,14), 0.5),
        new Tuple3<>(LocalDate.of(2014,5,14), LocalDate.of(2014,11,14), 0.5),
        new Tuple3<>(LocalDate.of(2014,11,14), LocalDate.of(2015,5,14), 0.5),
        new Tuple3<>(LocalDate.of(2015,5,14), LocalDate.of(2015,11,16), 0.5055555),
        new Tuple3<>(LocalDate.of(2015,11,16), LocalDate.of(2016,5,16), 0.5),
        new Tuple3<>(LocalDate.of(2016,5,16), LocalDate.of(2016,11,14), 0.4944444));
    var couponSchedule = BusinessCalendar.couponSchedule(start, end, new SemiAnnualCouponFrequency(), new ModifiedFollowing(), new DayCountConvention30360());
    var csi = couponSchedule.iterator();
    expected.stream().forEach(x -> {
      var actual = csi.next();
      assertEquals(x._1(), actual._1());
      assertEquals(x._2(), actual._2());
      assertEqualsBD(x._3(), actual._3());
    });

  }



}
