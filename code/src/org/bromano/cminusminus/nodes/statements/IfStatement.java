package org.bromano.cminusminus.nodes.statements;

import org.bromano.cminusminus.nodes.expressions.Expression;

public class IfStatement implements Statement {
    public Expression conditional;
    public Statement statement;
    public Statement elseStatement;
}
