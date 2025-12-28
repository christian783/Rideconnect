package app.io.rideconnect.exception;

public class AccountDisabledException extends RuntimeException {
    public AccountDisabledException(String message) { super(message); }
}