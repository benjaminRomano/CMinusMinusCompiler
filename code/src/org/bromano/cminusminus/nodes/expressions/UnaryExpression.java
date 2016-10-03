package org.bromano.cminusminus.nodes.expressions;

import org.bromano.cminusminus.lexer.Token;

public class UnaryExpression implements Expression {
    public Token operator;
    public Expression expression;
}
