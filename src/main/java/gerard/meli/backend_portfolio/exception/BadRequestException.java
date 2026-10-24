package gerard.meli.backend_portfolio.exception;



/** 400 - requête invalide côté métier. */
public class BadRequestException extends RuntimeException {

    public BadRequestException(String message) {
        super(message);
    }
}
