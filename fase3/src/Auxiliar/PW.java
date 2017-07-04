package Auxiliar;

import java.io.PrintWriter;

public class PW {
    public PW(PrintWriter out) {
        this.out = out;
    }

    public void increment() {
        ++level;
    }

    public void decrement() {
        --level;
    }

    public void indent() {
        for (int i = 0; i < level; i++)
            out.print(spaces);
    }

    public void println(String text) {
        out.println(text);
    }

    public void print(String text) {
        out.print(text);
    }
    
    private int level;
    private PrintWriter out;
    private String spaces = "    ";
}
