package org.bromano.cminusminus.checker;

import org.bromano.cminusminus.checker.primaries.FunctionPrimary;
import org.bromano.cminusminus.checker.primaries.ParameterPrimary;
import org.bromano.cminusminus.checker.primaries.Primary;
import org.bromano.cminusminus.checker.primaries.Type;

public class CheckerContext {
    Environment environment;

    public CheckerContext() throws CheckerException {
        initializeEnvironment();
    }

    private void initializeEnvironment() throws CheckerException {
        environment = new Environment();
        FunctionPrimary scanFunction = new FunctionPrimary();
        ParameterPrimary scanFunctionParameter = new ParameterPrimary(new Primary(Type.Number), true);
        scanFunction.parameters.add(scanFunctionParameter);

        environment.addVariable("scan", scanFunction);

        FunctionPrimary printFunction = new FunctionPrimary();
        ParameterPrimary printFunctionParameter = new ParameterPrimary(new Primary(Type.Number), false);
        printFunction.parameters.add(printFunctionParameter);

        environment.addVariable("print", printFunction);
    }
}
