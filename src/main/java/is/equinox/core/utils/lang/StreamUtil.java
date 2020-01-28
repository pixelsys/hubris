package is.equinox.core.utils.lang;

import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import scala.Tuple2;

public class StreamUtil {

  private StreamUtil() {}

  public static <A, B> Stream<Tuple2<A,B>> zip(Stream<A> as, Stream<B> bs) {
    Iterator<A> i1 = as.iterator();
    Iterator<B> i2 = bs.iterator();
    Iterable<Tuple2<A,B>> i=()->new Iterator<>() {
      public boolean hasNext() {
        return i1.hasNext() && i2.hasNext();
      }
      public Tuple2<A,B> next() {
        return new Tuple2<>(i1.next(), i2.next());
      }
    };
    return StreamSupport.stream(i.spliterator(), false);
  }

}
