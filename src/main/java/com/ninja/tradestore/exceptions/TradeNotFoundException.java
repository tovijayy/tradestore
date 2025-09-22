package com.ninja.tradestore.exceptions;

public class TradeNotFoundException extends RuntimeException {

    public TradeNotFoundException(String message) {
        super(message);
    }

    public TradeNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }


}
