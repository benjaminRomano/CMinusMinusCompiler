package org.bromano.cminusminus.nodes.declarations;

import org.bromano.cminusminus.lexer.Lexeme;

import java.util.ArrayList;
import java.util.List;

public class VariableDeclaration implements Declaration {
    public Lexeme type;
    public List<Variable> variables;

    public VariableDeclaration() {
        this.variables = new ArrayList<>();
    }
}
