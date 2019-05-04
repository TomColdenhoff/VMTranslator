/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vmtranslator;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.List;
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
        
        if (args.length != 1) {
            System.out.println("Wrong usage! usage: java -jar VMTranslator.jar"
                    + " [filename.vm /folder]");
            return;
        }
        
        List<String> files = new ArrayList<>();
        
        // If direct .vm file given
        if (args[0].contains(".vm")) {
            files.add(args[0]);
        } else { // If folder given
            File f = new File(args[0]);
            File[] acceptedFiles = f.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String name) {
                    return name.endsWith("vm");
                }
            });
            
            for(File file : acceptedFiles) {
                files.add(file.getParent() + "/" + file.getName());
            }
        }
        
        for (String str : files) {
            System.out.println(str);
        }        

        
        // Get first part of file
        String strs[] = args[0].split("\\.");
        // Open file writer
        CodeWriter writer = new CodeWriter();
        writer.setFileName(strs[0]);
        writer.writeInit();
        
        for (String file : files) {
            
            Parser parser = new Parser(file);

        
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

                    case C_LABEL:
                        writer.writeLabel(parser.arg1());
                        break;

                    case C_GOTO:
                        writer.writeGoto(parser.arg1());
                        break;

                    case C_IF:
                        writer.writeIf(parser.arg1());
                        break;

                    case C_FUNCTION:
                        writer.writeFunction(parser.arg1(), parser.arg2());
                        break;

                    case C_RETURN:
                        writer.writeReturn();
                        break;

                    case C_CALL:
                        writer.writeCall(parser.arg1(), parser.arg2());
                        break;

                }
            }

        }
        
        writer.close();
    }
    
}
