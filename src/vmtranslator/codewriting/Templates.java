/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vmtranslator.codewriting;

/**
 * Contains all hack assembly templates that can be used for translating and
 * writing.
 * @author Tom Coldenhoff
 */
public class Templates {
    
    private static int jumpFlag = 0;
    
    /**
     * Returns the command for a constant push.
     * @param index The constant number.
     * @return The assembly command in strings.
     */
    public static String[] getPushConstantTemplate(int index) {
        
        String str = Integer.toString(index);
        
        String template[] = {
            "@"+str,
            "D=A",
            "@SP",
            "A=M",
            "M=D",
            "@SP",
            "M=M+1"
        };
        
        return template;
    }
    
    public static String[] getPushTemplate(String segment, int index, boolean
            isDirect) {
        
        String noPointerCode = (isDirect)? "" : "@" + index + "\n" + "A=D+A\nD=M";
        
        String template[] = {
            "@" + segment,
            "D=M",
            noPointerCode,
            "@SP",
            "A=M",
            "M=D",
            "@SP",
            "M=M+1"
        };
        
        return template;
    }
    
    /**
     * Returns the command for the last part of a push operation.
     * @return last part of a push operation in HACK asm
     */
    public static String[] getFinishPushTemplate() {
        String template[] = {
            "@SP",
            "A=M",
            "M=D",
            "@SP",
            "M=M+1"
        };
        
        return template;
    }
    
    /**
     * 
     * @param segment
     * @param index
     * @param isDirect
     * @return 
     */
    public static String[] getPopTemplate(String segment, int index, boolean
            isDirect) {
        
        String noPointerCode = (isDirect) ? "D=A" : "D=M\n@" + index + "\nD=D+A";
        
        String template[] = {
            "@" + segment,
            noPointerCode,
            "@R13",
            "M=D",
            "@SP",
            "AM=M-1",
            "D=M",
            "@R13",
            "A=M",
            "M=D"  
        };
        
        return template;
    }
    
    /**
     * Returns the command for an add operation.
     * @param operand The operand, can be + or -.
     * @return The assembly command in strings.
     */
    public static String[] getAddSubTemplate(String operand) {
        
        String template[] = {
            "@SP",
            "AM=M-1",
            "D=M",
            "A=A-1",
            "M=M" + operand + "D"
        };
        
        return template;
    }
    
    /**
     * Returns the command for a lt, gt or eq operation.
     * @param type The type of jump we want, choices: JGE, JLE, JNE. 
     * @return The assembly command in strings
     */
    public static String[] getGtLtEqTemplate(String type) {
        jumpFlag++;
        
        String template[] = {
            "@SP",
            "AM=M-1",
            "D=M",
            "A=A-1",
            "D=D-M",
            "@FALSE" + jumpFlag,
            "D;" + type,
            "@SP",
            "A=M-1",
            "M=0",
            "@CONTINUE" + jumpFlag,
            "0;JMP",
            "(FALSE" + jumpFlag +")",
            "@SP",
            "A=M-1",
            "M=-1",
            "(CONTINUE" + jumpFlag + ")"
        };
               
        return template;
    }
    
    /**
     * Returns the command for a neg operation.
     * @return The assembly command in strings.
     */
    public static String[] getNegTemplate() {
        
        String template[] = {
            "D=0",
            "@SP",
            "A=M-1",
            "M=D-M"
        };
        
        return template;
    }
    
    /**
     * Returns the command for an and or or operation.
     * @param command The command, and or or.
     * @return The assembly command in strings.
     */
    public static String[] getAndOrTemplate(String command) {
        
        String template[] = new String[5];
        template[0] = "@SP";
        template[1] = "AM=M-1";
        template[2] = "D=M";
        template[3] = "A=A-1";
        
        switch(command) {
            case "and":
                template[4] = "M=M&D";
            break;
            case "or":
                template[4] = "M=M|D";
            break;
        }
        
        return template;
    }
    
    /**
     * Returns the command for the not operation.
     * @return The assembly command in strings.
     */
    public static String[] getNotTemplate() {
        
        String[] template = {
            "@SP",
            "A=M-1",
            "M=!M"
        };
        
        return template;
    }
}
