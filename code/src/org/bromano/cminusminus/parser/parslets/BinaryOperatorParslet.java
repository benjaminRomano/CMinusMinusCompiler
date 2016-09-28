package org.bromano.cminusminus.parser.parslets;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.BinaryExpression;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.parser.Parser;
import org.bromano.cminusminus.parser.ParserException;

public class BinaryOperatorParslet implements InfixParslet {

    private int precedence;
    private boolean isRightAssociative;

    public BinaryOperatorParslet(int precedence, boolean isRightAssociative) {
        this.precedence = precedence;
        this.isRightAssociative = isRightAssociative;
    }

    public Expression parse(Parser parser, Expression lhs, Lexeme lexeme) throws ParserException {

        BinaryExpression binaryExpression = new BinaryExpression();

        binaryExpression.left = lhs;
        binaryExpression.operator = lexeme;

        // Precedence climbing algorithm requires setting precedence -1 for right associative
        if (this.isRightAssociative)  {
            binaryExpression.right = parser.parseExpression(this.precedence - 1);
        } else {
            binaryExpression.right = parser.parseExpression(this.precedence);
        }

        return binaryExpression;
    }

    public int getPrecedence() {
        return this.precedence;
    }
}
