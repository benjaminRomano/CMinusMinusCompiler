package org.bromano.cminusminus.nodes.expressions;

import org.bromano.cminusminus.lexer.Token;

public class LocationExpression implements Expression {
    public Token name;
    public Expression expression;
}
