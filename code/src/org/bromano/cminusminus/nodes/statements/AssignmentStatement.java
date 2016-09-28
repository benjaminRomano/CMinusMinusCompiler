package org.bromano.cminusminus.nodes.statements;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.Expression;

public class AssignmentStatement implements Statement {
    public Lexeme name;
    public Expression expression;
}
