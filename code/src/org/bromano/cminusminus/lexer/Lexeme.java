package org.bromano.cminusminus.lexer;

public class Lexeme {
    public LexemeKind kind;
    public String value;
    public LinePosition linePos;


    public Lexeme(LexemeKind kind) {
        this.kind = kind;
    }

    public Lexeme(LexemeKind kind, String value) {
       this.kind = kind;
        this.value = value;
    }

    public Lexeme(LexemeKind kind, LinePosition linePos) {
        this.kind = kind;

        this.linePos = linePos;
    }

    public Lexeme(LexemeKind kind, String value, LinePosition linePos) {
        this.kind = kind;
        this.value = value;
        this.linePos = linePos;
    }

    @Override
    public String toString() {
        if (linePos != null) {
            return this.linePos.toString() + " " + this.kind.name() + " " + (this.value != null ? this.value : "");
        }
        return this.kind.name() + " " + this.value;
    }

    @Override
    public boolean equals(Object obj) {
        if(obj == null || getClass() != obj.getClass()) {
            return false;
        }

        Lexeme lexeme = (Lexeme) obj;
        return (lexeme.kind == this.kind
                &&  (lexeme.value == null && this.value == null
                || (lexeme.value != null && this.value != null && lexeme.value.equals(this.value))));
    }
}
