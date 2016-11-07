package org.bromano.cminusminus.generator;

import org.bromano.cminusminus.checker.CheckerException;
import org.bromano.cminusminus.lexer.TokenKind;
import org.bromano.cminusminus.nodes.Program;
import org.bromano.cminusminus.nodes.declarations.FunctionDeclaration;
import org.bromano.cminusminus.nodes.declarations.Variable;
import org.bromano.cminusminus.nodes.declarations.VariableDeclaration;
import org.bromano.cminusminus.nodes.expressions.*;
import org.bromano.cminusminus.nodes.statements.*;
import org.bromano.cminusminus.symbols.Location;
import org.bromano.cminusminus.symbols.Symbol;
import org.bromano.cminusminus.symbols.SymbolTable;
import org.bromano.cminusminus.types.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;
import java.util.stream.Collectors;

public class Generator {
    private SymbolTable symbolTable;
    private int labelValue;
    private int level;
    private int displacement;
    private Stack<Integer> previousDisplacement;
    private List<Instruction> instructions;

    public List<Instruction> generate(Program program) throws GeneratorException, CheckerException {
        this.instructions = new ArrayList<>();
        this.previousDisplacement = new Stack<>();
        this.level = 0;
        this.labelValue = 1;
        this.displacement = 1;
        this.symbolTable = program.symbolTable;

        emit(new Instruction(OpCode.INIT));

        int size = 0;
        List<VariableDeclaration> variableDeclarations = program.declarations.stream()
                .filter(d -> d instanceof VariableDeclaration)
                .map(v -> (VariableDeclaration) v)
                .collect(Collectors.toList());

        for (VariableDeclaration variableDeclaration : variableDeclarations) {
            size += this.generateVariableDeclaration(variableDeclaration);
        }

        String label = generateLabel();

        if (size != 0) {
            emit(new Instruction(OpCode.ALLOC, size));
        }

        emit(new Instruction(OpCode.JMP, label));


        List<FunctionDeclaration> functionDeclarations = program.declarations.stream()
                .filter(d -> d instanceof FunctionDeclaration)
                .map(f -> (FunctionDeclaration) f)
                .collect(Collectors.toList());

        for (FunctionDeclaration functionDeclaration : functionDeclarations) {
            this.generateFunctionDeclaration(functionDeclaration);
        }

        emit(new Instruction(label, OpCode.NOP));
        emit(new Instruction(symbolTable.getVariable("main").label, OpCode.CALL));

        if (size != 0) {
            emit(new Instruction(OpCode.ALLOC, -size));
        }

        emit(new Instruction(OpCode.HALT));

        return this.instructions;
    }

    private int generateVariableDeclaration(VariableDeclaration variableDeclaration) throws CheckerException {
        int size = 0;

        for (Variable variable : variableDeclaration.variables) {
            Symbol symbol = symbolTable.getVariable(variable.name.value);
            int variableSize = 0;

            if (symbol.getTypeKind() == TypeKind.Array) {
                ArrayType arrayType = (ArrayType) symbol.getType();
                variableSize += arrayType.getSize();
            } else {
                variableSize = 1;
            }

            symbol.location = new Location(this.level, this.displacement);

            this.displacement += variableSize;
            size += variableSize;
        }

        return size;
    }

    private void generateFunctionDeclaration(FunctionDeclaration functionDeclaration) throws GeneratorException, CheckerException {
        this.incrementLevel();
        this.symbolTable = functionDeclaration.symbolTable;

        Symbol symbol = this.symbolTable.getVariable(functionDeclaration.name.value);
        FunctionType functionType = (FunctionType) symbol.getType();

        // -2 because after arguments is the return address and then the link data
        for (int i = 0; i < functionType.fields.size(); i++) {
            Symbol argumentSymbol = symbolTable.getVariable(functionType.fields.get(i).getName());
            argumentSymbol.location  = new Location(this.level, i - 2);
        }

        String label = generateLabel();

        symbol.label = label;
        emit(new Instruction(label, OpCode.PROC, this.level));

        this.generateBlockStatement(functionDeclaration.blockStatement, false);

        this.emit(new Instruction(OpCode.RET, this.level));

        this.decrementLevel();
        this.symbolTable = this.symbolTable.parent;
    }

    private void generateBlockStatement(BlockStatement blockStatement, boolean isNewLevel) throws GeneratorException, CheckerException {
        if (isNewLevel) {
            this.incrementLevel();
            this.symbolTable = blockStatement.symbolTable;
            this.emit(new Instruction(null, OpCode.ENTER, this.level));
        }

        int size = 0;
        for (VariableDeclaration variableDeclaration : blockStatement.variableDeclarations) {
            size += generateVariableDeclaration(variableDeclaration);
        }

        if (size != 0) {
            emit(new Instruction(OpCode.ALLOC, size));
        }

        for (Statement statement : blockStatement.statements) {
            this.generateStatement(statement);
        }

        if (size != 0) {
            emit(new Instruction(OpCode.ALLOC, -size));
        }

        if (isNewLevel) {
            this.emit(new Instruction(OpCode.EXIT, this.level));
            this.decrementLevel();
            this.symbolTable = this.symbolTable.parent;
        }
    }


    private void generateStatement(Statement statement) throws CheckerException, GeneratorException {
        if (statement instanceof BlockStatement) {
            generateBlockStatement((BlockStatement) statement, true);
        } else if (statement instanceof IfStatement) {
            generateIfStatement((IfStatement) statement);
        } else if (statement instanceof WhileStatement) {
            generateWhileStatement((WhileStatement) statement);
        } else if (statement instanceof DoStatement) {
            generateDoStatement((DoStatement) statement);
        } else if (statement instanceof FunctionCallStatement) {
            generateFunctionCallStatement((FunctionCallStatement) statement);
        } else {
            generateAssignmentStatement((AssignmentStatement) statement);
        }
    }

    private void generateScanFunctionCall(LocationExpression locationExpression) throws CheckerException, GeneratorException {
        Symbol symbol = this.symbolTable.getVariable(locationExpression.name.value);

        if (symbol.getTypeKind() == TypeKind.Array) {
            if (symbol.isReference()) {
                emit(new Instruction(OpCode.LDV, symbol.location));
            } else {
                emit(new Instruction(OpCode.LDV, symbol.location));
            }

            generateExpression(locationExpression.expression);

            emit(new Instruction(OpCode.ADD));
            emit(new Instruction(OpCode.IN));
            emit(new Instruction(OpCode.STL));
        } else {
            emit(new Instruction(OpCode.IN));

            if (symbol.isReference()) {
                emit(new Instruction(OpCode.STI, symbol.location));
            } else {
                emit(new Instruction(OpCode.ST, symbol.location));
            }
        }
    }

    private void generateAssignmentStatement(AssignmentStatement assignmentStatement) throws CheckerException, GeneratorException {
        Symbol symbol = this.symbolTable.getVariable(assignmentStatement.locationExpression.name.value);

        if (symbol.getTypeKind() == TypeKind.Array) {
            if (symbol.isReference()) {
                emit(new Instruction(OpCode.LDV, symbol.location));
            } else {
                emit(new Instruction(OpCode.LDV, symbol.location));
            }

            generateExpression(assignmentStatement.locationExpression.expression);

            emit(new Instruction(OpCode.ADD));
            generateExpression(assignmentStatement.expression);
            emit(new Instruction(OpCode.STL));
        } else {
            generateExpression(assignmentStatement.expression);

            if (symbol.isReference()) {
                emit(new Instruction(OpCode.STI, symbol.location));
            } else {
                emit(new Instruction(OpCode.ST, symbol.location));
            }
        }
    }

    private void generateDoStatement(DoStatement doStatement) throws GeneratorException, CheckerException {
        String startLabel = generateLabel();

        emit(new Instruction(startLabel, OpCode.NOP));
        generateStatement(doStatement.statement);
        generateExpression(doStatement.conditional);
        emit(new Instruction(OpCode.JMPT, startLabel));
    }

    private void generateWhileStatement(WhileStatement whileStatement) throws GeneratorException, CheckerException {
        String conditionalLabel = generateLabel();
        String endLabel = generateLabel();

        emit(new Instruction(conditionalLabel, OpCode.NOP));
        generateExpression(whileStatement.conditional);
        emit(new Instruction(OpCode.JMPF, endLabel));
        generateStatement(whileStatement.statement);
        emit(new Instruction(OpCode.JMP, conditionalLabel));
        emit(new Instruction(endLabel, OpCode.NOP));
    }

    private void generateFunctionCallStatement(FunctionCallStatement functionCallStatement) throws CheckerException, GeneratorException {
        Symbol symbol = this.symbolTable.getVariable(functionCallStatement.name.value);
        FunctionType functionType = (FunctionType) symbol.getType();

        if (functionCallStatement.name.value.equals("scan")) {
            LocationExpression locationExpression = (LocationExpression) functionCallStatement.arguments.get(0);
            generateScanFunctionCall(locationExpression);
            return;
        } else if (functionCallStatement.name.value.equals("print")) {
            generateExpression(functionCallStatement.arguments.get(0));
            emit(new Instruction(OpCode.OUT));
            return;
        }

        for (int i  = 0; i < functionType.fields.size(); i++) {
            FieldType field = functionType.fields.get(i);
            Expression expression = functionCallStatement.arguments.get(i);

            if (field.isReference() || field.getTypeKind() == TypeKind.Array) {
                generateLocationExpressionAddress((LocationExpression) expression);
            } else {
                generateExpression(expression);
            }
        }

        emit(new Instruction(OpCode.CALL, symbol.label));
    }

    private void generateIfStatement(IfStatement ifStatement) throws GeneratorException, CheckerException {
        generateExpression(ifStatement.conditional);

        if (ifStatement.elseStatement == null) {
            String label = generateLabel();
            emit(new Instruction(OpCode.JMPF, label));
            generateStatement(ifStatement.statement);
            emit(new Instruction(label, OpCode.NOP));
        } else {
            String elseLabel = generateLabel();
            String endLabel = generateLabel();
            emit(new Instruction(OpCode.JMPF, elseLabel));
            generateStatement(ifStatement.statement);
            emit(new Instruction(elseLabel, OpCode.NOP));
            generateStatement(ifStatement.elseStatement);
            emit(new Instruction(endLabel, OpCode.NOP));
        }
    }

    private void generateExpression(Expression expression) throws CheckerException, GeneratorException {
        if (expression instanceof UnaryExpression) {
            generateUnaryExpression((UnaryExpression) expression);
        } else if (expression instanceof BinaryExpression) {
            generateBinaryExpression((BinaryExpression) expression);
        } else if (expression instanceof LocationExpression) {
            generateLocationExpressionValue((LocationExpression) expression);
        } else if (expression instanceof NumberExpression) {
            emit(new Instruction(null, OpCode.LDC, ((NumberExpression) expression).value.value, null));
        } else {
            BooleanExpression booleanExpression = (BooleanExpression)  expression;
            if (booleanExpression.value.value.toLowerCase().equals("true")) {
                emit(new Instruction(OpCode.LDC, "1"));
            } else {
                emit(new Instruction(OpCode.LDC, "0"));
            }
        }
    }

    private void generateLocationExpressionValue(LocationExpression locationExpression) throws CheckerException, GeneratorException {
        Symbol symbol = this.symbolTable.getVariable(locationExpression.name.value);

        if (symbol.getTypeKind() == TypeKind.Array && symbol.isReference()) {
            emit(new Instruction(OpCode.LDV, symbol.location));

            if (locationExpression.expression != null) {
                generateExpression(locationExpression.expression);
                emit(new Instruction(OpCode.ADD));
                emit(new Instruction(OpCode.IND));
            }
        } else if (symbol.getTypeKind() == TypeKind.Array) {
            emit(new Instruction(OpCode.LDA, symbol.location));

            if (locationExpression.expression != null) {
                generateExpression(locationExpression.expression);
                emit(new Instruction(OpCode.ADD));
                emit(new Instruction(OpCode.IND));
            }
        } else if (symbol.isReference()) {
            emit(new Instruction(OpCode.LDV, symbol.location));
            emit(new Instruction(OpCode.IND));
        } else {
            emit(new Instruction(OpCode.LDV, symbol.location));
        }
    }

    // Used for creating arguments for reference parameters (& or [])
    private void generateLocationExpressionAddress(LocationExpression locationExpression) throws CheckerException, GeneratorException {
        Symbol symbol = this.symbolTable.getVariable(locationExpression.name.value);

        if (symbol.getTypeKind() == TypeKind.Array && symbol.isReference()) {
            emit(new Instruction(OpCode.LDV, symbol.location));

            if (locationExpression.expression != null) {
                generateExpression(locationExpression.expression);
                emit(new Instruction(OpCode.ADD));
            }
        } else if (symbol.getTypeKind() == TypeKind.Array) {
            emit(new Instruction(OpCode.LDA, symbol.location));

            if (locationExpression.expression != null) {
                generateExpression(locationExpression.expression);
                emit(new Instruction(OpCode.ADD));
            }
        } else if (symbol.isReference()) {
            emit(new Instruction(OpCode.LDV, symbol.location));
        } else {
            emit(new Instruction(OpCode.LDA, symbol.location));
        }
    }

    private void generateBinaryExpression(BinaryExpression binaryExpression) throws CheckerException, GeneratorException {
        generateExpression(binaryExpression.left);
        generateExpression(binaryExpression.right);

        TokenKind operator = binaryExpression.operator.kind;

        if (operator == TokenKind.Plus) {
            emit(new Instruction(OpCode.ADD));
        } else if (operator == TokenKind.Minus) {
            emit(new Instruction(OpCode.SUB));
        } else if (operator == TokenKind.Asterisk) {
            emit(new Instruction(OpCode.MULT));
        } else if (operator == TokenKind.Slash) {
            emit(new Instruction(OpCode.DIV));
        } else if (operator == TokenKind.Percent) {
            emit(new Instruction(OpCode.MOD));
        } else if (operator == TokenKind.LessThan) {
            emit(new Instruction(OpCode.LT));
        } else if (operator == TokenKind.GreaterThan) {
            emit(new Instruction(OpCode.GT));
        } else if (operator == TokenKind.LessThanEquals) {
            emit(new Instruction(OpCode.LE));
        } else if (operator == TokenKind.GreaterThanEquals) {
            emit(new Instruction(OpCode.GE));
        } else if (operator == TokenKind.EqualsEquals) {
            emit(new Instruction(OpCode.EQ));
        } else if (operator == TokenKind.ExclamationEquals) {
            emit(new Instruction(OpCode.NE));
        } else if (operator == TokenKind.AmpersandAmpersand) {
            emit(new Instruction(OpCode.AND));
        } else {
            emit(new Instruction(OpCode.OR));
        }
    }

    // Ignoring + operator since it does nothing
    private void generateUnaryExpression(UnaryExpression expression) throws CheckerException, GeneratorException {
        generateExpression(expression.expression);
        TokenKind operator = expression.operator.kind;
        if (operator == TokenKind.Minus) {
            emit(new Instruction(OpCode.NEG));
        } else if (operator == TokenKind.Exclamation) {
            emit(new Instruction(OpCode.NOT));
        }
    }

    private void emit(Instruction instruction) throws GeneratorException {
        this.instructions.add(instruction);
    }

    private String generateLabel() {
        return "L" + this.labelValue++;
    }

    private void incrementLevel() {
        this.level++;
        this.previousDisplacement.push(this.displacement);
        this.displacement = 1;
    }

    private void decrementLevel() {
        this.level--;
        this.displacement = this.previousDisplacement.pop();
    }
}
