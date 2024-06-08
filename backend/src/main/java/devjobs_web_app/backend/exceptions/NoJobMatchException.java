package devjobs_web_app.backend.exceptions;

public class NoJobMatchException extends RuntimeException {
    public NoJobMatchException(String message) {
        super(message);
    }
}
