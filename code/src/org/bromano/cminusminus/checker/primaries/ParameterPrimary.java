package org.bromano.cminusminus.checker.primaries;

public class ParameterPrimary extends Primary {
    public Primary primary;
    public boolean isAddr;

    public ParameterPrimary(Primary primary, boolean isAddr) {
        super(primary.type);
        this.isAddr = isAddr;
    }
}
