package is.equinox.util

import org.scalatest.{MustMatchers, FlatSpec, ShouldMatchers}

class CsvSpec extends FlatSpec with MustMatchers {

  it should "not fail when quotes are inside a field" in {
      // given
      val line1 = """Tom,Jones,Senior Director,buyer@salesforcesample.com,1940-06-07Z,"Self-described as ""the top"" branding guru on the West Coast""""
      val line2 = "Ian,Dury,Chief Imagineer,cto@salesforcesample.com,,\"World-renowned expert in fuzzy logic design.\n" +
                  "Influential in technology purchases.\""      
      
      // when & then
      SimpleCsvParser.fromString(line1)(0) must contain allOf("Tom", "Jones", "Senior Director", "buyer@salesforcesample.com", "1940-06-07Z", "Self-described as \"the top\" branding guru on the West Coast")
      SimpleCsvParser.fromString(line2)(0) must contain allOf("Ian", "Dury", "Chief Imagineer", "cto@salesforcesample.com", "", "World-renowned expert in fuzzy logic design.\nInfluential in technology purchases.")
    }

  it should   "parse line with tabular separator" in {
      // given
      val line = "ala\tma\tkota"

      // when
      val csvLines = SimpleCsvParser.fromString(line)('\t')

      // then
      csvLines must  have size 1
      csvLines(0) must contain allOf("ala", "ma", "kota")
    }

  it should  "parse line with default separator" in {
      // given
      val line = "ala,ma,kota"

      // when
      val csvLines = SimpleCsvParser.fromString(line)

      // then
      csvLines must  have size 1
      csvLines(0) must contain allOf("ala", "ma", "kota")
    }

  it should   "parse line with quotes in field and default separator and remove quotes" in {
      // given
      val line = "ala,\"ma\",kota"

      // when
      val csvLines = SimpleCsvParser.fromString(line)

      // then
      csvLines must  have size 1
      csvLines(0) must contain allOf("ala", "ma", "kota")
    }

  it should   "accept json content in field" in {
      // given
      val line = """a,b,{"a":"asd","b":"asd"},po"""

      // when
      val csvLines = SimpleCsvParser.fromString(line)

      csvLines must have size 1
      csvLines(0) must contain allOf("a", "b", "po", """{"a":"asd","b":"asd"}""")
    }

  it should   "accept user-agent string with curly braces and prepended by json field" in {
      // given
      val line = """"{""retarget"":""E""}",4,Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SIMBAR={66452C55-2A96-475D-BD1C-F0A0C078F4CB}"""

      // when
      val csvLines = SimpleCsvParser.fromString(line)

      csvLines must  have size 1
      csvLines(0) must contain allOf("""{"retarget":"E"}""", "4", "Mozilla/4.0 (compatible; MSIE 8.0; Windows NT 6.1; WOW64; Trident/4.0; SIMBAR={66452C55-2A96-475D-BD1C-F0A0C078F4CB}")
    }

  it should   "accept curly brace inside a field" in {
      // given
      val line = "a,b,{\"json\":\"example\"},c"

      // when
      val csvLines = SimpleCsvParser.fromString(line)
      
      // then
      csvLines must  have size 1
      csvLines(0) must contain allOf("a", "b", "c", "{\"json\":\"example\"}")
    }

  it should   "place rows separately in the output Vector" in {
    // given
    val multiline = "colA,colB,colC\n" +
                    "valA1,valB1,valC1\n" +
                    "valA2,valB2,valC2"
    // when
    val csvLines = SimpleCsvParser.fromString(multiline)

    // then
    csvLines must  have size 3
    csvLines(0) must contain allOf("colA", "colB", "colC")
    csvLines(1) must contain allOf("valA1", "valB1", "valC1")
    csvLines(2) must contain allOf("valA2", "valB2", "valC2")
  }
 
}