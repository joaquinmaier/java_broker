package stind;

public interface ErrorHandler {
    <T extends Exception> void run(T exception);
}
