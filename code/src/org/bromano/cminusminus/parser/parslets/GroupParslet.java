package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.lexer.LexemeKind;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class GroupParslet implements PrefixParslet {

    public Expression parse(Parser parser, Lexeme lexeme) throws ParserException {
        Expression expression = parser.parseExpression(0);
        parser.match(LexemeKind.CloseParen);

        return expression;
    }
}
