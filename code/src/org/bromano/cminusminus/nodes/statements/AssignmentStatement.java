package org.bromano.cminusminus.nodes.statements;

import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.LocationExpression;

public class AssignmentStatement implements Statement {
    public LocationExpression locationExpression;
    public Expression expression;
}
