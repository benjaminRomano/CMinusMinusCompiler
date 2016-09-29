package org.bromano.cminusminus.nodes.expressions;

import org.bromano.cminusminus.lexer.Lexeme;

public class UnaryExpression implements Expression {
    public Lexeme operator;
    public Expression expression;
}
