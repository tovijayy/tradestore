package com.ninja.tradestore.exceptions;

import java.util.List;

public class TradeValidationException extends RuntimeException{

    private List<String> errors;

    public TradeValidationException(String message, List<String> errors) {
        super(message);
        this.errors = errors;
    }

    public List<String> getErrors() { return errors; }

}
