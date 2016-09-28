package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.lexer.LexemeKind;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;
import org.bromano.cminusminus.parser.Precedence;

public class PropertyAccessorParslet implements InfixParslet {

    public Expression parse(Parser parser, Expression lhs, Lexeme lexeme) throws ParserException {

        PropertyAccessorExpression propertyAccessorExpression = new PropertyAccessorExpression();

        propertyAccessorExpression.expression = lhs;

        propertyAccessorExpression.property = parser.match(LexemeKind.Identifier);

        return propertyAccessorExpression;
    }

    public int getPrecedence() {
        return Precedence.POSTFIX;
    }
}
