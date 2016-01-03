package is.equinox.hubris.model

import org.scalatest.FunSuite
import is.equinox.util.SimpleCsvParser
import is.equinox.time.ModifiedFollowing
import is.equinox.time.`30/360`
import java.time.LocalDate
import is.equinox.math.LinearInterpolator

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
  
  test("Yield Curve can be constructed from data in CSV file") {
    val csvIs =  getClass.getResourceAsStream("/mktdata/usdlibor_2011-11-10.csv")
    val csv = scala.io.Source.fromInputStream(csvIs).mkString
    val cob = LocalDate.of(2011, 11, 10)
    implicit val csvParser = SimpleCsvParser.fromString(_)
    implicit val interpolator = LinearInterpolator
    val curve = YieldCurve.loadFromCsv("usdlibor", cob, csv, ModifiedFollowing, `30/360`)
    assert(null != curve)
    curve.points.foreach{p => Console.println(p + " => " + curve.discountFactors(p))}
    // bootstrapping from  par swap rates
    // http://docs.fincad.com/support/developerFunc/mathref/DFCurves.htm
    // page 41 - http://www.mathematik.uni-muenchen.de/~filipo/ZINSMODELLE/zinsmodelle1.pdf
  }
  
}