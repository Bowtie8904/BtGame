package bt.game.core.container.exc;

public class InitException extends RuntimeException
{
    public InitException(String message)
    {
        super(message);
    }

    public InitException(String message, Throwable cause)
    {
        super(message, cause);
    }
}