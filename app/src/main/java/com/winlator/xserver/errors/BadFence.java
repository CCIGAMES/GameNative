package com.winlator.xserver.errors;

public class BadFence extends XRequestError {
    public BadFence(int id) {
        super(Byte.MIN_VALUE + 2, id);
    }
}
