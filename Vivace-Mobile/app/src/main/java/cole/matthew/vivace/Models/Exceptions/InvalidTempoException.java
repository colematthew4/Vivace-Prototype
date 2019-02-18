package cole.matthew.vivace.Models.Exceptions;

import java.util.Locale;

public class InvalidTempoException extends Exception {
    public InvalidTempoException(Number value) {
        this("The given tempo was invalid, got ", value);
    }

    public InvalidTempoException(String message, Number value) {
        super(String.format(Locale.US, "%s%f", message, value));
    }
}
