package org.bromano.cminusminus.nodes.expressions;

import org.bromano.cminusminus.lexer.Token;

public class BinaryExpression implements Expression {
    public Expression left;
    public Token operator;
    public Expression right;

}
