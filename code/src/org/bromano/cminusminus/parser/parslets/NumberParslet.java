package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Token;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.NumberExpression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class NumberParslet implements PrefixParslet {

    public Expression parse(Parser parser, Token token) throws ParserException {

        NumberExpression numberExpression = new NumberExpression();

        numberExpression.value = token;

        return numberExpression;
    }
}
