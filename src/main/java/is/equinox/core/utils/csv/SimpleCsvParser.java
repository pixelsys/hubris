package is.equinox.core.utils.csv;

import java.util.LinkedList;
import java.util.List;

public class SimpleCsvParser {

  public static final char SEPARATOR = ',';

  enum Status { INITIAL_STATE, INSIDE_STRING, QUOTE_HANDLING, LINE_END, INSIDE_JSON, NORMAL_FIELD }

  private static final class Parser {

    int line = 1;
    Status state = Status.INITIAL_STATE;
    List<List<String>> rows = new LinkedList<>();
    List<String> row = new LinkedList<>();
    int rowSize = 0;
    StringBuilder field = new StringBuilder();

    String source;
    char separator;

    public Parser(String source, char separator) {
      this.source = source;
      this.separator = separator;
    }

    void endField() {
      row.add(field.toString());
      rowSize += 1;
      field = new StringBuilder();
    }

    void endLine() {
      rows.add(row);
      row = new LinkedList<>();
      rowSize = 0;
      line += 1;
    }

    void doStateZero(char c) {
      switch (c) {
        case '"':
          state = Status.INSIDE_STRING;
          break;
        case '\n':
          endField();
          endLine();
          break;
        case '\r':
          state = Status.LINE_END;
          break;
        case '{':
          state = Status.INSIDE_JSON;
          field.append(c);
          break;
        default:
          if (c == separator) {
            endField();
          } else {
            field.append(c);
            state = Status.NORMAL_FIELD;
          }
          break;
      }
    }

    List<List<String>> parse() {
      for(char c: source.toCharArray()) {
        switch(state) {
          case INITIAL_STATE: // Initial state.
            doStateZero(c);
            break;
          case INSIDE_STRING: // Inside a string.
            switch(c) {
              case '"':
                state = Status.QUOTE_HANDLING;
                break;
              default:
                field.append(c);
                break;
            }
            break;
          case QUOTE_HANDLING: // Did we match one double-quote, or two?
            switch (c) {
              case '"':
                field.append('"');
                state = Status.INSIDE_STRING;
                break;
              case '\n':
              case '\r':
                state = Status.INITIAL_STATE;
                doStateZero(c);
                break;
              default:
                if (c != separator) {
                  throw new IllegalStateException("Failed to parse field $field at line $line");
                  //state = Status.INITIAL_STATE;
                  //doStateZero(c);
                }
                break;
            }
            break;
          case LINE_END: // Have '\r', squash following '\n', if any.
            endField();
            endLine();
            state = Status.INITIAL_STATE;
            if (c != '\n') {
              doStateZero(c);
            }
            break;
          case INSIDE_JSON:
            switch(c) {
              case '}':
                state = Status.INITIAL_STATE;
                field.append(c);
                break;
              default:
                field.append(c);
                break;
            }
            break;
          case NORMAL_FIELD:
            doStateZero(c);
            break;
        }
      }
      switch(state) {
        case INITIAL_STATE:
          if (field.length() > 0 || rowSize > 0) {
            endField(); endLine();
          }
          break;
        case QUOTE_HANDLING:
        case INSIDE_JSON:
        case NORMAL_FIELD:
          endField();
          endLine();
          break;
      }
      return rows;
    }

  }

  public static List<List<String>> fromString(String source) {
    return fromString(source, SEPARATOR);
  }

  public static List<List<String>> fromString(String source, char separator) {
    var parser = new Parser(source, separator);
    return parser.parse();
  }

}
