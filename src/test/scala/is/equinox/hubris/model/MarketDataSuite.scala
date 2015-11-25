package is.equinox.hubris.model

import org.scalatest.FunSuite
import is.equinox.util.SimpleCsvParser

class MarketDataSuite extends FunSuite {
  
  test("Yield Curve can be constructed from data in CSV file") {
    val csvIs =  getClass.getResourceAsStream("/mktdata/usdlibor_2011-11-10.csv")
    val csv = SimpleCsvParser.fromString(scala.io.Source.fromInputStream(csvIs).mkString)
    Console.println(csv)
  }
  
}