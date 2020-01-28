package is.equinox.hubris.context;

import is.equinox.hubris.dal.YieldCurveDao;
import is.equinox.hubris.model.calendar.BusinessDayConvention;
import is.equinox.hubris.model.calendar.DayCountConvention;
import is.equinox.hubris.model.marketdata.curve.YieldCurve;
import is.equinox.hubris.model.marketdata.curve.ZeroCurve;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class MarketDataContext {

  private final LocalDate asOfDate;

  private Map<String, YieldCurve> yieldCurves = new HashMap<>();

  public YieldCurve getYieldCurve(String yieldCurveName) {
    return yieldCurves.get(yieldCurveName);
  }

  public YieldCurve loadYieldCurve(String yieldCurveName, BusinessDayConvention dayCon, DayCountConvention dcc) throws URISyntaxException, IOException {
    final var csvPath = Paths.get(getClass().getResource("/mktdata/usdlibor_" + asOfDate + ".csv").toURI());
    final var csvString = Files.readString(csvPath);
    var yc = YieldCurveDao.loadFromCsv(yieldCurveName, asOfDate, csvString, dayCon, dcc);
    yieldCurves.put(yieldCurveName, yc);
    return yc;
  }

}
