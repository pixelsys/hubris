package is.equinox.hubris.model.marketdata.curve.bootstrap;

import is.equinox.hubris.model.marketdata.Rate;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

public interface CurveBootstrap {

  Map<Rate, BigDecimal> bootstrap(List<Rate> points);

}
