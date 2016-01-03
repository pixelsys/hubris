package is.equinox.hubris.model

import java.time.LocalDate
import java.io.File
import is.equinox.util.SimpleCsvParser
import collection.immutable.Vector
import is.equinox.hubris.model.marketdata._
import is.equinox.time.BusinessDayConvention
import java.time.Period
import java.time.temporal.ChronoUnit
import is.equinox.time.DayCountConvention
import is.equinox.math.CurveInterpolator
import is.equinox.math.SplineInterpolator
import is.equinox.math.CurveInterpolationContext

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
  val discountFactors : Map[Rate, Double] /*= bootstrap  
  def bootstrap : Map[Rate, Double] */
  
  def interpolate(date: LocalDate) : Double
  
  def discountFactor(date: LocalDate) : Double = {
    val dfPoints = discountFactors.map{ case(k, v) => k.endDate -> v }
    //if(!dfPoints.contains(date)) {
      //Console.println("hoppare zimi: " + date)
    //}
    dfPoints.getOrElse(date, interpolate(date))
  }
  
}

class ZeroCurve(val name: String, val points: List[Rate], val discountFactors : Map[Rate, Double], interpolation: (LocalDate => Double)) extends YieldCurve {
  
  def interpolate(date: LocalDate): Double = interpolation(date)
  
}

abstract class CurveBootstrap {
  
  def bootstrap(points: List[Rate]) : Map[Rate, Double]
  
}

class NWayBootstrap(cob: LocalDate, dayCountCon: DayCountConvention) extends CurveBootstrap {
  
  def df(bootstrap: Double, rate: Double, start: LocalDate, end: LocalDate, dayCountCon: DayCountConvention) : Double = {
     bootstrap / (1 + rate * dayCountCon.factor(start, end))
  }   
  
  def dfSwap(swapRate: Double, bootstrapValue: Double, start: LocalDate, end: LocalDate, dayCountCon: DayCountConvention) : Double = {
     (1 - swapRate * bootstrapValue) / (1 + swapRate * dayCountCon.factor(start, end))
  }
  
  def bootstrap(points: List[Rate]): Map[Rate, Double] = {
    val bootstrapMap = new scala.collection.mutable.HashMap[LocalDate, Double]
    var swapBootstrapValue = 0.0
    var lastPoint = points.head
    val dfList = points.map{p => {
      val dfValue = p match {
        case mm: MoneyMarketRate => {  
          val bootstrap = if(p.startDate == cob) { 1 } else { bootstrapMap(p.startDate) }
          df(bootstrap, p.rate, p.startDate, p.endDate, dayCountCon)          
        }
        case fra: FRARate => {
          ???
        }
        case swap: ParSwapRate => {
          val bootstrap = swapBootstrapValue + dayCountCon.factor(lastPoint.startDate, lastPoint.endDate) * bootstrapMap(lastPoint.endDate)
          dfSwap(p.rate, bootstrap, lastPoint.endDate, p.endDate, dayCountCon)          
        }
        case _ => throw new IllegalArgumentException("Unsupported rate type: " + p)
      }
      bootstrapMap(p.endDate) = dfValue
      lastPoint = p      
      p -> dfValue
    }}
    dfList.toMap
  }  
  
}

object YieldCurve {  
  
  def loadFromCsv(name: String, cob: LocalDate, csvString: String, dayCon: BusinessDayConvention, dayCountCon: DayCountConvention)(implicit csvParser : String => Vector[Vector[String]], interpolator : CurveInterpolator) = {
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
      }}.toList
     val bootstrapper = new NWayBootstrap(cob, dayCountCon)
     val discountFactors = bootstrapper.bootstrap(points)
     val ciCtx = new CurveInterpolationContext(discountFactors.map{ case(k, v) => k.endDate -> v }, interpolator)
     new ZeroCurve(name, points, discountFactors, ciCtx.interpolate)
  }
  
}