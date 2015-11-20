package is.equinox

package object math {

  case class Precision(val p:Double)
  
  implicit class DoubleWithAlmostEquals(val d:Double) extends AnyVal {
    def ~==(d2:Double)(implicit p:Precision) = (d - d2).abs < p.p
  }   
  
}