package vmtranslator.codewriting;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import vmtranslator.parsing.CommandType;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 * Contains the functionality for translating VM commands into hack assembly
 * and writing it to a .asm file.
 * @author Tom Coldenhoff
 */
public class CodeWriter {
    
    private BufferedWriter writer = null;
    
    /**
     * Constructor opens the output file and prepares it for writing.
     * @param filename The filename for the output file.
     */
    public CodeWriter(String filename) {
        
        File file = new File(filename + ".asm");
        
        try {
            writer = new BufferedWriter(new FileWriter(file));
        } catch (IOException ex) {
            Logger.getLogger(CodeWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
    /**
     * Writes the given lines to the open output file.
     * @param strs The lines to write.
     */
    private void write(String[] strs) {
        
        for (String str : strs) {
            try {
                writer.write(str + "\n");
            } catch (IOException ex) {
                Logger.getLogger(CodeWriter.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Closes the opened file for writing.
     */
    public void close() {
        try {
            writer.close();
        } catch (IOException ex) {
            Logger.getLogger(CodeWriter.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * writes the translation of an arithmetic command to the file.
     * @param command The arithmetic command.
     */
    public void writeArithmetic(String command) {
        
        // Save lines here before writing
        String strs[] = null;
        
        switch(command) {
            case "add":
                strs = Templates.getAddSubTemplate("+");
                break;
            case "sub":
                strs = Templates.getAddSubTemplate("-");
                break;
            case "eq":
                strs = Templates.getGtLtEqTemplate("JNE");
                break;
            case "gt":
                strs = Templates.getGtLtEqTemplate("JLE");
                break;
            case "lt":
                strs = Templates.getGtLtEqTemplate("JGE");
                break;
            case "neg":
                strs = Templates.getNegTemplate();
                break;
            case "and":
                strs = Templates.getAndOrTemplate(command);
                break;
            case "or":
                strs = Templates.getAndOrTemplate(command);
                break;
            case "not":
                strs = Templates.getNotTemplate();
                break;
        }
        
        write(strs);
        
    }
    
    /**
     * Writes the translation of a push or pop command to the file.
     * @param commandType The type of command.
     * @param segment The segment of the index.
     * @param index The index.
     */
    public void writePushPop(CommandType commandType, String segment, 
            int index) {
        
        // Save lines here before writing
        String strs[] = null;
        
        switch(commandType) {
        
            case C_POP:
                strs = getPopLines(segment, index);
                break;
            case C_PUSH:
                strs = getPushLines(segment, index);
                break;
        }
        
        // Write lines to file;
        write(strs);
    }
    
    /**
     * Gets the lines to write for this push command.
     * @param segment The segment of the command.
     * @param index The index.
     * @return Asm lines to write to the file.
     */
    private String[] getPushLines(String segment, int index) {
        
        switch(segment) {
            case "constant":
                return Templates.getPushConstantTemplate(index);
            case "local":
                return Templates.getPushTemplate("LCL", index, false);
            case "argument":
                return Templates.getPushTemplate("ARG", index, false);
            case "this":
                return Templates.getPushTemplate("THIS", index, false);
            case "that":
                return Templates.getPushTemplate("THAT", index, false);
            case "temp":
                return Templates.getPushTemplate("R5", index + 5, false);
            case "pointer":
                if (index == 0) {
                    return Templates.getPushTemplate("THIS", index, true); 
                } else if (index == 1) {
                    return Templates.getPushTemplate("THAT", index, true);
                }
            case "static":
                return Templates.getPushTemplate(String.valueOf(16 + index), index, true);

       }
        
        return null;
    }
    
    /**
     * Gets the lines to write for this pop command.
     * @param segment The segment of the command.
     * @param index The index.
     * @return Asm lines to write to the file.
     */
    private String[] getPopLines(String segment, int index) {
        switch (segment) {
            case "local":
                return Templates.getPopTemplate("LCL", index, false);
            case "argument":
                return Templates.getPopTemplate("ARG", index, false);
            case "this":
                return Templates.getPopTemplate("THIS", index, false);
            case "that":
                return Templates.getPopTemplate("THAT", index, false);
            case "temp":
                return Templates.getPopTemplate("R5", index + 5, false);
            case "pointer": {
                if (index == 0) {
                    return Templates.getPopTemplate("THIS", index, true);
                } else if (index == 1) {
                    return Templates.getPopTemplate("THAT", index, true);
                }
                
                break;
            }
            case "static":
                return Templates.getPopTemplate(String.valueOf(16 + index), index, true);
                
        }
        
        return null;
    }
}
