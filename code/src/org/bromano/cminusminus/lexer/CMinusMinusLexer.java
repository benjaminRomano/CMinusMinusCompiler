package org.bromano.cminusminus.lexer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CMinusMinusLexer implements Lexer {

    private String text;
    private int lineNum;
    private int lineStart;
    private int pos;
    private int end;
    private Map<String, TokenKind> keywordMap;

    public CMinusMinusLexer() {
        this.keywordMap = this.generateKeywordMap();
    }

    public CMinusMinusLexer(String text) {
        this.setText(text);
        this.keywordMap = this.generateKeywordMap();
    }

    @Override
    public void setText(String text) {
        this.text = text;
        this.pos = 0;
        this.lineNum = 1;
        this.lineStart = 0;
        this.end = this.text.length();
    }

    @Override
    public Token lex() throws LexerException {

        while (true) {
            String value;
            LinePosition linePos;

            if (this.pos >= this.end) {
                return null;
            }

            char ch = text.charAt(this.pos);

            switch (ch) {
                case '\n':
                    this.pos++;
                    this.lineNum++;
                    this.lineStart = this.pos;
                    continue;
                case '\t':
                case '\r':
                case '\f':
                case ' ':
                    this.pos++;
                    continue;
                case '!':
                    linePos = getLinePosition();
                    this.pos++;
                    if (isAMatch(this.pos, "=")) {
                        this.pos++;
                        return new Token(TokenKind.ExclamationEquals, "!=", linePos);
                    }

                    return new Token(TokenKind.Exclamation, "!", linePos);
                case '%':
                    linePos = getLinePosition();
                    this.pos++;

                    return new Token(TokenKind.Percent, "%", linePos);
                case '&':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "&")) {
                        this.pos++;
                        return new Token(TokenKind.AmpersandAmpersand, "&&", linePos);
                    }

                    return new Token(TokenKind.Ampersand, "&", linePos);
                case '(':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.OpenParen, linePos);
                case ')':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.CloseParen, linePos);
                case '*':
                    linePos = getLinePosition();
                    this.pos++;

                    return new Token(TokenKind.Asterisk, "*", linePos);
                case '+':
                    linePos = getLinePosition();
                    this.pos++;

                    return new Token(TokenKind.Plus, "+", linePos);
                case ',':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.Comma, ",", linePos);
                case '-':
                    linePos = getLinePosition();
                    this.pos++;

                    return new Token(TokenKind.Minus, "-", linePos);
                case '.':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.Dot, ".", linePos);
                case '/':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "*")) {
                        this.pos++;

                        while (pos < end) {
                            if (isAMatch(pos, "*/")) {
                                this.pos += 2;
                                break;
                            }

                            if (isAMatch(this.pos, "\n")) {
                                this.pos++;
                                this.lineNum++;
                                this.lineStart = this.pos;
                                continue;
                            }

                            this.pos++;
                        }

                        continue;
                    }

                    return new Token(TokenKind.Slash, "/", linePos);
                case ';':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.Semicolon, ";", linePos);
                case '<':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "=")) {
                        this.pos++;
                        return new Token(TokenKind.LessThanEquals, "<=", linePos);
                    }

                    return new Token(TokenKind.LessThan, "<", linePos);
                case '=':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "=")) {
                        this.pos++;
                        return new Token(TokenKind.EqualsEquals, "==", linePos);
                    } else if (this.isAMatch(this.pos, ">")) {
                        this.pos++;
                        return new Token(TokenKind.EqualsGreaterThan, "=>", linePos);
                    }

                    return new Token(TokenKind.Equals, "=", linePos);
                case '>':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "=")) {
                        this.pos++;
                        return new Token(TokenKind.GreaterThanEquals, ">=", linePos);
                    }

                    return new Token(TokenKind.GreaterThan, ">", linePos);
                case '[':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.OpenBracket, linePos);
                case ']':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.CloseBracket, linePos);
                case '_':
                    linePos = getLinePosition();
                    value = this.scanIdentifier();
                    return new Token(TokenKind.Identifier, value, linePos);
                case '{':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.OpenBrace, linePos);
                case '}':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Token(TokenKind.CloseBrace, linePos);
                case '|':
                    linePos = getLinePosition();
                    this.pos++;

                    if (!isAMatch(this.pos, "|")) {
                        throw new LexerException(error("Expected a |"));
                    }

                    this.pos++;
                    return new Token(TokenKind.BarBar, "||", linePos);
                case '0':
                case '1':
                case '2':
                case '3':
                case '4':
                case '5':
                case '6':
                case '7':
                case '8':
                case '9':
                    linePos = getLinePosition();
                    value = this.scanNumber();
                    return new Token(TokenKind.Number, value, linePos);
                default:
                    linePos = getLinePosition();

                    if (this.isAlphabetOrUnderscore(ch)) {

                        String identifierOrKeyword = this.scanIdentifier();

                        if (this.keywordMap.containsKey(identifierOrKeyword.trim())) {
                            return new Token(keywordMap.get(identifierOrKeyword.trim()), linePos);
                        }

                        return new Token(TokenKind.Identifier, identifierOrKeyword, linePos);
                    }

                    throw new LexerException(error("Char " + ch + " cannot be lexed"));
            }
        }
    }

    @Override
    public List<Token> getLexStream() throws LexerException {
        List<Token> tokens = new ArrayList<>();

        Token token = this.lex();

        while (token != null) {
            tokens.add(token);

            token = this.lex();
        }

        return tokens;
    }

    private boolean isAlphabetOrUnderscore(char ch) {
        return (ch >= 'a' && ch <= 'z') || (ch >= 'A' && ch <= 'Z') || ch == '_';
    }

    private boolean isDigit(char ch) {
        return ch >= '0' && ch <= '9';
    }

    private String scanNumber() {
        StringBuilder sb = new StringBuilder();

        while (this.pos < this.end) {
            char ch = this.text.charAt(this.pos);

            if (sb.length() == 0 && ch == '0') {
                this.pos++;
                return "0";
            } else if (this.isDigit(ch)) {
                sb.append(ch);
                this.pos++;
            } else {
                break;
            }
        }

        return sb.toString();
    }

    private String scanIdentifier() throws LexerException {
        StringBuilder sb = new StringBuilder();

        if (!this.isAlphabetOrUnderscore(text.charAt(this.pos))) {
            throw new LexerException("Identifier cannot start with a digit");
        }

        while (this.pos < this.end) {
            char ch = text.charAt(this.pos);

            if (this.isAlphabetOrUnderscore(ch) || this.isDigit(ch)) {
                sb.append(ch);
                this.pos++;
            } else {
                break;
            }
        }

        return sb.toString();
    }

    private boolean isAMatch(int start, String sequence) {
        int end = start + sequence.length();
        return start < this.end && end <= this.end
                && text.substring(start, end).equals(sequence);
    }

    private LinePosition getLinePosition() {
        return new LinePosition(this.lineNum, this.pos - this.lineStart);
    }

    private String error(String s) {
        return getLinePosition().toString() + " " + s;
    }

    private Map<String, TokenKind> generateKeywordMap() {
        Map<String, TokenKind> keywordMap = new HashMap<>();
        keywordMap.put("if", TokenKind.IfKeyword);
        keywordMap.put("else", TokenKind.ElseKeyword);
        keywordMap.put("while", TokenKind.WhileKeyword);
        keywordMap.put("true", TokenKind.TrueKeyword);
        keywordMap.put("false", TokenKind.FalseKeyword);
        keywordMap.put("bool", TokenKind.BoolKeyword);
        keywordMap.put("void", TokenKind.VoidKeyword);
        keywordMap.put("do", TokenKind.DoKeyword);
        keywordMap.put("int", TokenKind.IntKeyword);

        return keywordMap;
    }
}
