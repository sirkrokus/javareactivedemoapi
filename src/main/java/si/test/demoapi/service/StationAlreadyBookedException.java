package si.test.demoapi.service;

public class StationAlreadyBookedException extends Exception {
    public StationAlreadyBookedException() {
    }

    public StationAlreadyBookedException(String message) {
        super(message);
    }

    public StationAlreadyBookedException(String message, Throwable cause) {
        super(message, cause);
    }

    public StationAlreadyBookedException(Throwable cause) {
        super(cause);
    }

    public StationAlreadyBookedException(String message, Throwable cause, boolean enableSuppression, boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }
}
