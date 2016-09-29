package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.NumberExpression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class NumberParslet implements PrefixParslet {

    public Expression parse(Parser parser, Lexeme lexeme) throws ParserException {

        NumberExpression numberExpression = new NumberExpression();

        numberExpression.value = lexeme;

        return numberExpression;
    }
}
