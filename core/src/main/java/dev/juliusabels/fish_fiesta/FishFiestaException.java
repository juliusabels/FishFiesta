package dev.juliusabels.fish_fiesta;

public class FishFiestaException extends RuntimeException {

    public FishFiestaException(String message) {
        super(message);
    }

    public FishFiestaException(String message, Object ... args) {
        super(String.format(message, args));
    }
}
