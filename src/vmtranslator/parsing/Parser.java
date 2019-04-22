/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vmtranslator.parsing;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Contains the functionality for parsing a VM file.
 * @author Tom Coldenhoff
 */
public class Parser {
    
    // List containing a
    private List<String> commands = new ArrayList<>();
    
    // Index of the current command
    private int currentIndex = 0;
    
    // The current command
    private String currentCommand = "";
    
    // Arithmetic commands
    private Set arithmeticCommands;
    
    /**
     * Opens the input file and gets ready to parse.
     * @param filePath The file path to read.
     */
    public Parser(String filePath) {
        
        // Open file
        File file = new File(filePath);
        
        String arStrings[] = {"add", "sub", "neg", "eq", "gt", "lt", "and", "or"
        , "not"};
        arithmeticCommands = new HashSet<>(Arrays.asList(arStrings));
        
        try {
            
            BufferedReader br = new BufferedReader(new FileReader(file));
            
            // Read file
            String st;
            while((st = br.readLine()) != null) {
                
                String[] strings = st.split(" ");
                
                if (strings.length == 0) {
                    continue;
                } else if (st.equals("") || strings[0].equals("//")) {
                    continue;
                }
                
                commands.add(st);
                
            }
            
            br.close();
        } catch (FileNotFoundException ex) {
            
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            
            Logger.getLogger(Parser.class.getName()).log(Level.SEVERE, null, ex);
        }        
    }
    
    /**
     * Checks if there are more commands left, Returns true if there are more
     * left.
     * @return Boolean saying that there are more commands left to use. 
     */
    public boolean hasMoreCommands() {
        
        return currentIndex < commands.size();
    }
    
    /**
     * Reads the next command from the input and makes it the current command.
     * Updates the currentIndex and currentCommand field.
     */
    public void advance() {
        
        currentCommand = commands.get(currentIndex);
        currentIndex++;
    }
    
    /**
     * Returns the type saying what kind of command the current command is.
     * @return Command type.
     */
    public CommandType commandType() {
        // TODO Retiurn all types
        
        String strs[] = currentCommand.split(" ");
        
        if (arithmeticCommands.contains(strs[0])) {
            return CommandType.C_ARITHMETIC;
        } else if (currentCommand.contains("push")) {
            return CommandType.C_PUSH;
        } else if (currentCommand.contains("pop")) {
            return CommandType.C_POP;
        }
        
        return null;
    }
    
    /**
     * Returns the first argument of the current command. Should not be called
     * if the current command is {@link CommandType.C_RETURN}.
     * @return First argument of the current command.
     */
    public String arg1() {
        String strs[] = currentCommand.split(" ");
        
        if (strs.length > 1)
            return strs[1];
        else
            return strs[0];
    }
    
    /**
     * Returns the second argument of the current command. Should be called only
     * if the current command is C_PUSH, C_POP, C_FUNCTION or C_CALL.
     * @return Second argument of the current command.
     */
    public int arg2() {
        String strs[] = currentCommand.split(" ");
        return Integer.parseInt(strs[2]);
    }
}
