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
    private int returnLabel = 0;

    /**
     * Informs the {@link CodeWriter that the translation of a new VM file has
     * started(called by the main program of the VM translator).
     * @param fileName 
     */
    public void setFileName(String fileName) {
        File file = new File(fileName + ".asm");
        
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
     * Writes the assembly instruction that effect the bootstrap code that
     * initializes the VM. This generated code must be placed at the beginning
     * of the generated *.asm file.
     */
    public void writeInit() {
        String initLines[] = {
            "@256",
            "D=A",
            "@SP",
            "M=D"
        };
        
        write(initLines);
        writeCall("Sys.init", 0);
    }
    
    /**
     * Writes assembly code that effect the label command.
     * @param label The label name.
     */
    public void writeLabel(String label) {
        String labelCommand[] = {
            "(" + label + ")"
        };
        
        write(labelCommand);
    }
    
    /**
     * Writes assembly code that effects the goto command.
     * @param label The label to go to.
     */
    public void writeGoto(String label) {
        String gotoCommand[] = {
            "@" + label,
            "0;JMP"
        };
        
        write(gotoCommand);
    }

    /**
     * Writes assembly code that effect the if-goto command
     * @param label The label to go to.
     */
    public void writeIf(String label) {
        String ifCommand[] = {
            "@SP",
            "AM=M-1",
            "D=M",
            "A=A-1",
            "@" + label,
            "D;JNE"
        };
        
        write(ifCommand);
    }
        
    /**
     * Writes the assembly code that effects the function command.
     * @param functionName The name of the function.
     * @param numVars The amount of local variables the function uses.
     */
    public void writeFunction(String functionName, int numVars) {
        
        String functionCommand[] = {
            "(" + functionName + ")",
        };
        
        write(functionCommand);
        
        // Write space for local variables
        for (int i = 0; i < numVars; i++) {
            writePushPop(CommandType.C_PUSH, "constant", 0);
        }
        
    }
    
    /**
     * Writes assembly code that effects the call command.
     * @param functionName The name of the function to call.
     * @param numArgs The amount of arguments the function takes.
     */
    public void writeCall(String functionName, int numArgs) {
        
        // Save calling function
        String saveCommand[] = {
            "@return-address" + returnLabel,
            "D=A",
        };
        
        write(saveCommand);
        write(Templates.getFinishPushTemplate());
        
        // Save local addres
        String lclCommand[] = {
            "@LCL",
            "D=M",
        };
        
        write(lclCommand);
        write(Templates.getFinishPushTemplate());
        
        // Save arg addres
        String argCommand[] = {
            "@ARG",
            "D=M",
        };
        
        write(argCommand);
        write(Templates.getFinishPushTemplate());
        
        // Save this addres
        String thisCommand[] = {
            "@THIS",
            "D=M",
        };
        
        write(thisCommand);
        write(Templates.getFinishPushTemplate());
        
        // Save that addres
        String thatCommand[] = {
            "@THAT",
            "D=M",
        };
        
        write(thatCommand);
        write(Templates.getFinishPushTemplate());
        
        // Reposition ARG
        String argRepositionCommand[] = {
            "@SP",
            "D=M",
            "@" + numArgs,
            "D=D-A",
            "@5",
            "D=D-A",
            "@ARG",
            "M=D"
        };
        
        write(argRepositionCommand);
        
        // Reposition LCL
        String lclRepositionCommand[] = {
            "@SP",
            "D=M",
            "@LCL",
            "M=D"
        };
        
        write(lclRepositionCommand);
        
        // transfer control
        writeGoto(functionName);
        
        // declare return address label
        writeLabel("return-address" + returnLabel);
        
        returnLabel++;
    }
    
    /**
     * Writes assembly code that effects the return command.
     */
    public void writeReturn() {
        
        String frameLCLCommand[] = {
            "@LCL",
            "D=M",
            "@FRAME",
            "M=D"
        };
        write(frameLCLCommand);
        
        String frameRETCommand[] = {
            "@5",
            "A=D-A",
            "D=M",
            "@RET",
            "M=D"
        };
        write(frameRETCommand);

        String popARGCommand[] = {
            "@SP",
            "AM=M-1",
            "D=M",
            "@ARG",
            "A=M",
            "M=D"
        };
        write(popARGCommand);
        
        String spRestoreCommand[] = {
            "@ARG",
            "D=M+1",
            "@SP",
            "M=D"
        };
        write(spRestoreCommand);

        String thatRestoreCommand[] = {
            "@FRAME",
            "A=M-1",
            "D=M",
            "@THAT",
            "M=D"
        };
        write(thatRestoreCommand);

        String thisRestoreCommand[] = {
            "@FRAME",
            "D=M",
            "@2",
            "A=D-A",
            "D=M",
            "@THIS",
            "M=D"
        };
        write(thisRestoreCommand);
        
        String argRestoreCommand[] = {
            "@FRAME",
            "D=M",
            "@3",
            "A=D-A",
            "D=M",
            "@ARG",
            "M=D"
        };
        write(argRestoreCommand);

        String lclRestoreCommand[] = {
            "@FRAME",
            "D=M",
            "@4",
            "A=D-A",
            "D=M",
            "@LCL",
            "M=D"
        };
        
        write(lclRestoreCommand);
        
        String gotoCommand[] = {
            "@RET",
            "A=M",
            "0;JMP"
        };
        write(gotoCommand);
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
