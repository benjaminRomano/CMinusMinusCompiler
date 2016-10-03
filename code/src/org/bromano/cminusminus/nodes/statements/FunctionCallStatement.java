package org.bromano.cminusminus.nodes.statements;

import org.bromano.cminusminus.lexer.Token;
import org.bromano.cminusminus.nodes.expressions.Expression;

import java.util.ArrayList;
import java.util.List;

public class FunctionCallStatement implements Statement {
    public Token name;
    public List<Expression> arguments;

    public FunctionCallStatement() {
        this.arguments = new ArrayList<>();
    }
}
