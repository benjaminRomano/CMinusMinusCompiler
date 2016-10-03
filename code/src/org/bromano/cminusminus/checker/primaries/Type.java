package org.bromano.cminusminus.checker.primaries;

import org.bromano.cminusminus.checker.CheckerException;
import org.bromano.cminusminus.lexer.TokenKind;

public enum Type {
    Number,
    Boolean,
    BooleanArray,
    NumberArray,
    Function;


    public static Type fromKind(TokenKind kind) throws CheckerException {
        if (kind == TokenKind.IntKeyword) {
            return Number;
        } else if (kind == TokenKind.BoolKeyword) {
            return Boolean;
        }

        throw new CheckerException(String.format("Could not convert %s to type", kind));
    }
}
