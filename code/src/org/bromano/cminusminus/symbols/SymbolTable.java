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

        SymbolTable currEnviroment = this;

        while (currEnviroment != null) {
            if (currEnviroment.variables.containsKey(name)) {
                return true;
            }

            currEnviroment = currEnviroment.parent;
        }

        return false;
    }

    public Symbol getVariable(String name) throws CheckerException {

        SymbolTable currEnviroment = this;

        while (currEnviroment != null) {
            if (currEnviroment.variables.containsKey(name)) {
                return currEnviroment.variables.get(name);
            }

            currEnviroment = currEnviroment.parent;
        }

        throw new CheckerException(String.format("%s does not exist in scope", name));
    }
}
