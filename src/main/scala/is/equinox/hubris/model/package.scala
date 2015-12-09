package is.equinox.hubris.model

import java.time.LocalDate

package object marketdata {

  val DaysRE = "([0-9]+)D".r                     
  val WeeksRE = "([0-9]+)W".r                    
  val MonthsRE = "([0-9]+)M".r                   
  val YearsRE = "([0-9]+)Y".r    
  
  object isOvernightTerm {
    def unapply(term: String) = term match {
      case "ON" | "0D" => true
      case _ => false
    }
  }
  
  object isTomorrowTerm {
    def unapply(term: String) = term match {
      case "T/N" | "TN" | "1D" => true
      case _ => false
    }    
  }
  
  def settleDays(term: String) : Int = {
    term.toUpperCase match {
      case isOvernightTerm() => 0
      case isTomorrowTerm() => 1
      case _ => 2
    }
  }   
 
  def calculateTerm(start: LocalDate, term: String) : LocalDate = {
    term.toUpperCase match {
      case isOvernightTerm() | isTomorrowTerm() => start.plusDays(1)
      case DaysRE(days) => start.plusDays(days.toLong)
      case WeeksRE(weeks) => start.plusWeeks(weeks.toLong)
      case MonthsRE(months) => start.plusMonths(months.toLong)
      case YearsRE(years) => start.plusYears(years.toLong)
      case _ => throw new IllegalArgumentException
    }
  }      
  
}