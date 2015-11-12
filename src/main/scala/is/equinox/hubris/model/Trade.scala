package is.equinox.hubris.model

import java.util.Date

trait Instrument {
  
  def id : String
  def tradeDate : Date
  def value : Double
  
}

trait Bond extends Instrument {
  
  def notional : Double
  
}

trait FixedRate extends Bond {
  
  def rate : Double
  def expiration : Date
  
}

trait FloatingRateNote extends Bond {
  
  def benchmark : Benchmark
  def expiration : Date
  
}

class IRSwap(id: String, notional: BigDecimal, tradeDate: Date) {  
  
}