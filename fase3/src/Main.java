import java.io.*;
import AST.Program;
import Auxiliar.PW;

public class Main {
    public static void main(String[] args) {
        // Verificar a quantidade de argumentos
        if (args.length != 1 && args.length != 2) {
            System.out.println("Usage:\n   Main input [output]");
            System.out.println("input is the file to be compiled");
            System.out.println("output is the file where the generated code will be stored");
            return;
        }

        // Verificar a existência do input
        File file = new File(args[0]);
        if (!file.exists() || !file.canRead()) {
            System.out.println("Either the file " + args[0] + " does not exist or it cannot be read");
            return;
        }
        
        // Abrir o input para leitura
        FileReader stream;
        try {
            stream = new FileReader(file);
        } catch (FileNotFoundException e) {
            System.out.println("Something wrong: file does not exist anymore");
            return;
        }
        
        // Ler arquivo de input
        int numChRead;
        char[] input = new char[(int) file.length() + 1];
        try {
            numChRead = stream.read(input, 0, (int) file.length());
        } catch (IOException e) {
            System.out.println("Error reading file " + args[0]);
            return;
        }

        
        
        // Fechar o arquivo de input
        try {
            stream.close();
        } catch (IOException e) {
            System.out.println("Error in handling the file " + args[0]);
            return;
        }

        // Compilar o programa
        Compiler compiler = new Compiler();
        Program program = compiler.compile(input, args[0]);

        // Obter nome do output
        String outputFileName;
        if (args.length == 2)
            outputFileName = args[1];
        else {
            outputFileName = args[0];
            int lastIndex = outputFileName.lastIndexOf('.');
            if (lastIndex == -1)
                lastIndex = outputFileName.length();
            outputFileName = outputFileName.substring(0, lastIndex) + ".c";
        }

        // Abrir o output para escrita
        FileOutputStream outputStream;
        try {
            outputStream = new FileOutputStream(outputFileName);
        } catch (IOException e) {
            System.out.println("File " + args[1] + " was not found");
            return;
        }

        // Escrever no arquivo de output
        PrintWriter printWriter = new PrintWriter(outputStream);
        program.genC(new PW(printWriter));
        if (printWriter.checkError())
            System.out.println("There was an error in the output");
        
        // Fechar o arquivo de output
        try {
            outputStream.close();
        } catch (IOException e) {
            System.out.println("Error in handling the file " + outputFileName);
        }
    }
}