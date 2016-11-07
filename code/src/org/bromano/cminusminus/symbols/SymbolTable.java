package org.bromano.cminusminus.symbols;

import org.bromano.cminusminus.checker.CheckerException;

import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    public SymbolTable parent;
    Map<String, Symbol> variables;

    public SymbolTable() {
        this.variables = new HashMap<>();
    }

    public void addVariable(String name, Symbol symbol) throws CheckerException {
        if (this.variables.containsKey(name)) {
            throw new CheckerException(String.format("`%s` already exists in scope", name));
        }

        this.variables.put(name, symbol);
   }

    public boolean hasVariable(String name) {

        SymbolTable currTable = this;

        while (currTable != null) {
            if (currTable.variables.containsKey(name)) {
                return true;
            }

            currTable = currTable.parent;
        }

        return false;
    }

    public Symbol getVariable(String name) throws CheckerException {

        SymbolTable currTable = this;

        while (currTable != null) {
            if (currTable.variables.containsKey(name)) {
                return currTable.variables.get(name);
            }

            currTable = currTable.parent;
        }

        throw new CheckerException(String.format("%s does not exist in scope", name));
    }
}
