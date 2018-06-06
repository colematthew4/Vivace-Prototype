package cole.matthew.vivace.Exceptions;

public class StorageNotReadableException extends Exception
{
    public StorageNotReadableException()
    { }

    public StorageNotReadableException(String message)
    {
        super(message);
    }
}
