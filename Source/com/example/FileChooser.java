/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example;

import javax.swing.JFileChooser;

/**
 *
 * @author Nino
 */
public class FileChooser {
    
    public JFileChooser fileChooser;
    
    public FileChooser(){
        fileChooser = new JFileChooser();
        
    }
    
    public String execute(){
        fileChooser.setDialogTitle("Select .csvcomp file");
        
        int retVal = fileChooser.showOpenDialog(null);
        if(retVal == JFileChooser.APPROVE_OPTION)
            return fileChooser.getSelectedFile().getAbsolutePath().toLowerCase().replaceAll("\\\\", "/");
        return null;
    }
}
