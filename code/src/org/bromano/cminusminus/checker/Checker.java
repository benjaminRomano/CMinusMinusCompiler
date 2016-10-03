package org.bromano.cminusminus.checker;

import org.bromano.cminusminus.checker.primaries.*;
import org.bromano.cminusminus.lexer.TokenKind;
import org.bromano.cminusminus.nodes.Program;
import org.bromano.cminusminus.nodes.declarations.*;
import org.bromano.cminusminus.nodes.expressions.*;
import org.bromano.cminusminus.nodes.statements.*;

public class Checker {

    public void check(Program program) throws CheckerException {
        CheckerContext context = new CheckerContext();

        for (Declaration declaration : program.declarations) {
            checkDeclaration(context, declaration);
        }

        // TODO: Verify main exists with no parameters
    }

    private void checkDeclaration(CheckerContext context, Declaration declaration) throws CheckerException {
        if (declaration instanceof VariableDeclaration) {
            checkVariableDeclaration(context, (VariableDeclaration) declaration);
        } else {
            checkFunctionDeclaration(context, (FunctionDeclaration) declaration);
        }
    }

    private void checkVariableDeclaration(CheckerContext context, VariableDeclaration variableDeclaration) throws CheckerException {
        Type type = Type.fromKind(variableDeclaration.type.kind);

        for (Variable variable : variableDeclaration.variables) {
            if (variable.number != null) {
                if (type == Type.Number) {
                    context.environment.addVariable(variable.name.value, new Primary(Type.NumberArray));
                } else {
                    context.environment.addVariable(variable.name.value, new Primary(Type.BooleanArray));
                }
            } else {
                context.environment.addVariable(variable.name.value, new Primary(type));
            }
        }
    }

    private void checkFunctionDeclaration(CheckerContext context, FunctionDeclaration functionDeclaration) throws CheckerException {
        FunctionPrimary primary = new FunctionPrimary();

        for (Parameter parameter : functionDeclaration.parameters) {
            Type type = Type.fromKind(parameter.type.kind);
            if (parameter.number != null) {
                if (type == Type.Number) {
                    primary.parameters.add(new ParameterPrimary(new Primary(Type.NumberArray), parameter.isAddr));
                } else {
                    primary.parameters.add(new ParameterPrimary(new Primary(Type.BooleanArray), parameter.isAddr));
                }
            } else {
                primary.parameters.add(new ParameterPrimary(new Primary(type), parameter.isAddr));
            }
        }

        context.environment.addVariable(functionDeclaration.name.value, primary);

        checkBlockStatement(context, functionDeclaration.blockStatement);
    }

    private void checkBlockStatement(CheckerContext context, BlockStatement blockStatement) throws CheckerException {
        Environment environment = new Environment();
        environment.parent = context.environment;
        context.environment = environment;

        for (VariableDeclaration variableDeclaration : blockStatement.variableDeclarations) {
            checkVariableDeclaration(context, variableDeclaration);
        }


        for (Statement statement : blockStatement.statements) {
            checkStatement(context, statement);
        }


        context.environment = context.environment.parent;
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
        if (!context.environment.hasVariable(functionCallStatement.name.value)) {
            throw new CheckerException("Variable, " + functionCallStatement.name.value + " does not exist in this scope", functionCallStatement.name.linePos);
        }

        Primary primary = context.environment.getVariable(functionCallStatement.name.value);

        if (!(primary instanceof FunctionPrimary)) {
            throw new CheckerException("Cannot use variable, " + functionCallStatement.name + " with type " + primary.getType() + " as a function", functionCallStatement.name.linePos);
        }

        FunctionPrimary functionPrimary = (FunctionPrimary) primary;

        for (int i = 0; i < functionCallStatement.arguments.size(); i++) {
            primary = checkExpression(context, functionCallStatement.arguments.get(i));

            if (functionPrimary.parameters.get(i).isAddr && !(functionCallStatement.arguments.get(i) instanceof  LocationExpression)) {
                throw new CheckerException("Expected location for pass-by-reference parameter",  functionCallStatement.name.linePos);
            }

            if (primary.getType() != functionPrimary.parameters.get(i).getType()) {
                throw new CheckerException("Expected parameter of type, " + functionPrimary.parameters.get(i).getType() + ", but given type " + primary.getType(), functionCallStatement.name.linePos);
            }
        }
   }

    private void checkAssignmentStatement(CheckerContext context, AssignmentStatement assignmentStatement) throws CheckerException {
        Primary location = checkLocationExpression(context, assignmentStatement.locationExpression);

        Primary value = checkExpression(context, assignmentStatement.expression);

        if (location.getType() != value.getType()) {

            throw new CheckerException("Type mismatch on assignment. Given " + value.getType() + ", expected " + location.getType(), assignmentStatement.locationExpression.name.linePos);
        }
    }

    private void checkDoStatement(CheckerContext context, DoStatement doStatement) throws CheckerException {
        checkStatement(context, doStatement.statement);

        Primary primary = checkExpression(context, doStatement.conditional);

        if (primary.getType() != Type.Boolean) {
            throw new CheckerException("Conditional returned " + primary.getType() + " expected boolean");
        }
    }

    private void checkWhileStatement(CheckerContext context, WhileStatement whileStatement) throws CheckerException {
        Primary primary = checkExpression(context, whileStatement.conditional);

        if (primary.getType() != Type.Boolean) {
            throw new CheckerException("Conditional returned " + primary.getType() + " expected boolean");
        }

        checkStatement(context, whileStatement.statement);
    }

    private void checkIfStatement(CheckerContext context, IfStatement ifStatement) throws CheckerException {
        Primary primary = checkExpression(context, ifStatement.conditional);

        if (primary.getType() != Type.Boolean) {
            throw new CheckerException("Conditional returned " + primary.getType() + " expected boolean");
        }

        checkStatement(context, ifStatement.statement);
        checkStatement(context, ifStatement.elseStatement);
    }

    private Primary checkExpression(CheckerContext context, Expression expression) throws CheckerException {
        if (expression instanceof UnaryExpression) {
            return checkUnaryExpression(context, (UnaryExpression) expression);
        } else if (expression instanceof BinaryExpression) {
            return checkBinaryExpression(context, (BinaryExpression) expression);
        } else if (expression instanceof LocationExpression) {
            return checkLocationExpression(context, (LocationExpression) expression);
        } else if (expression instanceof NumberExpression) {
            return new Primary(Type.Number);
        } else {
            return new Primary(Type.Boolean);
        }
    }

    private Primary checkLocationExpression(CheckerContext context, LocationExpression locationExpression) throws CheckerException {
        if (!context.environment.hasVariable(locationExpression.name.value)) {
            throw new CheckerException("Variable, " + locationExpression.name.value + " does not exist in this scope", locationExpression.name.linePos);
        }

        Primary primary = context.environment.getVariable(locationExpression.name.value);

        if (locationExpression.expression == null) {
            return primary;
        }

        if (primary.getType() != Type.NumberArray && primary.getType() != Type.BooleanArray) {
            throw new CheckerException("Cannot use array indexing on a " + primary.getType(), locationExpression.name.linePos);
        }

        Primary accessorPrimary = checkExpression(context, locationExpression.expression);

        if (primary.getType() != Type.Number) {
            throw new CheckerException("Cannot use " + accessorPrimary.getType() + " to access array", locationExpression.name.linePos);
        }

        if (primary.getType() == Type.NumberArray) {
            return new Primary(Type.Number);
        } else {
            return new Primary(Type.Boolean);
        }
   }

    private Primary checkBinaryExpression(CheckerContext context, BinaryExpression binaryExpression) throws CheckerException {
        TokenKind kind = binaryExpression.operator.kind;
        Primary leftExpression = checkExpression(context, binaryExpression.left);
        Primary rightExpression = checkExpression(context, binaryExpression.right);

        if (isAMatch(new TokenKind[] { TokenKind.ExclamationEquals, TokenKind.EqualsEquals }, kind)) {
            if (leftExpression.getType() != rightExpression.getType()) {
                throw new CheckerException("Type mismatch for, " + binaryExpression.operator.value + " operator", binaryExpression.operator.linePos);
            }

            return new Primary(Type.Boolean);
        } else if (isAMatch(new TokenKind[] { TokenKind.LessThan, TokenKind.LessThanEquals, TokenKind.GreaterThan, TokenKind.GreaterThanEquals }, kind)) {
            if (leftExpression.getType() != Type.Number) {
                throw new CheckerException("Type mismatch for left-hand side of " + binaryExpression.operator.value + " operator. Expected a Number", binaryExpression.operator.linePos);
            } else if (rightExpression.getType() != Type.Number) {
                throw new CheckerException("Type mismatch for right-hand side of " + binaryExpression.operator.value + " operator. Expected a Number", binaryExpression.operator.linePos);
            }

            return new Primary(Type.Boolean);
        } else if (isAMatch(new TokenKind[] { TokenKind.Minus, TokenKind.Plus, TokenKind.Percent, TokenKind.Asterisk, TokenKind.Slash }, kind)) {
            if (leftExpression.getType() != Type.Number) {
                throw new CheckerException("Type mismatch for left-hand side of " + binaryExpression.operator.value + " operator. Expected a Number", binaryExpression.operator.linePos);
            } else if (rightExpression.getType() != Type.Number) {
                throw new CheckerException("Type mismatch for right-hand side of " + binaryExpression.operator.value + " operator. Expected a Number", binaryExpression.operator.linePos);
            }

            return new Primary(Type.Number);
        } else {
            // && and ||
            if (leftExpression.getType() != Type.Boolean) {
                throw new CheckerException("Type mismatch for left-hand side of " + binaryExpression.operator.value + " operator. Expected a Boolean", binaryExpression.operator.linePos);
            } else if (rightExpression.getType() != Type.Boolean) {
                throw new CheckerException("Type mismatch for right-hand side of " + binaryExpression.operator.value + " operator. Expected a Boolean", binaryExpression.operator.linePos);
            }

            return new Primary(Type.Boolean);
        }
    }

    private Primary checkUnaryExpression(CheckerContext context, UnaryExpression unaryExpression) throws CheckerException {
        TokenKind kind = unaryExpression.operator.kind;
        if (kind == TokenKind.Plus || kind == TokenKind.Minus) {
            Primary primary = checkExpression(context, unaryExpression.expression);

            if (primary.getType() != Type.Number) {
                throw new CheckerException("Cannot use " + unaryExpression.operator.value + " with " + primary.getType(), unaryExpression.operator.linePos);

            }

            return primary;
        } else if (kind == TokenKind.Exclamation) {
            Primary primary = checkExpression(context,unaryExpression.expression);

            if (primary.getType() != Type.Boolean) {
                throw new CheckerException("Cannot use " + unaryExpression.operator.value + " with " + primary.getType(), unaryExpression.operator.linePos);
            }

            return primary;
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
