package is.equinox.hubris.model.trade;

import java.time.LocalDate;

public interface Instrument {

  String tradeId();

  LocalDate effectiveDate();

}
