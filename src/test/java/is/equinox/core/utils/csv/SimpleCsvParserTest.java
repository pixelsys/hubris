package is.equinox.core.utils.csv;

import static org.junit.Assert.assertEquals;

import java.util.List;
import java.util.Set;
import org.junit.Test;

public class SimpleCsvParserTest {

  @Test
  public void should_notFailWhenQuotesAreInsideAField() {
    // given
    var line1 = "Tom,Jones,Senior Director,buyer@salesforcesample.com,1940-06-07Z,\"Self-described as \"\"the top\"\" branding guru on the West Coast\"";
    var line2 = "Ian,Dury,Chief Imagineer,cto@salesforcesample.com,,\"World-renowned expert in fuzzy logic design.\n" +
        "Influential in technology purchases.\"";

    // when & then
    assertEquals(6, SimpleCsvParser.fromString(line1).get(0).size());
    assertEquals(6, SimpleCsvParser.fromString(line2).get(0).size());
  }

  @Test
  public void should_placeRowsSeparatelyInOutputList() {
    // given
    var multiline = "colA,colB,colC\n" +
        "valA1,valB1,valC1\r\n" +
        "valA2,valB2,valC2\r\n" +
        "\"value A\",\"value B\",\"value C\"";
    // when
    var csvLines = SimpleCsvParser.fromString(multiline);

    // then
    assertEquals(4, csvLines.size());
    assertEquals(List.of("colA", "colB", "colC"), csvLines.get(0));
    assertEquals(List.of("valA1", "valB1", "valC1"), csvLines.get(1));
    assertEquals(List.of("valA2", "valB2", "valC2"), csvLines.get(2));
    assertEquals(List.of("value A", "value B", "value C"), csvLines.get(3));
  }

}
