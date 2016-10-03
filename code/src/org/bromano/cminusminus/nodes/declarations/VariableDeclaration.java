package org.bromano.cminusminus.nodes.declarations;

import org.bromano.cminusminus.lexer.Token;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclaration implements Declaration {
    public Token type;
    public List<Variable> variables;

    public VariableDeclaration() {
        this.variables = new ArrayList<>();
    }
}
