package org.iru.translation;

public class TranslationException extends Exception {

    public TranslationException(String message, Throwable cause) {
        super(message, cause);
    }

    public TranslationException(String message) {
        super(message);
    }
}
