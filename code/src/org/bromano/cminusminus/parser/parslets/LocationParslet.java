package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.lexer.LexemeKind;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.LocationExpression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class LocationParslet implements PrefixParslet {

    public Expression parse(Parser parser, Lexeme lexeme) throws ParserException {

        LocationExpression locationExpression = new LocationExpression();

        locationExpression.name = lexeme;
        if (parser.isAMatch(LexemeKind.OpenBracket)) {
            parser.match();
            locationExpression.expression = parser.parseExpression(0);
            parser.match(LexemeKind.CloseBracket);
        }

        return locationExpression;
    }
}
