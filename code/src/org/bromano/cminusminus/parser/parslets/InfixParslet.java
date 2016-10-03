package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Token;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public interface InfixParslet {
    Expression parse(Parser parser, Expression lhs, Token token) throws ParserException;
    int getPrecedence();
}
