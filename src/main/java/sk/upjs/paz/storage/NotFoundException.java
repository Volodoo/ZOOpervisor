package sk.upjs.paz.storage;

public class NotFoundException extends RuntimeException {
    public NotFoundException(String message) {
        super(message);
    }
}