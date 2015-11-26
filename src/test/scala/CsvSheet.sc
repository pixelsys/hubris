import is.equinox.util.SimpleCsvParser
import java.time.LocalDate
import is.equinox.time.BusinessCalendar
import is.equinox.time.ModifiedFollowing
import is.equinox.hubris.model._
import is.equinox.time.`30/360`
import is.equinox.time._

object CsvSheet {
 
    val csvIs =  getClass.getResourceAsStream("/mktdata/usdlibor_2011-11-10.csv")
                                                  //> csvIs  : java.io.InputStream = java.io.BufferedInputStream@a09ee92
    val csv = scala.io.Source.fromInputStream(csvIs).mkString
                                                  //> csv  : String = ""Type","Term","Rate"
                                                  //| "Cash","ON","0.001410"
                                                  //| "Cash","T/N","0.001410"
                                                  //| "Cash","1W","0.001910"
                                                  //| "Cash","2W","0.002090"
                                                  //| "Cash","1M","0.002490"
                                                  //| "Cash","2M","0.003450"
                                                  //| "Cash","3M","0.004570"
                                                  //| "Cash","4M","0.005230"
                                                  //| "Cash","5M","0.005860"
                                                  //| "Cash","6M","0.006540"
                                                  //| "Cash","7M","0.007080"
                                                  //| "Cash","8M","0.007540"
                                                  //| "Cash","9M","0.008080"
                                                  //| "Cash","10M","0.008570"
                                                  //| "Cash","11M","0.009130"
                                                  //| "
    val cob = LocalDate.of(2011, 11, 10)          //> cob  : java.time.LocalDate = 2011-11-10
    implicit val csvParser = SimpleCsvParser.fromString(_)
                                                  //> csvParser  : String => scala.collection.immutable.Vector[scala.collection.im
                                                  //| mutable.Vector[String]] = <function1>
    val curve = YieldCurve.loadFromCsv("usdlibor", cob, csv, ModifiedFollowing)
                                                  //> curve  : is.equinox.hubris.model.ZeroCurve = is.equinox.hubris.model.ZeroCur
                                                  //| ve@548a9f61
 val p = curve.points(0)                          //> p  : is.equinox.hubris.model.Rate = MoneyMarketRate(ON,2011-11-10,2011-11-11
                                                  //| ,0.00141)
     val dayCountCon = `30/360`                   //> dayCountCon  : is.equinox.time.30/360.type = 30/360
  
  /*
  val df = 1 / (1 + p.rate * dayCountCon.factor(p.startDate, p.endDate))
                                                  
  val pTn = curve.points(1)
  val dfTn = (1 / (1 + pTn.rate * dayCountCon.factor(pTn.startDate, pTn.endDate))) * df
    */
   val map = new scala.collection.mutable.HashMap[LocalDate, Double]
                                                  //> map  : scala.collection.mutable.HashMap[java.time.LocalDate,Double] = Map()
                                                  
  def df(bootstrap: Double, rate: Double, start: LocalDate, end: LocalDate, dayCountCon: DayCountConvention) : Double = {
     bootstrap / (1 + rate * dayCountCon.factor(start, end))
  }                                               //> df: (bootstrap: Double, rate: Double, start: java.time.LocalDate, end: java
                                                  //| .time.LocalDate, dayCountCon: is.equinox.time.DayCountConvention)Double
                                                  
   curve.points.map{p => {
     val bootstrap = if(p.startDate == cob) { 1 } else { map(p.startDate) }
     val discF = df(bootstrap, p.rate, p.startDate, p.endDate, dayCountCon)
     map.getOrElseUpdate(p.endDate, discF)
     p -> discF
   }}                                             //> res0: List[(is.equinox.hubris.model.Rate, Double)] = List((MoneyMarketRate(
                                                  //| ON,2011-11-10,2011-11-11,0.00141),0.9999960833486734), (MoneyMarketRate(T/N
                                                  //| ,2011-11-11,2011-11-14,0.00141),0.9999843335327543), (MoneyMarketRate(1W,20
                                                  //| 11-11-14,2011-11-21,0.00191),0.9999471966049248), (MoneyMarketRate(2W,2011-
                                                  //| 11-14,2011-11-28,0.00209),0.9999030636337488), (MoneyMarketRate(1M,2011-11-
                                                  //| 14,2011-12-14,0.00249),0.9997768798301896), (MoneyMarketRate(2M,2011-11-14,
                                                  //| 2012-01-16,0.00345),0.999390528993444), (MoneyMarketRate(3M,2011-11-14,2012
                                                  //| -02-14,0.00457),0.9988431552279063), (MoneyMarketRate(4M,2011-11-14,2012-03
                                                  //| -14,0.00523),0.9982440613857384), (MoneyMarketRate(5M,2011-11-14,2012-04-16
                                                  //| ,0.00586),0.9975162566435388), (MoneyMarketRate(6M,2011-11-14,2012-05-14,0.
                                                  //| 00654),0.9967250426433105), (MoneyMarketRate(7M,2011-11-14,2012-06-14,0.007
                                                  //| 08),0.9958713847138859), (MoneyMarketRate(8M,2011-11-14,2012-07-16,0.00754)
                                                  //| ,0.9949414176827062), (
                                                  //| Output exceeds cutoff limit.
                                                  
}