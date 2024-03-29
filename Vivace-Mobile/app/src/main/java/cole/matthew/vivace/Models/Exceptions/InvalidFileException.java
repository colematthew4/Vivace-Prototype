package cole.matthew.vivace.Models.Exceptions;

public class InvalidFileException extends Exception {
    public InvalidFileException(String fileExtension) {
        super("Trying to open a file that isn't supported. File has extension ." + fileExtension);
    }
}
