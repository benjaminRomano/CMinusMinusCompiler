package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.IntegerLiteralExpression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class IntegerLiteralParslet implements PrefixParslet {

    public Expression parse(Parser parser, Lexeme lexeme) throws ParserException {

        IntegerLiteralExpression integerLiteralExpression = new IntegerLiteralExpression();

        integerLiteralExpression.integer = lexeme;

        return integerLiteralExpression;
    }
}
