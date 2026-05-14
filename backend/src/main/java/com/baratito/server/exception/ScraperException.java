package com.baratito.server.exception;

public class ScraperException extends RuntimeException {

    private final String source;

    public ScraperException(String source, String message, Throwable cause) {
        super("[" + source + "] " + message, cause);
        this.source = source;
    }

    public ScraperException(String source, String message) {
        super("[" + source + "] " + message);
        this.source = source;
    }

    public String getSource() {
        return source;
    }
}