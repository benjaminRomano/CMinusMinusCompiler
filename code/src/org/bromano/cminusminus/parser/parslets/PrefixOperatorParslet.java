package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.UnaryExpression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class PrefixOperatorParslet implements PrefixParslet {

    private int precedence;

    public PrefixOperatorParslet(int precedence) {
        this.precedence = precedence;
    }

    public Expression parse(Parser parser, Lexeme lexeme) throws ParserException {

        UnaryExpression unaryExpression = new UnaryExpression();

        unaryExpression.operator = lexeme;
        unaryExpression.expression = parser.parseExpression(this.precedence);

        return unaryExpression;
    }
}
