package org.bromano.cminusminus.lexer;

import java.util.List;

public interface Lexer {
    Token lex() throws LexerException;
    List<Token> getLexStream() throws LexerException;
    void setText(String text);
}
