package is.equinox.hubris.model

import java.time.LocalDate
import java.io.File
import is.equinox.util.SimpleCsvParser
import collection.immutable.Vector
import is.equinox.hubris.model.marketdata._
import is.equinox.time.BusinessDayConvention
import java.time.Period
import java.time.temporal.ChronoUnit

trait MarketData

sealed trait Rate extends MarketData {
  val startDate : LocalDate
  val endDate : LocalDate
  val rate : Double
  def days = ChronoUnit.DAYS.between(startDate, endDate)  
}
case class MoneyMarketRate(tenor: String, startDate: LocalDate, endDate: LocalDate, rate: Double) extends Rate
case class FRARate(contract: String, startDate: LocalDate, endDate: LocalDate, rate: Double) extends Rate
case class ParSwapRate(tenor: String, startDate: LocalDate, endDate: LocalDate, rate: Double) extends Rate 

trait YieldCurve {
  
  val name : String
  val points : List[Rate]
  lazy val discountFactors : Map[Rate, Double] = bootstrap  
  def bootstrap : Map[Rate, Double] 
  
}

class ZeroCurve(val name: String, val points: List[Rate]) extends YieldCurve {
  
  def bootstrap = {
    ???
  }
  
}

object YieldCurve {  
  
  def loadFromCsv(name: String, cob: LocalDate, csvString: String, dayCon: BusinessDayConvention)(implicit csvParser : String => Vector[Vector[String]]) = {
     val csv = csvParser(csvString)
     val points = csv.tail.map{row => {
        val instrument = row(0)
        val term = row(1)
        val rate = row(2).toDouble
        val startDate = dayCon.businessDate(cob.plusDays(settleDays(term)))
        val endDate = dayCon.businessDate(calculateTerm(startDate, term))
        instrument.toLowerCase match {
          case "cash" => new MoneyMarketRate(term, startDate, endDate, rate)
          case "fra" => new FRARate(term, startDate, endDate, rate)
          case "swap" => new ParSwapRate(term, startDate, endDate, rate)
          case _ => throw new IllegalStateException("Invalid instrument: " + instrument)
        }
      }}
     new ZeroCurve(name, points.toList)
  }
  
}