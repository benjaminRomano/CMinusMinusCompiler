package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Token;
import org.bromano.cminusminus.lexer.TokenKind;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.LocationExpression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class LocationParslet implements PrefixParslet {

    public Expression parse(Parser parser, Token token) throws ParserException {

        LocationExpression locationExpression = new LocationExpression();

        locationExpression.name = token;
        if (parser.isAMatch(TokenKind.OpenBracket)) {
            parser.match();
            locationExpression.expression = parser.parseExpression(0);
            parser.match(TokenKind.CloseBracket);
        }

        return locationExpression;
    }
}
