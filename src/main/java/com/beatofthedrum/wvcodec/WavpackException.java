package com.beatofthedrum.wvcodec;

public class WavpackException extends java.io.IOException {
    private static final long serialVersionUID = -4133889103583083491L;

    public WavpackException(String message) {
        super(message);
    }
    public WavpackException(String message, Throwable cause) {
        super(message, cause);
    }
    public WavpackException(Throwable cause) {
        super(cause);
    }
    public WavpackException() {
    }
}
