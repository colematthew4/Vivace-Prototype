package cole.matthew.vivace.Exceptions;

import java.util.Locale;

public class NegativeNumberException extends Exception {
    public NegativeNumberException(Number value) {
        this("Expected a positive integer, got ", value);
    }

    public NegativeNumberException(String message, Number value) {
        super(String.format(Locale.US, "%s%f", message, value));
    }
}
