package com.iharshashkou.internetstore.exception;

public class CartConflictException extends RuntimeException {

    public CartConflictException(String message) {
        super(message);
    }
}
