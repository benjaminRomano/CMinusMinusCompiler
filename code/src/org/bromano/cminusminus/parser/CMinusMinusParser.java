package org.bromano.cminusminus.parser;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.lexer.LexemeKind;
import org.bromano.cminusminus.nodes.Program;
import org.bromano.cminusminus.nodes.declarations.*;
import org.bromano.cminusminus.nodes.expressions.Expression;
import org.bromano.cminusminus.nodes.statements.*;
import org.bromano.cminusminus.parser.parslets.*;

import java.util.*;

public class CMinusMinusParser implements Parser {
    private List<Lexeme> lexemes;
    private int pos;
    private int end;

    //Handles prefix and primaries
    private Map<LexemeKind, PrefixParslet> prefixParslets;
    //Handles prefix and postfix
    private Map<LexemeKind, InfixParslet> infixParslets;

    public CMinusMinusParser(List<Lexeme> lexemes) {
        this.setLexemeStream(lexemes);

        this.prefixParslets = this.generatePrefixParsletMap();
        this.infixParslets = this.generateInfixParsletMap();
    }

    public CMinusMinusParser() {
        this.prefixParslets = this.generatePrefixParsletMap();
        this.infixParslets = this.generateInfixParsletMap();
    }

    @Override
    public void setLexemeStream(List<Lexeme> lexemes) {
        this.lexemes = lexemes;
        this.pos = 0;
        this.end = lexemes.size();
    }

    public Lexeme lookAhead(int x) {
        if (this.pos + x >= this.end) {
            return null;
        }

        return this.lexemes.get(this.pos + x);
    }

    public boolean isAMatch(LexemeKind kind) {
        return this.pos < this.end && this.lexemes.get(this.pos).kind == kind;

    }

    public boolean isAMatch(LexemeKind[] kinds) {
        if (this.pos >= this.end) {
            return false;
        }

        Lexeme l = this.lexemes.get(this.pos);

        for (LexemeKind kind : kinds) {
            if (l.kind == kind) {
                return true;
            }
        }
        return false;
    }

    public boolean isAMatch(Lexeme lexeme, LexemeKind kind) {
        return this.pos < this.end && lexeme.kind == kind;
    }

    public Lexeme match() throws ParserException {
        if (this.pos >= this.end) {
            throw new ParserException("Expected another lexeme");
        }

        Lexeme lexeme = this.lexemes.get(this.pos);

        this.pos++;

        return lexeme;
    }

    public Lexeme match(LexemeKind kind) throws ParserException {
        if (this.pos >= this.end) {
            throw new ParserException("Expected a " + kind.name() + ", but received a EOF");
        }

        Lexeme lexeme = this.lexemes.get(this.pos);

        if (this.lexemes.get(this.pos).kind != kind) {
            throw new ParserException("Expected a " + kind.name() + ", but received a " + lexeme.kind.name() + " at " + lexeme.linePos);
        }

        this.pos++;

        return lexeme;
    }

    public Lexeme match(LexemeKind[] kinds) throws ParserException {

        String kindString = Arrays.stream(kinds).map(LexemeKind::name).reduce("[", (a, v) -> a + " " + v) + "]";

        if (this.pos >= this.end) {
            throw new ParserException("Expected a " + kindString + ", but received a EOF");
        }

        Lexeme lexeme = this.lexemes.get(this.pos);

        boolean containsKind = Arrays.stream(kinds).anyMatch(x -> x == this.lexemes.get(this.pos).kind);

        if (!containsKind) {
            throw new ParserException("Expected a " + kindString + ", but received a " + lexeme.kind.name() + " at " + lexeme.linePos);
        }

        this.pos++;

        return lexeme;
    }

    @Override
    public Program parse() throws ParserException {
        Program program = new Program();

        program.declarations = new ArrayList<>();

        while (declarationPending()) {
            program.declarations.add(parseDeclaration());

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

        this.match(LexemeKind.VoidKeyword);
        functionDeclaration.name = this.match(LexemeKind.Identifier);

        this.match(LexemeKind.OpenParen);
        functionDeclaration.parameters = parseParameterList();
        this.match(LexemeKind.CloseParen);

        functionDeclaration.blockStatement = parseBlockStatement();

        return functionDeclaration;
    }

    private List<Parameter> parseParameterList() throws ParserException {
        List<Parameter> parameters = new ArrayList<>();

        if (parameterPending()) {
            parameters.add(parseParameter());

            while (this.isAMatch(LexemeKind.Comma)) {
                this.match();
                parameters.add(parseParameter());
            }
        }

        return parameters;
    }

    private Parameter parseParameter() throws ParserException {
        Parameter parameter = new Parameter();
        parameter.type = parseType();

        if (this.isAMatch(LexemeKind.Ampersand)) {
            this.match();
            parameter.isAddr = true;
            parameter.name = this.match(LexemeKind.Identifier);
        } else {
            parameter.name = this.match(LexemeKind.Identifier);

            if (this.isAMatch(LexemeKind.OpenBracket)) {
                this.match(LexemeKind.OpenBracket);
                parameter.number = this.match(LexemeKind.Number);
                this.match(LexemeKind.CloseBracket);
            }
        }

        return parameter;
    }

    private VariableDeclaration parseVariableDeclaration() throws ParserException {
        VariableDeclaration variableDeclaration = new VariableDeclaration();

        variableDeclaration.type = parseType();

        variableDeclaration.variables.add(this.parseVariable());

        while (this.isAMatch(LexemeKind.Comma)) {
            this.match(LexemeKind.Comma);
            variableDeclaration.variables.add(this.parseVariable());
        }

        this.match(LexemeKind.Semicolon);

        return variableDeclaration;
    }

    private Variable parseVariable() throws ParserException {
        Variable variable = new Variable();
        variable.name = this.match(LexemeKind.Identifier);

        if (this.isAMatch(LexemeKind.OpenBracket)) {
            this.match(LexemeKind.OpenBracket);
            variable.number = this.match(LexemeKind.Number);
            this.match(LexemeKind.CloseBracket);
        }

        return variable;
    }

    private Lexeme parseType() throws ParserException {
        return this.match(new LexemeKind[]{LexemeKind.BoolKeyword, LexemeKind.IntKeyword});
    }

    private BlockStatement parseBlockStatement() throws ParserException {
        this.match(LexemeKind.OpenBrace);

        BlockStatement blockStatement = new BlockStatement();

        while (this.variableDeclarationPending()) {
            blockStatement.variableDeclarations.add(parseVariableDeclaration());
        }

        while (this.statementPending()) {
            blockStatement.statements.add(parseStatement());
        }

        this.match(LexemeKind.CloseBrace);

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
        Lexeme lexeme = this.match();

        PrefixParslet prefix = this.prefixParslets.getOrDefault(lexeme.kind, null);

        if (prefix == null) {
            throw new ParserException("Unexpected token: " + lexeme + " at line " + lexeme.linePos);
        }

        Expression left = prefix.parse(this, lexeme);

        while (precedence < this.getPrecedence()) {

            lexeme = this.match();

            InfixParslet infix = this.infixParslets.get(lexeme.kind);

            left = infix.parse(this, left, lexeme);
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

        } else if (this.functionCallStatementPending() && this.isAMatch(this.lookAhead(1), LexemeKind.OpenParen)) {

            return this.parseFunctionCallStatement();

        } else if (this.assignmentStatementPending()) {

            return this.parseAssignmentStatement();

        }

        throw new ParserException("Expected a statement at line " + this.lookAhead(0).linePos);
    }

    private IfStatement parseIfStatement() throws ParserException {
        IfStatement ifStatement = new IfStatement();

        this.match(LexemeKind.IfKeyword);
        this.match(LexemeKind.OpenParen);
        ifStatement.conditional = parseExpression();
        this.match(LexemeKind.CloseParen);

        ifStatement.statement = parseStatement();

        if (this.isAMatch(LexemeKind.ElseKeyword)) {
            this.match();
            ifStatement.elseStatement = parseStatement();
        }

        return ifStatement;
    }

    private WhileStatement parseWhileStatement() throws ParserException {
        WhileStatement whileStatement = new WhileStatement();

        this.match(LexemeKind.WhileKeyword);
        this.match(LexemeKind.OpenParen);
        whileStatement.conditional = parseExpression();
        this.match(LexemeKind.CloseParen);
        whileStatement.statement = parseStatement();

        return whileStatement;
    }

    private DoStatement parseDoStatement() throws ParserException {
        DoStatement doStatement = new DoStatement();

        this.match(LexemeKind.DoKeyword);
        doStatement.statement = parseStatement();
        this.match(LexemeKind.WhileKeyword);
        this.match(LexemeKind.OpenParen);
        doStatement.conditional = parseExpression();
        this.match(LexemeKind.CloseParen);
        this.match(LexemeKind.Semicolon);

        return doStatement;
    }

    private AssignmentStatement parseAssignmentStatement() throws ParserException {
        AssignmentStatement assignmentStatement = new AssignmentStatement();

        assignmentStatement.location = parseLocation();

        this.match(LexemeKind.Equals);
        assignmentStatement.expression = parseExpression();
        this.match(LexemeKind.Semicolon);

        return assignmentStatement;
    }

    private Location parseLocation() throws ParserException {
        Location location = new Location();
        location.name = this.match(LexemeKind.Identifier);

        if (this.isAMatch(LexemeKind.OpenBracket)) {
            this.match();
            location.expression = parseExpression();
            this.match(LexemeKind.CloseBracket);
        }

        return location;
    }

    private FunctionCallStatement parseFunctionCallStatement() throws ParserException {
        FunctionCallStatement functionCallStatement = new FunctionCallStatement();
        functionCallStatement.name = this.match(LexemeKind.Identifier);
        this.match(LexemeKind.OpenParen);

        if (this.expressionPending()) {
            functionCallStatement.arguments.add(parseExpression());

            while (this.isAMatch(LexemeKind.Comma)) {
                this.match();
                functionCallStatement.arguments.add(parseExpression());
            }
        }

        this.match(LexemeKind.CloseParen);
        this.match(LexemeKind.Semicolon);

        return functionCallStatement;
    }

    private boolean declarationPending() {
        return variableDeclarationPending() || functionDeclarationPending();
    }

    private boolean variableDeclarationPending() {
        return this.isAMatch(new LexemeKind[]{LexemeKind.IntKeyword, LexemeKind.BoolKeyword});
    }

    private boolean functionDeclarationPending() {
        return this.isAMatch(LexemeKind.VoidKeyword);
    }

    private boolean typePending() throws ParserException {
        return this.isAMatch(new LexemeKind[]{LexemeKind.BoolKeyword, LexemeKind.IntKeyword});
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
        return this.isAMatch(LexemeKind.OpenBrace);
    }

    private boolean ifStatementPending() {
        return this.isAMatch(LexemeKind.IfKeyword);
    }

    private boolean doStatementPending() {
        return this.isAMatch(LexemeKind.DoKeyword);
    }

    private boolean whileStatementPending() {
        return this.isAMatch(LexemeKind.WhileKeyword);
    }

    private boolean functionCallStatementPending() {
        return this.isAMatch(LexemeKind.Identifier);
    }

    private boolean assignmentStatementPending() {
        return this.locationPending();
    }

    private boolean locationPending() {
        return this.isAMatch(LexemeKind.Identifier);
    }

    private boolean expressionPending() {
        return this.locationPending() ||
                this.isAMatch(new LexemeKind[]{
                        LexemeKind.Number,
                        LexemeKind.TrueKeyword,
                        LexemeKind.FalseKeyword,
                        LexemeKind.OpenParen,
                        LexemeKind.Exclamation,
                        LexemeKind.Minus,
                        LexemeKind.Plus
                });
    }

    private Map<LexemeKind, PrefixParslet> generatePrefixParsletMap() {
        Map<LexemeKind, PrefixParslet> map = new HashMap<>();

        map.put(LexemeKind.Exclamation, new PrefixOperatorParslet(Precedence.PREFIX));
        map.put(LexemeKind.Minus, new PrefixOperatorParslet(Precedence.PREFIX));
        map.put(LexemeKind.Plus, new PrefixOperatorParslet(Precedence.PREFIX));

        map.put(LexemeKind.Identifier, new LocationParslet());
        map.put(LexemeKind.Number, new NumberParslet());

        map.put(LexemeKind.TrueKeyword, new BooleanParslet());
        map.put(LexemeKind.FalseKeyword, new BooleanParslet());

        map.put(LexemeKind.OpenParen, new GroupParslet());

        return map;
    }

    private Map<LexemeKind, InfixParslet> generateInfixParsletMap() {
        Map<LexemeKind, InfixParslet> map = new HashMap<>();

        map.put(LexemeKind.BarBar, new BinaryOperatorParslet(Precedence.LOGICALOR, false));
        map.put(LexemeKind.AmpersandAmpersand, new BinaryOperatorParslet(Precedence.LOGICALAND, false));

        map.put(LexemeKind.EqualsEquals, new BinaryOperatorParslet(Precedence.EQUALITY, false));
        map.put(LexemeKind.ExclamationEquals, new BinaryOperatorParslet(Precedence.EQUALITY, false));

        map.put(LexemeKind.LessThan, new BinaryOperatorParslet(Precedence.RELATIONAL, false));
        map.put(LexemeKind.LessThanEquals, new BinaryOperatorParslet(Precedence.RELATIONAL, false));
        map.put(LexemeKind.GreaterThan, new BinaryOperatorParslet(Precedence.RELATIONAL, false));
        map.put(LexemeKind.GreaterThanEquals, new BinaryOperatorParslet(Precedence.RELATIONAL, false));

        map.put(LexemeKind.Plus, new BinaryOperatorParslet(Precedence.ADDITIVE, false));
        map.put(LexemeKind.Minus, new BinaryOperatorParslet(Precedence.ADDITIVE, false));

        map.put(LexemeKind.Asterisk, new BinaryOperatorParslet(Precedence.MULTIPLICATIVE, false));
        map.put(LexemeKind.Slash, new BinaryOperatorParslet(Precedence.MULTIPLICATIVE, false));
        map.put(LexemeKind.Percent, new BinaryOperatorParslet(Precedence.MULTIPLICATIVE, false));

        return map;
    }
}