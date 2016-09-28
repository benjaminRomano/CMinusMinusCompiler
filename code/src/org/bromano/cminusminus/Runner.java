package org.bromano.cminusminus;

import org.bromano.cminusminus.lexer.CMinusMinusLexer;
import org.bromano.cminusminus.lexer.Lexeme;

import java.io.*;
import java.util.List;

public class Runner {

    public static void main(String[] args) throws Exception {
        if (args.length >= 1) {
            run(loadFile(args[0]));
        }

        run(readFromSystemIn());
    }

    public static String loadFile(String filePath) throws Exception {
        InputStream stream = new BufferedInputStream(new FileInputStream(filePath));

        StringBuilder sb = new StringBuilder();

        try {
            Reader r = new InputStreamReader(stream);
            int c;

            while ((c = r.read()) != -1) {
                sb.append((char) c);
            }
        } catch (Exception e) {
            throw new Exception("Failed to read stream!");
        }

        return sb.toString();
    }

    public static String readFromSystemIn() {
        java.util.Scanner systemInScanner = new java.util.Scanner(System.in);

        StringBuilder inputStringBuilder = new StringBuilder();
        while(systemInScanner.hasNext()) {
            inputStringBuilder.append(systemInScanner.nextLine());
            inputStringBuilder.append("\n");
        }

        return inputStringBuilder.toString();
    }

    public static void run(String code) throws Exception {
        List<Lexeme> lexemes = new CMinusMinusLexer(code).getLexStream();
        lexemes.stream().forEach(System.out::println);
    }
}
