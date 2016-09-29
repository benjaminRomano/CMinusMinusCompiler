package org.bromano.cminusminus.nodes.statements;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.expressions.Expression;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallStatement implements Statement {
    public Lexeme name;
    public List<Expression> arguments;

    public FunctionCallStatement() {
        this.arguments = new ArrayList<>();
    }
}
