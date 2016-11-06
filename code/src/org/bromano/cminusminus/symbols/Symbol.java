package org.bromano.cminusminus.symbols;

import org.bromano.cminusminus.types.Type;
import org.bromano.cminusminus.types.TypeKind;

public class Symbol {

    public String label;

    private Type type;
    private boolean reference;

    public Symbol(Type type) {
        this.type = type;
        this.reference = false;
    }

    public Symbol(Type type, boolean reference) {
        this.type = type;
        this.reference = reference;
    }

    public boolean isReference() {
        return this.reference;
    }

    public Type getType() {
        return this.type;
    }

    public TypeKind getTypeKind() {
        return this.type.getTypeKind();
    }
}
