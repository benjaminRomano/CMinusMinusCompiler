package org.bromano.cminusminus.nodes.declarations;

import org.bromano.cminusminus.lexer.Token;
import org.bromano.cminusminus.nodes.statements.BlockStatement;
import org.bromano.cminusminus.symbols.SymbolTable;

import java.util.List;

public class FunctionDeclaration implements Declaration {
    public Token name;
    public List<Parameter> parameters;
    public BlockStatement blockStatement;
    public SymbolTable symbolTable;
}
