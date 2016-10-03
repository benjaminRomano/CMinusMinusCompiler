package org.bromano.cminusminus.lexer;

public class Token {
    public TokenKind kind;
    public String value;
    public LinePosition linePos;


    public Token(TokenKind kind) {
        this.kind = kind;
    }

    public Token(TokenKind kind, String value) {
       this.kind = kind;
        this.value = value;
    }

    public Token(TokenKind kind, LinePosition linePos) {
        this.kind = kind;

        this.linePos = linePos;
    }

    public Token(TokenKind kind, String value, LinePosition linePos) {
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

        Token token = (Token) obj;
        return (token.kind == this.kind
                &&  (token.value == null && this.value == null
                || (token.value != null && this.value != null && token.value.equals(this.value))));
    }
}
