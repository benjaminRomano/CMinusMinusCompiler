package org.bromano.cminusminus.checker;

import org.bromano.cminusminus.checker.primaries.Primary;

import java.util.HashMap;
import java.util.Map;

public class Environment {
    public Environment parent;
    Map<String, Primary> variables;

    public Environment() {
        this.variables = new HashMap<>();
    }

    public void addVariable(String name, Primary primary) throws CheckerException {
        if (this.variables.containsKey(name)) {
            throw new CheckerException(String.format("`%s` already exists in scope", name));
        }

        this.variables.put(name, primary);
   }

    public boolean hasVariable(String name) {

        Environment currEnviroment = this;

        while (currEnviroment != null) {
            if (currEnviroment.variables.containsKey(name)) {
                return true;
            }

            currEnviroment = currEnviroment.parent;
        }

        return false;
    }

    public Primary getVariable(String name) throws CheckerException {

        Environment currEnviroment = this;

        while (currEnviroment != null) {
            if (currEnviroment.variables.containsKey(name)) {
                return currEnviroment.variables.get(name);
            }

            currEnviroment = currEnviroment.parent;
        }

        throw new CheckerException(String.format("%s does not exist in scope", name));
    }
}
