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
    private Map<String, LexemeKind> keywordMap;

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
    public Lexeme lex() throws LexerException {

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
                        return new Lexeme(LexemeKind.ExclamationEquals, "!=", linePos);
                    }

                    return new Lexeme(LexemeKind.Exclamation, "!", linePos);
                case '%':
                    linePos = getLinePosition();
                    this.pos++;

                    return new Lexeme(LexemeKind.Percent, "%", linePos);
                case '&':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "&")) {
                        this.pos++;
                        return new Lexeme(LexemeKind.AmpersandAmpersand, "&&", linePos);
                    }

                    return new Lexeme(LexemeKind.Ampersand, "&", linePos);
                case '(':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.OpenParen, linePos);
                case ')':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.CloseParen, linePos);
                case '*':
                    linePos = getLinePosition();
                    this.pos++;

                    return new Lexeme(LexemeKind.Asterisk, "*", linePos);
                case '+':
                    linePos = getLinePosition();
                    this.pos++;

                    return new Lexeme(LexemeKind.Plus, "+", linePos);
                case ',':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.Comma, ",", linePos);
                case '-':
                    linePos = getLinePosition();
                    this.pos++;

                    return new Lexeme(LexemeKind.Minus, "-", linePos);
                case '.':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.Dot, ".", linePos);
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

                    return new Lexeme(LexemeKind.Slash, "/", linePos);
                case ';':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.Semicolon, ";", linePos);
                case '<':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "=")) {
                        this.pos++;
                        return new Lexeme(LexemeKind.LessThanEquals, "<=", linePos);
                    }

                    return new Lexeme(LexemeKind.LessThan, "<", linePos);
                case '=':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "=")) {
                        this.pos++;
                        return new Lexeme(LexemeKind.EqualsEquals, "==", linePos);
                    } else if (this.isAMatch(this.pos, ">")) {
                        this.pos++;
                        return new Lexeme(LexemeKind.EqualsGreaterThan, "=>", linePos);
                    }

                    return new Lexeme(LexemeKind.Equals, "=", linePos);
                case '>':
                    linePos = getLinePosition();
                    this.pos++;

                    if (isAMatch(this.pos, "=")) {
                        this.pos++;
                        return new Lexeme(LexemeKind.GreaterThanEquals, ">=", linePos);
                    }

                    return new Lexeme(LexemeKind.GreaterThan, ">", linePos);
                case '[':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.OpenBracket, linePos);
                case ']':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.CloseBracket, linePos);
                case '_':
                    linePos = getLinePosition();
                    value = this.scanIdentifier();
                    return new Lexeme(LexemeKind.Identifier, value, linePos);
                case '{':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.OpenBrace, linePos);
                case '}':
                    linePos = getLinePosition();
                    this.pos++;
                    return new Lexeme(LexemeKind.CloseBrace, linePos);
                case '|':
                    linePos = getLinePosition();
                    this.pos++;

                    if (!isAMatch(this.pos, "|")) {
                        throw new LexerException(error("Expected a |"));
                    }

                    this.pos++;
                    return new Lexeme(LexemeKind.BarBar, "||", linePos);
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
                    return new Lexeme(LexemeKind.Number, value, linePos);
                default:
                    linePos = getLinePosition();

                    if (this.isAlphabetOrUnderscore(ch)) {

                        String identifierOrKeyword = this.scanIdentifier();

                        if (this.keywordMap.containsKey(identifierOrKeyword.trim())) {
                            return new Lexeme(keywordMap.get(identifierOrKeyword.trim()), linePos);
                        }

                        return new Lexeme(LexemeKind.Identifier, identifierOrKeyword, linePos);
                    }

                    throw new LexerException(error("Char " + ch + " cannot be lexed"));
            }
        }
    }

    @Override
    public List<Lexeme> getLexStream() throws LexerException {
        List<Lexeme> lexemes = new ArrayList<>();

        Lexeme lexeme = this.lex();

        while (lexeme != null) {
            lexemes.add(lexeme);

            lexeme = this.lex();
        }

        return lexemes;
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

    private Map<String, LexemeKind> generateKeywordMap() {
        Map<String, LexemeKind> keywordMap = new HashMap<>();
        keywordMap.put("if", LexemeKind.IfKeyword);
        keywordMap.put("else", LexemeKind.ElseKeyword);
        keywordMap.put("while", LexemeKind.WhileKeyword);
        keywordMap.put("true", LexemeKind.TrueKeyword);
        keywordMap.put("false", LexemeKind.FalseKeyword);
        keywordMap.put("bool", LexemeKind.BoolKeyword);
        keywordMap.put("void", LexemeKind.VoidKeyword);
        keywordMap.put("do", LexemeKind.DoKeyword);
        keywordMap.put("int", LexemeKind.IntKeyword);

        return keywordMap;
    }
}
