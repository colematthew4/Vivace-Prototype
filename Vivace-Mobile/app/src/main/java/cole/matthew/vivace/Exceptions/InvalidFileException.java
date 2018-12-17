package cole.matthew.vivace.Exceptions;

public class InvalidFileException extends Exception {
    public InvalidFileException(String fileExtension) {
        super(String.format("Trying to open a file that isn't supported. File has extension .%s", fileExtension));
    }
}
