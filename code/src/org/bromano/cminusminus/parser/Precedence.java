package org.bromano.cminusminus.parser;

public class Precedence {
    public static final int ASSIGNMENT = 1;
    public static final int LOGICALAND = 2;
    public static final int LOGICALOR = 3;
    public static final int EQUALITY = 4;
    public static final int RELATIONAL = 5;
    public static final int ADDITIVE = 6;
    public static final int MULTIPLICATIVE = 7;
    public static final int EXPONENT = 8;
    public static final int PREFIX = 9;
    public static final int POSTFIX = 10;
    public static final int CALL = 11;
}
