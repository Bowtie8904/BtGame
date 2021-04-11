package bt.game.resource.load.exc;

public class LoadException extends RuntimeException
{
    public LoadException(String message)
    {
        super(message);
    }

    public LoadException(String message, Throwable cause)
    {
        super(message, cause);
    }
}