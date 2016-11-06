package org.bromano.cminusminus.parser;

import org.bromano.cminusminus.lexer.Token;
import org.bromano.cminusminus.lexer.TokenKind;
import org.bromano.cminusminus.nodes.Program;
import org.bromano.cminusminus.nodes.declarations.*;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.expressions.LocationExpression;
import org.bromano.cminusminus.nodes.statements.*;
import org.bromano.cminusminus.parser.parslets.*;

import java.util.*;

public class CMinusMinusParser implements Parser {
    private List<Token> tokens;
    private int pos;
    private int end;

    //Handles prefix and primaries
    private Map<TokenKind, PrefixParslet> prefixParslets;
    //Handles prefix and postfix
    private Map<TokenKind, InfixParslet> infixParslets;

    public CMinusMinusParser(List<Token> tokens) {
        this.setLexemeStream(tokens);

        this.prefixParslets = this.generatePrefixParsletMap();
        this.infixParslets = this.generateInfixParsletMap();
    }

    public CMinusMinusParser() {
        this.prefixParslets = this.generatePrefixParsletMap();
        this.infixParslets = this.generateInfixParsletMap();
    }

    @Override
    public void setLexemeStream(List<Token> tokens) {
        this.tokens = tokens;
        this.pos = 0;
        this.end = tokens.size();
    }

    public Token lookAhead(int x) {
        if (this.pos + x >= this.end) {
            return null;
        }

        return this.tokens.get(this.pos + x);
    }

    public boolean isAMatch(TokenKind kind) {
        return this.pos < this.end && this.tokens.get(this.pos).kind == kind;

    }

    public boolean isAMatch(TokenKind[] kinds) {
        if (this.pos >= this.end) {
            return false;
        }

        Token l = this.tokens.get(this.pos);

        for (TokenKind kind : kinds) {
            if (l.kind == kind) {
                return true;
            }
        }
        return false;
    }

    public boolean isAMatch(Token token, TokenKind kind) {
        return this.pos < this.end && token.kind == kind;
    }

    public Token match() throws ParserException {
        if (this.pos >= this.end) {
            throw new ParserException("Expected another token");
        }

        Token token = this.tokens.get(this.pos);

        this.pos++;

        return token;
    }

    public Token match(TokenKind kind) throws ParserException {
        if (this.pos >= this.end) {
            throw new ParserException("Expected a " + kind.name() + ", but received a EOF");
        }

        Token token = this.tokens.get(this.pos);

        if (this.tokens.get(this.pos).kind != kind) {
            throw new ParserException("Expected a " + kind.name() + ", but received a " + token.kind.name() + " at " + token.linePos);
        }

        this.pos++;

        return token;
    }

    public Token match(TokenKind[] kinds) throws ParserException {

        String kindString = Arrays.stream(kinds).map(TokenKind::name).reduce("[", (a, v) -> a + " " + v) + "]";

        if (this.pos >= this.end) {
            throw new ParserException("Expected a " + kindString + ", but received a EOF");
        }

        Token token = this.tokens.get(this.pos);

        boolean containsKind = Arrays.stream(kinds).anyMatch(x -> x == this.tokens.get(this.pos).kind);

        if (!containsKind) {
            throw new ParserException("Expected a " + kindString + ", but received a " + token.kind.name() + " at " + token.linePos);
        }

        this.pos++;

        return token;
    }

    @Override
    public Program parse() throws ParserException {
        Program program = new Program();

        program.declarations = new ArrayList<>();

        while (declarationPending()) {
            program.declarations.add(parseDeclaration());

        }

        if (pos < tokens.size()) {
            throw new ParserException("Unexpected token: " + tokens.get(pos));
        }

        return program;
    }

    private Declaration parseDeclaration() throws ParserException {
        if (variableDeclarationPending()) {
            return parseVariableDeclaration();
        }

        return parseFunctionDeclaration();
    }

    private FunctionDeclaration parseFunctionDeclaration() throws ParserException {
        FunctionDeclaration functionDeclaration = new FunctionDeclaration();

        this.match(TokenKind.VoidKeyword);
        functionDeclaration.name = this.match(TokenKind.Identifier);

        this.match(TokenKind.OpenParen);
        functionDeclaration.parameters = parseParameterList();
        this.match(TokenKind.CloseParen);

        functionDeclaration.blockStatement = parseBlockStatement();

        return functionDeclaration;
    }

    private List<Parameter> parseParameterList() throws ParserException {
        List<Parameter> parameters = new ArrayList<>();

        if (parameterPending()) {
            parameters.add(parseParameter());

            while (this.isAMatch(TokenKind.Comma)) {
                this.match();
                parameters.add(parseParameter());
            }
        }

        return parameters;
    }

    private Parameter parseParameter() throws ParserException {
        Parameter parameter = new Parameter();
        parameter.type = parseType();

        if (this.isAMatch(TokenKind.Ampersand)) {
            this.match();
            parameter.isAddr = true;
            parameter.name = this.match(TokenKind.Identifier);
        } else {
            parameter.name = this.match(TokenKind.Identifier);

            if (this.isAMatch(TokenKind.OpenBracket)) {
                this.match(TokenKind.OpenBracket);
                this.match(TokenKind.CloseBracket);
                parameter.isArray = true;
            }
        }

        return parameter;
    }

    private VariableDeclaration parseVariableDeclaration() throws ParserException {
        VariableDeclaration variableDeclaration = new VariableDeclaration();

        variableDeclaration.type = parseType();

        variableDeclaration.variables.add(this.parseVariable());

        while (this.isAMatch(TokenKind.Comma)) {
            this.match(TokenKind.Comma);
            variableDeclaration.variables.add(this.parseVariable());
        }

        this.match(TokenKind.Semicolon);

        return variableDeclaration;
    }

    private Variable parseVariable() throws ParserException {
        Variable variable = new Variable();
        variable.name = this.match(TokenKind.Identifier);

        if (this.isAMatch(TokenKind.OpenBracket)) {
            this.match(TokenKind.OpenBracket);
            variable.number = this.match(TokenKind.Number);
            this.match(TokenKind.CloseBracket);
        }

        return variable;
    }

    private Token parseType() throws ParserException {
        return this.match(new TokenKind[]{TokenKind.BoolKeyword, TokenKind.IntKeyword});
    }

    private BlockStatement parseBlockStatement() throws ParserException {
        this.match(TokenKind.OpenBrace);

        BlockStatement blockStatement = new BlockStatement();

        while (this.variableDeclarationPending()) {
            blockStatement.variableDeclarations.add(parseVariableDeclaration());
        }

        while (this.statementPending()) {
            blockStatement.statements.add(parseStatement());
        }

        this.match(TokenKind.CloseBrace);

        return blockStatement;
    }

    private int getPrecedence() {

        if (this.pos >= this.end) {
            return 0;
        }

        InfixParslet infix = this.infixParslets.getOrDefault(this.lookAhead(0).kind, null);

        return infix == null ? 0 :  infix.getPrecedence();
    }

    private Expression parseExpression() throws ParserException {

        return this.parseExpression(0);
    }

    public Expression parseExpression(int precedence) throws ParserException {
        Token token = this.match();

        PrefixParslet prefix = this.prefixParslets.getOrDefault(token.kind, null);

        if (prefix == null) {
            throw new ParserException("Unexpected token: " + token + " at line " + token.linePos);
        }

        Expression left = prefix.parse(this, token);

        while (precedence < this.getPrecedence()) {

            token = this.match();

            InfixParslet infix = this.infixParslets.get(token.kind);

            left = infix.parse(this, left, token);
        }

        return left;
    }

    // Note: Ambiguity between function call and assignment statements.
    public Statement parseStatement() throws ParserException {
        if (this.blockStatementPending()) {

            return this.parseBlockStatement();

        } else if (this.ifStatementPending()) {

            return this.parseIfStatement();

        } else if (this.whileStatementPending()) {

            return this.parseWhileStatement();

        } else if (this.doStatementPending()) {

            return this.parseDoStatement();

        } else if (this.functionCallStatementPending() && this.isAMatch(this.lookAhead(1), TokenKind.OpenParen)) {

            return this.parseFunctionCallStatement();

        } else if (this.assignmentStatementPending()) {

            return this.parseAssignmentStatement();

        }

        throw new ParserException("Expected a statement at line " + this.lookAhead(0).linePos);
    }

    private IfStatement parseIfStatement() throws ParserException {
        IfStatement ifStatement = new IfStatement();

        this.match(TokenKind.IfKeyword);
        this.match(TokenKind.OpenParen);
        ifStatement.conditional = parseExpression();
        this.match(TokenKind.CloseParen);

        ifStatement.statement = parseStatement();

        if (this.isAMatch(TokenKind.ElseKeyword)) {
            this.match();
            ifStatement.elseStatement = parseStatement();
        }

        return ifStatement;
    }

    private WhileStatement parseWhileStatement() throws ParserException {
        WhileStatement whileStatement = new WhileStatement();

        this.match(TokenKind.WhileKeyword);
        this.match(TokenKind.OpenParen);
        whileStatement.conditional = parseExpression();
        this.match(TokenKind.CloseParen);
        whileStatement.statement = parseStatement();

        return whileStatement;
    }

    private DoStatement parseDoStatement() throws ParserException {
        DoStatement doStatement = new DoStatement();

        this.match(TokenKind.DoKeyword);
        doStatement.statement = parseStatement();
        this.match(TokenKind.WhileKeyword);
        this.match(TokenKind.OpenParen);
        doStatement.conditional = parseExpression();
        this.match(TokenKind.CloseParen);
        this.match(TokenKind.Semicolon);

        return doStatement;
    }

    private AssignmentStatement parseAssignmentStatement() throws ParserException {
        AssignmentStatement assignmentStatement = new AssignmentStatement();

        assignmentStatement.locationExpression = parseLocation();

        this.match(TokenKind.Equals);
        assignmentStatement.expression = parseExpression();
        this.match(TokenKind.Semicolon);

        return assignmentStatement;
    }

    private LocationExpression parseLocation() throws ParserException {
        LocationExpression location = new LocationExpression();
        location.name = this.match(TokenKind.Identifier);

        if (this.isAMatch(TokenKind.OpenBracket)) {
            this.match();
            location.expression = parseExpression();
            this.match(TokenKind.CloseBracket);
        }

        return location;
    }

    private FunctionCallStatement parseFunctionCallStatement() throws ParserException {
        FunctionCallStatement functionCallStatement = new FunctionCallStatement();
        functionCallStatement.name = this.match(TokenKind.Identifier);
        this.match(TokenKind.OpenParen);

        if (this.expressionPending()) {
            functionCallStatement.arguments.add(parseExpression());

            while (this.isAMatch(TokenKind.Comma)) {
                this.match();
                functionCallStatement.arguments.add(parseExpression());
            }
        }

        this.match(TokenKind.CloseParen);
        this.match(TokenKind.Semicolon);

        return functionCallStatement;
    }

    private boolean declarationPending() {
        return variableDeclarationPending() || functionDeclarationPending();
    }

    private boolean variableDeclarationPending() {
        return this.isAMatch(new TokenKind[]{TokenKind.IntKeyword, TokenKind.BoolKeyword});
    }

    private boolean functionDeclarationPending() {
        return this.isAMatch(TokenKind.VoidKeyword);
    }

    private boolean typePending() throws ParserException {
        return this.isAMatch(new TokenKind[]{TokenKind.BoolKeyword, TokenKind.IntKeyword});
    }

    private boolean parameterPending() throws ParserException {
        return this.typePending();
    }

    private boolean statementPending() throws ParserException {
        return this.blockStatementPending() ||
                this.ifStatementPending() ||
                this.whileStatementPending() ||
                this.doStatementPending() ||
                this.assignmentStatementPending() ||
                this.functionCallStatementPending();
    }

    private boolean blockStatementPending() {
        return this.isAMatch(TokenKind.OpenBrace);
    }

    private boolean ifStatementPending() {
        return this.isAMatch(TokenKind.IfKeyword);
    }

    private boolean doStatementPending() {
        return this.isAMatch(TokenKind.DoKeyword);
    }

    private boolean whileStatementPending() {
        return this.isAMatch(TokenKind.WhileKeyword);
    }

    private boolean functionCallStatementPending() {
        return this.isAMatch(TokenKind.Identifier);
    }

    private boolean assignmentStatementPending() {
        return this.locationPending();
    }

    private boolean locationPending() {
        return this.isAMatch(TokenKind.Identifier);
    }

    private boolean expressionPending() {
        return this.locationPending() ||
                this.isAMatch(new TokenKind[]{
                        TokenKind.Number,
                        TokenKind.TrueKeyword,
                        TokenKind.FalseKeyword,
                        TokenKind.OpenParen,
                        TokenKind.Exclamation,
                        TokenKind.Minus,
                        TokenKind.Plus
                });
    }

    private Map<TokenKind, PrefixParslet> generatePrefixParsletMap() {
        Map<TokenKind, PrefixParslet> map = new HashMap<>();

        map.put(TokenKind.Exclamation, new PrefixOperatorParslet(Precedence.PREFIX));
        map.put(TokenKind.Minus, new PrefixOperatorParslet(Precedence.PREFIX));
        map.put(TokenKind.Plus, new PrefixOperatorParslet(Precedence.PREFIX));

        map.put(TokenKind.Identifier, new LocationParslet());
        map.put(TokenKind.Number, new NumberParslet());

        map.put(TokenKind.TrueKeyword, new BooleanParslet());
        map.put(TokenKind.FalseKeyword, new BooleanParslet());

        map.put(TokenKind.OpenParen, new GroupParslet());

        return map;
    }

    private Map<TokenKind, InfixParslet> generateInfixParsletMap() {
        Map<TokenKind, InfixParslet> map = new HashMap<>();

        map.put(TokenKind.BarBar, new BinaryOperatorParslet(Precedence.LOGICALOR, false));
        map.put(TokenKind.AmpersandAmpersand, new BinaryOperatorParslet(Precedence.LOGICALAND, false));

        map.put(TokenKind.EqualsEquals, new BinaryOperatorParslet(Precedence.EQUALITY, false));
        map.put(TokenKind.ExclamationEquals, new BinaryOperatorParslet(Precedence.EQUALITY, false));

        map.put(TokenKind.LessThan, new BinaryOperatorParslet(Precedence.RELATIONAL, false));
        map.put(TokenKind.LessThanEquals, new BinaryOperatorParslet(Precedence.RELATIONAL, false));
        map.put(TokenKind.GreaterThan, new BinaryOperatorParslet(Precedence.RELATIONAL, false));
        map.put(TokenKind.GreaterThanEquals, new BinaryOperatorParslet(Precedence.RELATIONAL, false));

        map.put(TokenKind.Plus, new BinaryOperatorParslet(Precedence.ADDITIVE, false));
        map.put(TokenKind.Minus, new BinaryOperatorParslet(Precedence.ADDITIVE, false));

        map.put(TokenKind.Asterisk, new BinaryOperatorParslet(Precedence.MULTIPLICATIVE, false));
        map.put(TokenKind.Slash, new BinaryOperatorParslet(Precedence.MULTIPLICATIVE, false));
        map.put(TokenKind.Percent, new BinaryOperatorParslet(Precedence.MULTIPLICATIVE, false));

        return map;
    }
}