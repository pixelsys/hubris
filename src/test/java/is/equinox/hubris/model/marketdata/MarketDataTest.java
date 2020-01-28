package is.equinox.hubris.model.marketdata;

import is.equinox.core.utils.csv.SimpleCsvParser;
import is.equinox.hubris.dal.YieldCurveDao;
import is.equinox.hubris.model.calendar.DayCountConventionActual360;
import is.equinox.hubris.model.calendar.ModifiedFollowing;
import is.equinox.hubris.model.marketdata.curve.YieldCurve;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDate;
import org.junit.Test;

public class MarketDataTest {

  private YieldCurve loadTestYieldCurve() throws Exception {
    final var cob = LocalDate.of(2011, 11, 10);
    final var csvPath = Paths.get(getClass().getResource("/mktdata/usdlibor_2011-11-10.csv").toURI());
    final var csv = Files.readString(csvPath);
    return YieldCurveDao.loadFromCsv("usdlibor", cob, csv,
        new ModifiedFollowing(), new DayCountConventionActual360());
  }

  @Test
  public void test_bootstrapYieldCurve() throws Exception {
    var curve = loadTestYieldCurve();
    assert(null != curve);
    curve.points().stream().forEach(x ->
        System.out.println(x + " -> " + curve.getDiscountFactor(x))
    );
  }

}
