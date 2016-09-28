package org.bromano.cminusminus.nodes.expressions;

import org.bromano.cminusminus.lexer.Lexeme;

public class BinaryExpression implements Expression {
    public Expression left;
    public Lexeme operator;
    public Expression right;

}
