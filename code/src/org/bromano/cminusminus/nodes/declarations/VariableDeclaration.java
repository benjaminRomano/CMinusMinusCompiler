package org.bromano.cminusminus.nodes.declarations;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.Expression;

public class VariableDeclaration implements Declaration {
    public Lexeme name;
    public Expression expression;
}
