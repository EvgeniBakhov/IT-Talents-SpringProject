package finalproject.airbnb.exceptions;

public class AuthorizationException extends RuntimeException {

    public AuthorizationException(String message) {
        super(message);
    }

    public AuthorizationException() {
        super("You must log in!");
    }

}
