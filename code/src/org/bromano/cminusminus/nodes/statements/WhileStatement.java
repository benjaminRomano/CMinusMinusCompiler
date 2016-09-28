package org.bromano.cminusminus.nodes.statements;

import org.bromano.cminusminus.nodes.expressions.Expression;

public class WhileStatement implements Statement {
    public Expression conditional;
    public Statement statement;
}
