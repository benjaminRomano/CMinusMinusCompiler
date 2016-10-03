package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Token;
import org.bromano.cminusminus.lexer.TokenKind;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class GroupParslet implements PrefixParslet {

    public Expression parse(Parser parser, Token token) throws ParserException {
        Expression expression = parser.parseExpression(0);
        parser.match(TokenKind.CloseParen);

        return expression;
    }
}
