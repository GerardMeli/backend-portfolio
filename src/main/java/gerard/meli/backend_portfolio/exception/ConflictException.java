package gerard.meli.backend_portfolio.exception;



/** 409 - conflit (doublon, état incompatible). */
public class ConflictException extends RuntimeException {

    public ConflictException(String message) {
        super(message);
    }
}
