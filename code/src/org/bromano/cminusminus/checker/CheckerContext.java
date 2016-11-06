package org.bromano.cminusminus.checker;

import org.bromano.cminusminus.symbols.Symbol;
import org.bromano.cminusminus.symbols.SymbolTable;
import org.bromano.cminusminus.types.FieldType;
import org.bromano.cminusminus.types.FunctionType;
import org.bromano.cminusminus.types.LiteralType;
import org.bromano.cminusminus.types.TypeKind;

public class CheckerContext {
    SymbolTable symbolTable;

    public CheckerContext() throws CheckerException {
        initializeSymbolTable();
    }

    private void initializeSymbolTable() throws CheckerException {
        symbolTable = new SymbolTable();
        FunctionType scanFunction = new FunctionType();
        FieldType scanFunctionParameter = new FieldType("value", new LiteralType(TypeKind.Number), true);
        scanFunction.parameters.add(scanFunctionParameter);
        Symbol scanSymbol = new Symbol(scanFunction);

        symbolTable.addVariable("scan", scanSymbol);

        FunctionType printFunction = new FunctionType();
        FieldType printFunctionParameter = new FieldType("value", new LiteralType(TypeKind.Number), false);
        printFunction.parameters.add(printFunctionParameter);

        Symbol printSymbol = new Symbol(printFunction);

        symbolTable.addVariable("print", printSymbol);
    }
}
