/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vmtranslator;

import vmtranslator.parsing.Parser;
import vmtranslator.codewriting.CodeWriter;

/**
 *
 * @author Tom Coldenhoff
 */
public class VMTranslator {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        
        if (args.length == 0) {
            System.out.println("Wrong usage! usage: java -jar VMTranslator.jar"
                    + " [filename.vm]");
            return;
        }
        
        Parser parser = new Parser(args[0]);
             
        // Get first part of file
        String strs[] = args[0].split("\\.");
        CodeWriter writer = new CodeWriter(strs[0]);
        
        while(parser.hasMoreCommands()) {
            // Advance to first following command
            parser.advance();

            switch(parser.commandType()) {
                
                case C_ARITHMETIC:
                    writer.writeArithmetic(parser.arg1());
                    break;
                
                case C_PUSH:
                case C_POP:
                    writer.writePushPop(parser.commandType(), parser.arg1(), 
                            parser.arg2());
                    break;
            }
        }
        
        writer.close();
    }
    
}
