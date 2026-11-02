package com.iharshashkou.internetstore.exception;

public class CategoryHasActiveProductsException extends RuntimeException {

    public CategoryHasActiveProductsException(Long categoryId) {
        super("Cannot deactivate category " + categoryId
                + ": it has active products");
    }
}
