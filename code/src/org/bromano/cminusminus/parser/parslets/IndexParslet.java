package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.lexer.LexemeKind;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;
import org.bromano.cminusminus.parser.Precedence;

public class IndexParslet implements InfixParslet {

    public Expression parse(Parser parser, Expression lhs, Lexeme lexeme) throws ParserException {

        IndexExpression indexExpression = new IndexExpression();

        indexExpression.expression = lhs;

        indexExpression.indexExpression = parser.parseExpression(0);

        parser.match(LexemeKind.CloseBracket);

        return indexExpression;
    }

    public int getPrecedence() {
        return Precedence.POSTFIX;
    }
}
