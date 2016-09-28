package org.bromano.cminusminus.nodes.declarations;

import org.bromano.cminusminus.lexer.Lexeme;
import org.bromano.cminusminus.nodes.statements.Statement;

import java.util.List;

public class FunctionDeclaration implements Declaration {
    public Lexeme name;
    public List<Lexeme> parameters;
    public Statement statement;
}
