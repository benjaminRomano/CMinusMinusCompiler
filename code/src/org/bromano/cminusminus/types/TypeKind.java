package org.bromano.cminusminus.types;

import org.bromano.cminusminus.checker.CheckerException;
import org.bromano.cminusminus.lexer.TokenKind;

public enum TypeKind {
    Number,
    Boolean,
    Array,
    Function;

    public static TypeKind fromTokenKind(TokenKind kind) throws CheckerException {
        if (kind == TokenKind.IntKeyword) {
            return Number;
        } else if (kind == TokenKind.BoolKeyword) {
            return Boolean;
        }

        throw new CheckerException(String.format("Could not convert %s to type", kind));
    }
}
