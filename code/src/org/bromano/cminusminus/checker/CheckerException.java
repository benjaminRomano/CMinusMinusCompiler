package org.bromano.cminusminus.checker;

import org.bromano.cminusminus.lexer.LinePosition;

public class CheckerException extends Exception {
    public CheckerException(String message) {
        super(message);
    }

    public CheckerException(String message, LinePosition position) {
        super(message + " at " + position);
    }
}
