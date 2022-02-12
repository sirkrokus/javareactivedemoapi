package si.test.demoapi.service;

public class StationIsNotFoundException extends Exception {
    public StationIsNotFoundException() {
    }

    public StationIsNotFoundException(String message) {
        super(message);
    }

    public StationIsNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }

    public StationIsNotFoundException(Throwable cause) {
        super(cause);
    }

    public StationIsNotFoundException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
