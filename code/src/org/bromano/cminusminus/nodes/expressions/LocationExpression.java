package org.bromano.cminusminus.nodes.expressions;

import org.bromano.cminusminus.lexer.Lexeme;

public class LocationExpression implements Expression {
    public Lexeme name;
    public Expression expression;
}
