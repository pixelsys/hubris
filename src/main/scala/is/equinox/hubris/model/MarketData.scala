package is.equinox.hubris.model

import java.time.LocalDate
import java.io.File

trait MarketData

sealed trait Rate extends MarketData {
  val startDate : LocalDate
  val endDate : LocalDate
  val rate : Double
  def days = {
    // settlement date - quote date
    ???
  }
}
case class MoneyMarketRate(tenor: String, startDate: LocalDate, endDate: LocalDate, rate: Double) extends Rate
case class FRARate(contract: String, startDate: LocalDate, endDate: LocalDate, rate: Double) extends Rate
case class ParSwapRate(tenor: String, startDate: LocalDate, endDate: LocalDate, rate: Double) extends Rate 

trait YieldCurve {
  
  val name : String
  val points : List[Rate]
  lazy val discountFactors : Map[Rate, Double] = bootstrap
  
  def bootstrap : Map[Rate, Double] = {
    // bootstrap dfs based on points
    ???
  }
  
}

class ZeroCurve

object YieldCurve {
  
  def loadFromCsv(name: String, cob: LocalDate, file: File) = {
    ???
  }
  
}