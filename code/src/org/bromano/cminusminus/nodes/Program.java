package org.bromano.cminusminus.nodes;

import org.bromano.cminusminus.nodes.declarations.Declaration;
import org.bromano.cminusminus.symbols.SymbolTable;

import java.util.List;

public class Program implements ParserNode {
    public List<Declaration> declarations;
    public SymbolTable symbolTable;
}
