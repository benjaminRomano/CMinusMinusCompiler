package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.lexer.LexemeKind;
import org.bromano.cminusminus.nodes.expressions.LocationExpression;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

import java.util.ArrayList;

public class ArrayParslet implements PrefixParslet {

    public Expression parse(Parser parser, Lexeme lexeme) throws ParserException {

        LocationExpression arrayExpression = new LocationExpression();

        arrayExpression.values = new ArrayList<>();

        if (!parser.isAMatch(LexemeKind.CloseBracket)) {
            arrayExpression.values.add(parser.parseExpression(0));
        }

        while (parser.isAMatch(LexemeKind.Comma)) {
            parser.match(LexemeKind.Comma);
            arrayExpression.values.add(parser.parseExpression(0));
        }

        parser.match(LexemeKind.CloseBracket);

        return arrayExpression;
    }
}
