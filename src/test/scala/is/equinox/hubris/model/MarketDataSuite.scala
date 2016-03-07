package is.equinox.hubris.model

import org.scalatest.FunSuite
import is.equinox.util.SimpleCsvParser
import is.equinox.time.ModifiedFollowing
import is.equinox.time.`Actual/360`
import java.time.LocalDate
import is.equinox.math.LinearInterpolator
import is.equinox.math.SplineInterpolator

class MarketDataSuite extends FunSuite {  
  
  test("Rates should tell the days between start and end date") {
    // given
    val dates = List((LocalDate.of(2011, 11, 10), LocalDate.of(2011, 11, 11), 1),
        (LocalDate.of(2011, 11, 11), LocalDate.of(2011, 11, 14), 3),
        (LocalDate.of(2011, 11, 14), LocalDate.of(2011, 11, 21), 7),
        (LocalDate.of(2011, 11, 14), LocalDate.of(2011, 12, 14), 30),
        (LocalDate.of(2011, 11, 14), LocalDate.of(2012,  1, 16), 63),
        (LocalDate.of(2011, 11, 14), LocalDate.of(2012, 10, 15), 336))
    
     // when & then
     dates.foreach{d => {
       val rate = new MoneyMarketRate("", d._1, d._2, 0.0)
       assert(d._3 == rate.days)
     }}
  }
 
  def createYieldCurve : YieldCurve = {
    val csvIs =  getClass.getResourceAsStream("/mktdata/usdlibor_2011-11-10.csv")
    val csv = scala.io.Source.fromInputStream(csvIs).mkString
    val cob = LocalDate.of(2011, 11, 10)
    implicit val csvParser = SimpleCsvParser.fromString(_)
    implicit val interpolator = SplineInterpolator
    YieldCurve.loadFromCsv("usdlibor", cob, csv, ModifiedFollowing, `Actual/360`)    
  }
  
  test("Yield Curve can be constructed from data in CSV file") {
    val curve = createYieldCurve
    assert(null != curve)
    curve.points.foreach{p => Console.println(p + " => " + curve.discountFactors(p))}
    // bootstrapping from  par swap rates
    // http://docs.fincad.com/support/developerFunc/mathref/DFCurves.htm
    // page 41 - http://www.mathematik.uni-muenchen.de/~filipo/ZINSMODELLE/zinsmodelle1.pdf
  }
  
  test("Yield Curve returns interpolated values for points not defined by the market data") {
    val curve = createYieldCurve
    Console.println(curve.discountFactor(LocalDate.of(2012, 11, 14)) + " == 0.994211")
    Console.println(curve.discountFactor(LocalDate.of(2013, 5, 14))  + " == 0.991188")
    Console.println(curve.discountFactor(LocalDate.of(2013, 11, 14)) + " == 0.988074")
    Console.println(curve.discountFactor(LocalDate.of(2017, 5, 15))  + " == 0.927182")
    Console.println(curve.discountFactor(LocalDate.of(2017, 11, 14))  + " == 0.913869")
    //Console.println(dfIntp)
  }
  
}