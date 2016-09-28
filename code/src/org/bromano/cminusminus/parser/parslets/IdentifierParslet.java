package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.IdentifierExpression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class IdentifierParslet implements PrefixParslet {

    public IdentifierParslet() {
    }

    public Expression parse(Parser parser, Lexeme lexeme) throws ParserException {

        IdentifierExpression identifierExpression = new IdentifierExpression();

        identifierExpression.name = lexeme;

        return identifierExpression;
    }
}
