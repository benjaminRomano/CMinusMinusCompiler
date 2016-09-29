package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.BooleanExpression;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class BooleanParslet implements PrefixParslet {

    public Expression parse(Parser parser, Lexeme lexeme) throws ParserException {

        BooleanExpression booleanExpression = new BooleanExpression();

        booleanExpression.value = lexeme;

        return booleanExpression;
    }
}
