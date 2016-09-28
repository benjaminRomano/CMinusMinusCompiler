package org.bromano.cminusminus.emitter;

public class PrettyPrinter {

    private String indentation;
    private int indentLevel;
    private StringBuilder text;
    private boolean addIndent;

    public PrettyPrinter() {
        this.text = new StringBuilder();
        this.indentation = "\t";
        this.addIndent = false;
        this.indentLevel = 0;
    }

    public void reset() {
        this.text = new StringBuilder();
    }

    public void increaseIndent() {
        this.indentLevel += 1;
    }

    public void decreaseIndent() {
        this.indentLevel -= 1;

    }

    public void append(String s) {
        //Indent if new line
        if (this.addIndent) {

            this.indent();

            this.addIndent = false;
        }

        this.text.append(s);

        if (s.length() != 0 && s.charAt(s.length() - 1) == '\n') {
            this.addIndent = true;
        }
    }

    private void indent() {
        for (int i = 0; i < this.indentLevel; i++) {
            this.text.append(this.indentation);
        }
    }

    @Override
    public String toString() {
        return this.text.toString();
    }
}
