package org.bromano.cminusminus.nodes.statements;

import org.bromano.cminusminus.nodes.declarations.VariableDeclaration;
import org.bromano.cminusminus.symbols.SymbolTable;

import java.util.ArrayList;
import java.util.List;

public class BlockStatement implements Statement {
    public List<VariableDeclaration> variableDeclarations;
    public List<Statement> statements;
    public SymbolTable symbolTable;

    public BlockStatement() {
        variableDeclarations = new ArrayList<>();
        statements = new ArrayList<>();
    }
}
