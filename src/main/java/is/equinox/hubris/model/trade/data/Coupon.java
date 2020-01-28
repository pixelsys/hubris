package is.equinox.hubris.model.trade.data;

import java.math.BigDecimal;
import java.time.LocalDate;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.ToString;

@RequiredArgsConstructor
@Getter
@ToString
public class Coupon {

  private final LocalDate startDate;
  private final LocalDate endDate;
  private final BigDecimal factor;
  private final BigDecimal amount;

}
