package is.equinox.hubris.model.trade;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.experimental.SuperBuilder;

@SuperBuilder
public abstract class Instrument {

  protected String id;
  protected LocalDate effectiveDate;

  abstract BigDecimal value(LocalDate cob);

}
