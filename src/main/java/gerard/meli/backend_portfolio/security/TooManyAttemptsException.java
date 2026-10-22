package gerard.meli.backend_portfolio.security;

public class TooManyAttemptsException extends RuntimeException {

    public TooManyAttemptsException() {
        super("Too many failed login attempts");
    }
}