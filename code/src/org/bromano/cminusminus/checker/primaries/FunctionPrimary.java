package org.bromano.cminusminus.checker.primaries;

import java.util.ArrayList;
import java.util.List;

public class FunctionPrimary extends Primary {
    public List<ParameterPrimary> parameters;

    public FunctionPrimary() {
        super(Type.Function);
        parameters = new ArrayList<>();
    }
}
