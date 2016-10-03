package org.bromano.cminusminus.parser;

import org.bromano.cminusminus.lexer.Token;
import org.bromano.cminusminus.lexer.TokenKind;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.Program;
import org.bromano.cminusminus.nodes.statements.Statement;

import java.util.List;

public interface Parser {
    void setLexemeStream(List<Token> tokens);
    Program parse() throws ParserException;
    Expression parseExpression(int precedence) throws ParserException;
    Statement parseStatement() throws ParserException;
    Token match(TokenKind kind) throws ParserException;
    Token match(TokenKind[] kinds) throws ParserException;
    Token match() throws ParserException;
    boolean isAMatch(TokenKind kind);
    boolean isAMatch(TokenKind[] kinds);
    boolean isAMatch(Token token, TokenKind kind);
    Token lookAhead(int x);
}
