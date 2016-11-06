package org.bromano.cminusminus.types;

import java.util.ArrayList;
import java.util.List;

public class FunctionType extends Type {
    public List<FieldType> parameters;

    public FunctionType() {
        super(TypeKind.Function);
        parameters = new ArrayList<>();
    }

    @Override
    public boolean equals(Object o) {
        if (!(o instanceof FunctionType)) {
            return false;
        }

        FunctionType primaryType = (FunctionType) o;

        if (primaryType.parameters.size() != this.parameters.size()) {
            return false;
        }

        for (int i = 0; i < this.parameters.size(); i++) {
            if (!this.parameters.get(i).equals(primaryType.parameters.get(i))) {
                return false;
            }
        }

        return true;
    }
}
