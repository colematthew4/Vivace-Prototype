package cole.matthew.vivace.Models.Exceptions;

public class InvalidTempoException extends Exception {
    public InvalidTempoException(Number value) {
        this("The given tempo was invalid, got ", value);
    }

    public InvalidTempoException(String message, Number value) {
        super(message + value);
    }
}
