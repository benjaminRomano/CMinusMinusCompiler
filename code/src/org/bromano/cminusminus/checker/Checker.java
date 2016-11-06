package org.bromano.cminusminus.checker;

import org.bromano.cminusminus.lexer.TokenKind;
import org.bromano.cminusminus.nodes.Program;
import org.bromano.cminusminus.nodes.declarations.*;
import org.bromano.cminusminus.nodes.expressions.*;
import org.bromano.cminusminus.nodes.statements.*;
import org.bromano.cminusminus.symbols.Symbol;
import org.bromano.cminusminus.symbols.SymbolTable;
import org.bromano.cminusminus.types.*;

public class Checker {

    public void check(Program program) throws CheckerException {
        CheckerContext context = new CheckerContext();

        for (Declaration declaration : program.declarations) {
            checkDeclaration(context, declaration);
        }

        if (!context.symbolTable.hasVariable("main")) {
            throw new CheckerException("Program must contain a main function");
        }

        Symbol symbol = context.symbolTable.getVariable("main");

        if (symbol.getTypeKind() != TypeKind.Function) {
            throw new CheckerException("main must be a function");
        }

        FunctionType mainFunction = (FunctionType) symbol.getType();

        if (mainFunction.parameters.size() != 0) {
            throw new CheckerException("main must be have 0 parameters");
        }
    }

    private void checkDeclaration(CheckerContext context, Declaration declaration) throws CheckerException {
        if (declaration instanceof VariableDeclaration) {
            checkVariableDeclaration(context, (VariableDeclaration) declaration);
        } else {
            checkFunctionDeclaration(context, (FunctionDeclaration) declaration);
        }
    }

    private void checkVariableDeclaration(CheckerContext context, VariableDeclaration variableDeclaration) throws CheckerException {
        TypeKind typeKind = TypeKind.fromTokenKind(variableDeclaration.type.kind);

        for (Variable variable : variableDeclaration.variables) {
            if (variable.number != null) {
                int size = Integer.parseInt(variable.number.value);

                ArrayType arrayType;

                if (typeKind == TypeKind.Number) {
                    arrayType = new ArrayType(new LiteralType(TypeKind.Number), size);
                } else {
                    arrayType = new ArrayType(new LiteralType(TypeKind.Boolean), size);
                }

                Symbol symbol = new Symbol(arrayType);
                context.symbolTable.addVariable(variable.name.value, symbol);
            } else {
                context.symbolTable.addVariable(variable.name.value, new Symbol(new LiteralType(typeKind)));
            }
        }
    }

    private void checkFunctionDeclaration(CheckerContext context, FunctionDeclaration functionDeclaration) throws CheckerException {
        FunctionType functionType = new FunctionType();

        for (Parameter parameter : functionDeclaration.parameters) {
            TypeKind typeKind = TypeKind.fromTokenKind(parameter.type.kind);

            Type type;

            if (parameter.isArray) {
                if (typeKind == TypeKind.Number) {
                    type = new ArrayType(new LiteralType(TypeKind.Number), 0);
                } else {
                    type = new ArrayType(new LiteralType(TypeKind.Boolean), 0);
                }

            } else {
                type = new LiteralType(typeKind);
            }

            functionType.parameters.add(new FieldType(parameter.name.value, type, parameter.isAddr));
        }

        context.symbolTable.addVariable(functionDeclaration.name.value, new Symbol(functionType, false));
        checkBlockStatement(context, functionDeclaration.blockStatement);
    }

    private void checkBlockStatement(CheckerContext context, BlockStatement blockStatement) throws CheckerException {
        SymbolTable symbolTable = new SymbolTable();
        symbolTable.parent = context.symbolTable;
        context.symbolTable = symbolTable;

        for (VariableDeclaration variableDeclaration : blockStatement.variableDeclarations) {
            checkVariableDeclaration(context, variableDeclaration);
        }


        for (Statement statement : blockStatement.statements) {
            checkStatement(context, statement);
        }


        context.symbolTable = context.symbolTable.parent;
    }

    private void checkStatement(CheckerContext context, Statement statement) throws CheckerException {
        if (statement instanceof BlockStatement) {
            checkBlockStatement(context, (BlockStatement) statement);
        } else if (statement instanceof IfStatement) {
            checkIfStatement(context, (IfStatement) statement);
        } else if (statement instanceof WhileStatement) {
            checkWhileStatement(context, (WhileStatement) statement);
        } else if (statement instanceof DoStatement) {
            checkDoStatement(context, (DoStatement) statement);
        } else if (statement instanceof FunctionCallStatement) {
            checkFunctionCallStatement(context, (FunctionCallStatement) statement);
        } else {
            checkAssignmentStatement(context, (AssignmentStatement) statement);
        }
    }

    private void checkFunctionCallStatement(CheckerContext context, FunctionCallStatement functionCallStatement) throws CheckerException {
        if (!context.symbolTable.hasVariable(functionCallStatement.name.value)) {
            throw new CheckerException("Variable, " + functionCallStatement.name.value + " does not exist in this scope", functionCallStatement.name.linePos);
        }

        Symbol symbol = context.symbolTable.getVariable(functionCallStatement.name.value);

        if (symbol.getTypeKind() != TypeKind.Function) {
            throw new CheckerException("Cannot use variable, " + functionCallStatement.name + " with type " + symbol.getType() + " as a function", functionCallStatement.name.linePos);
        }

        FunctionType functionPrimary = (FunctionType) symbol.getType();

        if (functionPrimary.parameters.size() != functionCallStatement.arguments.size()) {
            throw new CheckerException(String.format("Argument mismatch. Expected %d arguments, received %d", functionPrimary.parameters.size(), functionCallStatement.arguments.size()), functionCallStatement.name.linePos);

        }

        for (int i = 0; i < functionCallStatement.arguments.size(); i++) {
            Type type = checkExpression(context, functionCallStatement.arguments.get(i));

            if (functionPrimary.parameters.get(i).isReference() && !(functionCallStatement.arguments.get(i) instanceof LocationExpression)) {
                throw new CheckerException("Expected locationExpression for pass-by-reference parameter",  functionCallStatement.name.linePos);
            }

            if (!(type.equals(functionPrimary.parameters.get(i).getType()))) {
                throw new CheckerException("Expected parameter of type, " + functionPrimary.parameters.get(i).getTypeKind() + ", but given type " + symbol.getType(), functionCallStatement.name.linePos);
            }
        }
   }

    private void checkAssignmentStatement(CheckerContext context, AssignmentStatement assignmentStatement) throws CheckerException {
        Type location = checkLocationExpression(context, assignmentStatement.locationExpression);

        Type value = checkExpression(context, assignmentStatement.expression);

        if (!location.equals(value)) {
            throw new CheckerException("TypeKind mismatch on assignment. Given " + value.getTypeKind() + ", expected " + location.getTypeKind(), assignmentStatement.locationExpression.name.linePos);
        }
    }

    private void checkDoStatement(CheckerContext context, DoStatement doStatement) throws CheckerException {
        checkStatement(context, doStatement.statement);

        Type type = checkExpression(context, doStatement.conditional);

        if (type.getTypeKind() != TypeKind.Boolean) {
            throw new CheckerException("Conditional returned " + type.getTypeKind() + " expected boolean");
        }
    }

    private void checkWhileStatement(CheckerContext context, WhileStatement whileStatement) throws CheckerException {
        Type type = checkExpression(context, whileStatement.conditional);

        if (type.getTypeKind() != TypeKind.Boolean) {
            throw new CheckerException("Conditional returned " + type.getTypeKind() + " expected boolean");
        }

        checkStatement(context, whileStatement.statement);
    }

    private void checkIfStatement(CheckerContext context, IfStatement ifStatement) throws CheckerException {
        Type type = checkExpression(context, ifStatement.conditional);

        if (type.getTypeKind() != TypeKind.Boolean) {
            throw new CheckerException("Conditional returned " + type.getTypeKind() + " expected boolean");
        }

        checkStatement(context, ifStatement.statement);
        checkStatement(context, ifStatement.elseStatement);
    }

    private Type checkExpression(CheckerContext context, Expression expression) throws CheckerException {
        if (expression instanceof UnaryExpression) {
            return checkUnaryExpression(context, (UnaryExpression) expression);
        } else if (expression instanceof BinaryExpression) {
            return checkBinaryExpression(context, (BinaryExpression) expression);
        } else if (expression instanceof LocationExpression) {
            return checkLocationExpression(context, (LocationExpression) expression);
        } else if (expression instanceof NumberExpression) {
            return new LiteralType(TypeKind.Number);
        } else {
            return new LiteralType(TypeKind.Boolean);
        }
    }

    private Type checkLocationExpression(CheckerContext context, LocationExpression locationExpression) throws CheckerException {
        if (!context.symbolTable.hasVariable(locationExpression.name.value)) {
            throw new CheckerException("Variable, " + locationExpression.name.value + " does not exist in this scope", locationExpression.name.linePos);
        }

        Symbol symbol = context.symbolTable.getVariable(locationExpression.name.value);

        // Not an arrayType
        if (locationExpression.expression == null) {
            return symbol.getType();
        }

        if (symbol.getTypeKind() != TypeKind.Array) {
            throw new CheckerException("Cannot use array indexing on a " + symbol.getTypeKind(), locationExpression.name.linePos);
        }

        Type accessorType = checkExpression(context, locationExpression.expression);

        if (accessorType.getTypeKind() != TypeKind.Number) {
            throw new CheckerException("Cannot use " + accessorType.getTypeKind() + " to access array", locationExpression.name.linePos);
        }

        ArrayType arrayType = (ArrayType) symbol.getType();

        if (arrayType.getElementType().getTypeKind() == TypeKind.Number) {
            return new LiteralType(TypeKind.Number);
        } else {
            return new LiteralType(TypeKind.Boolean);
        }
   }

    private Type checkBinaryExpression(CheckerContext context, BinaryExpression binaryExpression) throws CheckerException {
        TokenKind kind = binaryExpression.operator.kind;
        Type leftExpression = checkExpression(context, binaryExpression.left);
        Type rightExpression = checkExpression(context, binaryExpression.right);

        if (isAMatch(new TokenKind[] { TokenKind.ExclamationEquals, TokenKind.EqualsEquals }, kind)) {
            if (leftExpression.equals(rightExpression)) {
                throw new CheckerException("TypeKind mismatch for, " + binaryExpression.operator.value + " operator", binaryExpression.operator.linePos);
            }

            return new LiteralType(TypeKind.Boolean);
        } else if (isAMatch(new TokenKind[] { TokenKind.LessThan, TokenKind.LessThanEquals, TokenKind.GreaterThan, TokenKind.GreaterThanEquals }, kind)) {
            if (leftExpression.getTypeKind() != TypeKind.Number) {
                throw new CheckerException("TypeKind mismatch for left-hand side of " + binaryExpression.operator.value + " operator. Expected a Number", binaryExpression.operator.linePos);
            } else if (rightExpression.getTypeKind() != TypeKind.Number) {
                throw new CheckerException("TypeKind mismatch for right-hand side of " + binaryExpression.operator.value + " operator. Expected a Number", binaryExpression.operator.linePos);
            }

            return new LiteralType(TypeKind.Boolean);
        } else if (isAMatch(new TokenKind[] { TokenKind.Minus, TokenKind.Plus, TokenKind.Percent, TokenKind.Asterisk, TokenKind.Slash }, kind)) {
            if (leftExpression.getTypeKind() != TypeKind.Number) {
                throw new CheckerException("TypeKind mismatch for left-hand side of " + binaryExpression.operator.value + " operator. Expected a Number", binaryExpression.operator.linePos);
            } else if (rightExpression.getTypeKind() != TypeKind.Number) {
                throw new CheckerException("TypeKind mismatch for right-hand side of " + binaryExpression.operator.value + " operator. Expected a Number", binaryExpression.operator.linePos);
            }

            return new LiteralType(TypeKind.Number);
        } else {
            // && and ||
            if (leftExpression.getTypeKind() != TypeKind.Boolean) {
                throw new CheckerException("TypeKind mismatch for left-hand side of " + binaryExpression.operator.value + " operator. Expected a Boolean", binaryExpression.operator.linePos);
            } else if (rightExpression.getTypeKind() != TypeKind.Boolean) {
                throw new CheckerException("TypeKind mismatch for right-hand side of " + binaryExpression.operator.value + " operator. Expected a Boolean", binaryExpression.operator.linePos);
            }

            return new LiteralType(TypeKind.Boolean);
        }
    }

    private Type checkUnaryExpression(CheckerContext context, UnaryExpression unaryExpression) throws CheckerException {
        TokenKind kind = unaryExpression.operator.kind;
        if (kind == TokenKind.Plus || kind == TokenKind.Minus) {
            Type type = checkExpression(context, unaryExpression.expression);

            if (type.getTypeKind() != TypeKind.Number) {
                throw new CheckerException("Cannot use " + unaryExpression.operator.value + " with " + type.getTypeKind(), unaryExpression.operator.linePos);

            }

            return type;
        } else if (kind == TokenKind.Exclamation) {
            Type type = checkExpression(context,unaryExpression.expression);

            if (type.getTypeKind() != TypeKind.Boolean) {
                throw new CheckerException("Cannot use " + unaryExpression.operator.value + " with " + type.getTypeKind(), unaryExpression.operator.linePos);
            }

            return type;
        }

        throw new CheckerException("Could not recognize operator " + unaryExpression.operator.value, unaryExpression.operator.linePos);
    }

    private boolean isAMatch(TokenKind[] kinds, TokenKind kind) {
        for (TokenKind k : kinds) {
            if (k == kind) {
                return true;
            }
        }

        return false;
    }
}
