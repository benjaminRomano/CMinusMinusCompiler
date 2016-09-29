package org.bromano.cminusminus.emitter;

import org.bromano.cminusminus.nodes.Program;

public class CMinusMinusEmitter implements Emitter {

    private PrettyPrinter prettyPrinter;

    @Override
    public String emit(Program program) throws EmitterException {

        this.prettyPrinter = new PrettyPrinter();
//
//        for (Statement s : program.statements) {
//            this.emitStatement(s);
//        }
//
        return prettyPrinter.toString();
    }

//    private void emitBreakStatement(BreakStatement statement) {
//        this.prettyPrinter.append("break;\n");
//    }
//
//    private void emitContinueStatement(ContinueStatement statement) {
//        this.prettyPrinter.append("continue;\n");
//    }
//
//    private void emitReturnStatement(ReturnStatement statement) throws EmitterException {
//            this.prettyPrinter.append("return ");
//            this.emitExpression(statement.expression);
//            this.prettyPrinter.append(";\n");
//    }
//
//    private void emitCompoundStatement(BlockStatement statement) throws EmitterException {
//
//        this.prettyPrinter.append("{\n");
//        this.prettyPrinter.increaseIndent();
//
//        for (Statement s : statement.statements) {
//            this.emitStatement(s);
//        }
//
//        this.prettyPrinter.decreaseIndent();
//        this.prettyPrinter.append("}\n");
//    }
//
//    private void emitWhileStatement(WhileStatement statement) throws EmitterException {
//
//        this.prettyPrinter.append("while (");
//
//        this.emitExpression(statement.conditional);
//
//        this.prettyPrinter.append(") ");
//
//        this.emitStatement(statement.statement);
//    }
//
//    private void emitForStatement(ForStatement statement) throws EmitterException {
//
//        this.prettyPrinter.append("for (");
//
//        this.emitStatement(statement.initializationStatement);
//
//        if (statement.conditionalExpression != null) {
//            this.emitExpression(statement.conditionalExpression);
//        }
//
//        this.prettyPrinter.append(";");
//
//        if (statement.incrementalExpression != null) {
//            this.emitExpression(statement.incrementalExpression);
//        }
//
//        this.prettyPrinter.append(")");
//
//        this.emitStatement(statement.statement);
//    }
//
//    private void emitExpressionStatement(ExpressionStatement statement) throws EmitterException {
//
//        this.emitExpression(statement.expression);
//        this.prettyPrinter.append(";\n");
//    }
//
//    private void emitFunctionDeclarationStatement(FunctionDeclaration statement) throws EmitterException {
//
//        this.prettyPrinter.append("func ");
//        this.prettyPrinter.append(statement.name.value);
//        this.emitParamList(statement.parameters);
//        this.prettyPrinter.append(" ");
//        this.emitStatement(statement.statement);
//    }
//
//    private void emitIfStatement(IfStatement statement) throws EmitterException {
//
//        this.prettyPrinter.append("if (");
//        this.emitExpression(statement.conditional);
//        this.prettyPrinter.append(") ");
//        this.emitStatement(statement.statement);
//
//        if (statement.elseStatement != null) {
//            this.prettyPrinter.append(" else ");
//            this.emitStatement(statement.elseStatement);
//        }
//    }
//
//    private void emitVariableDeclarationStatement(VariableDeclaration statement) throws EmitterException {
//
//        this.prettyPrinter.append("var ");
//        this.prettyPrinter.append(statement.name.value);
//        this.prettyPrinter.append(" = ");
//        this.emitExpression(statement.expression);
//
//        if (!(statement.expression instanceof LambdaExpression)) {
//            this.prettyPrinter.append(";\n");
//        }
//    }
//
//    private void emitStatement(Statement statement) throws EmitterException {
//
//        if (statement instanceof BreakStatement) {
//            this.emitBreakStatement((BreakStatement)statement);
//
//        } else if (statement instanceof BlockStatement) {
//
//            this.emitCompoundStatement((BlockStatement)statement);
//
//        } else if (statement instanceof ContinueStatement) {
//
//            this.emitContinueStatement((ContinueStatement)statement);
//
//        } else if (statement instanceof ExpressionStatement) {
//
//            this.emitExpressionStatement((ExpressionStatement)statement);
//
//        } else if (statement instanceof ForStatement) {
//
//            this.emitForStatement((ForStatement)statement);
//
//        } else if (statement instanceof FunctionDeclaration) {
//
//            this.emitFunctionDeclarationStatement((FunctionDeclaration)statement);
//
//        } else if (statement instanceof IfStatement) {
//
//            this.emitIfStatement((IfStatement)statement);
//
//        } else if (statement instanceof ReturnStatement) {
//
//            this.emitReturnStatement((ReturnStatement) statement);
//
//        } else if (statement instanceof VariableDeclaration) {
//
//            this.emitVariableDeclarationStatement((VariableDeclaration) statement);
//
//        } else if (statement instanceof WhileStatement) {
//            this.emitWhileStatement((WhileStatement) statement);
//
//        } else {
//            throw new EmitterException("Cannot emit statement!" + statement);
//        }
//    }
//
//    private void emitAccessorExpression(IndexExpression expression) throws EmitterException {
//
//        this.emitExpression(expression.expression);
//        this.prettyPrinter.append("[");
//        this.emitExpression(expression.indexExpression);
//        this.prettyPrinter.append("]");
//    }
//
//    private void emitNullLiteralExpression(NullLiteralExpression expression) throws EmitterException {
//
//        this.prettyPrinter.append("null");
//    }
//
//    private void emitArrayExpression(LocationExpression expression) throws EmitterException {
//
//        this.prettyPrinter.append("[");
//
//        for ( int i = 0; i < expression.values.size(); i++) {
//
//
//            this.emitExpression(expression.values.get(i));
//
//            if ( i != expression.values.size() - 1) {
//                this.prettyPrinter.append(", ");
//            }
//        }
//
//        this.prettyPrinter.append("]");
//    }
//
//    private void emitBinaryExpression(BinaryExpression expression) throws EmitterException {
//
//        this.emitExpression(expression.left);
//
//        this.emitBinaryOperator(expression.operator);
//
//        this.emitExpression(expression.right);
//    }
//
//    private void emitBooleanLiteralExpression(NumberExpression expression) {
//
//        this.prettyPrinter.append(expression.bool.value);
//    }
//
//    private void emitCallExpression(CallExpression expression) throws EmitterException {
//
//        this.emitExpression(expression.function);
//        this.prettyPrinter.append("(");
//
//        for ( int i = 0; i < expression.arguments.size(); i++) {
//
//            this.emitExpression(expression.arguments.get(i));
//
//            if ( i != expression.arguments.size() - 1) {
//                this.prettyPrinter.append(", ");
//            }
//        }
//
//        this.prettyPrinter.append(")");
//    }
//
//    private void emitIntegerLiteralExpression(IntegerLiteralExpression expression) {
//
//        this.prettyPrinter.append(expression.integer.value);
//    }
//
//    private void emitLambdaExpression(LambdaExpression expression) throws EmitterException {
//
//
//        this.emitParamList(expression.parameters);
//        this.prettyPrinter.append(" => ");
//        this.emitStatement(expression.statement);
//    }
//
//    private void emitPostfixExpression(SumExpression expression) throws EmitterException {
//
//        this.emitExpression(expression.expression);
//
//        this.prettyPrinter.append(expression.operator.value);
//    }
//
//    private void emitStringLiteralExpression(StringLiteralExpression expression) {
//
//        //Default to single quotes unless required to use double
//        boolean containsSingleQuote = expression.string.value.contains("'");
//
//        if (containsSingleQuote) {
//            this.prettyPrinter.append("\"");
//        } else {
//            this.prettyPrinter.append("\'");
//        }
//
//        this.prettyPrinter.append(expression.string.value);
//
//        if (containsSingleQuote) {
//            this.prettyPrinter.append("\"");
//        } else {
//            this.prettyPrinter.append("\'");
//        }
//    }
//
//    private void emitUnaryExpression(UnaryExpression expression) throws EmitterException {
//
//        this.prettyPrinter.append(expression.operator.value);
//        this.emitExpression(expression.expression);
//    }
//
//    private void emitNameExpression(IdentifierExpression expression) {
//
//        this.prettyPrinter.append(expression.name.value);
//    }
//
//    private void emitObjectExpression(ObjectExpression expression) throws EmitterException {
//
//        if (expression.keyValues.size() == 0) {
//            this.prettyPrinter.append("{}");
//            return;
//        }
//
//        this.prettyPrinter.append("{ \n");
//
//
//        this.prettyPrinter.increaseIndent();
//
//        int i = 0;
//
//        for (Map.Entry<Lexeme, Expression> keyValue : expression.keyValues.entrySet()) {
//            i++;
//
//            this.prettyPrinter.append(keyValue.getKey().value);
//            this.prettyPrinter.append(" : ");
//            this.emitExpression(keyValue.getValue());
//
//            if (i == expression.keyValues.size() - 1) {
//                this.prettyPrinter.append(",");
//            }
//
//            this.prettyPrinter.append(("\n"));
//        }
//
//        this.prettyPrinter.decreaseIndent();
//        this.prettyPrinter.append("}");
//
//    }
//
//    private void emitPropertyAccessorExpression(PropertyAccessorExpression expression) throws EmitterException {
//
//        this.emitExpression(expression.expression);
//        this.prettyPrinter.append(".");
//        this.prettyPrinter.append(expression.property.value);
//
//    }
//
//    private void emitGroupExpression(GroupExpression expression) throws EmitterException {
//
//        this.prettyPrinter.append("(");
//
//        this.emitExpression(expression.expression);
//
//        this.prettyPrinter.append(")");
//
//    }
//
//    private void emitExpression(Expression expression) throws EmitterException {
//
//        if (expression instanceof PropertyAccessorExpression) {
//
//            this.emitPropertyAccessorExpression((PropertyAccessorExpression) expression);
//
//        } else if (expression instanceof IndexExpression) {
//
//            this.emitAccessorExpression((IndexExpression) expression);
//
//        } else if (expression instanceof LocationExpression) {
//
//            this.emitArrayExpression((LocationExpression) expression);
//
//        } else if (expression instanceof NullLiteralExpression) {
//
//            this.emitNullLiteralExpression((NullLiteralExpression) expression);
//
//        } else if (expression instanceof BinaryExpression) {
//            this.emitBinaryExpression((BinaryExpression) expression);
//
//        } else if (expression instanceof NumberExpression) {
//
//            this.emitBooleanLiteralExpression((NumberExpression) expression);
//
//        } else if (expression instanceof CallExpression) {
//
//            this.emitCallExpression((CallExpression) expression);
//
//        } else if (expression instanceof IntegerLiteralExpression) {
//
//            this.emitIntegerLiteralExpression((IntegerLiteralExpression) expression);
//
//        } else if (expression instanceof LambdaExpression) {
//
//            this.emitLambdaExpression((LambdaExpression) expression);
//
//        } else if (expression instanceof IdentifierExpression) {
//
//            this.emitNameExpression((IdentifierExpression) expression);
//
//        } else if (expression instanceof ObjectExpression) {
//
//            this.emitObjectExpression((ObjectExpression) expression);
//
//        } else if (expression instanceof SumExpression) {
//
//            this.emitPostfixExpression((SumExpression) expression);
//
//        } else if (expression instanceof StringLiteralExpression) {
//
//            this.emitStringLiteralExpression((StringLiteralExpression) expression);
//
//        } else if (expression instanceof UnaryExpression) {
//
//            this.emitUnaryExpression((UnaryExpression) expression);
//
//        } else if (expression instanceof GroupExpression) {
//
//            this.emitGroupExpression((GroupExpression) expression);
//
//        } else {
//            throw new EmitterException("Could not emit expression" + expression);
//        }
//    }
//
//    private void emitBinaryOperator(Lexeme lexeme) {
//        this.prettyPrinter.append(" " + lexeme.value.trim() + " ");
//    }
//
//    private void emitParamList(List<Lexeme> lexemes) {
//
//        this.prettyPrinter.append("(");
//
//        for (int i = 0; i < lexemes.size(); i++) {
//
//            this.prettyPrinter.append(lexemes.get(i).value);
//
//            if(i != lexemes.size() - 1) {
//                this.prettyPrinter.append(", ");
//            }
//        }
//
//        this.prettyPrinter.append(")");
//    }
}
