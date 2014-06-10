/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.example;

import com.sun.star.util.CloseVetoException;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

/**
 *
 * @author nino
 */
public class DecompressionOptions extends JPanel implements ActionListener{

    static String containsHeader = "The file contains a header";
    static String fieldsAreQuoted = "The fields are quoted";
    static String decompress = "Decompress";
    static String cancel = "Cancel";
    
    static boolean header = false;
    static boolean quoted = false;
    
    
    static JFrame frame;
    
    public DecompressionOptions(){
        super(new BorderLayout());
        
        JCheckBox checkbox1 = new JCheckBox(containsHeader, false);
        JCheckBox checkbox2 = new JCheckBox(fieldsAreQuoted, false);
        JButton decompressButton = new JButton(decompress);
        JButton cancelButton = new JButton(cancel);
        
        // assign action commands
        checkbox1.setActionCommand(containsHeader);
        checkbox2.setActionCommand(fieldsAreQuoted);
        decompressButton.setActionCommand(decompress);
        cancelButton.setActionCommand(cancel);
        
        // add action listeners
        checkbox1.addActionListener(this);
        checkbox2.addActionListener(this);
        decompressButton.addActionListener(this);
        cancelButton.addActionListener(this);
        
        // create 2 panels
        JPanel checkboxPanel = new JPanel(new GridLayout(0,1));
        checkboxPanel.add(checkbox1);
        checkboxPanel.add(checkbox2);
        
        JPanel buttonPanel = new JPanel(new GridLayout(1,0));
        buttonPanel.add(decompressButton);
        buttonPanel.add(cancelButton);
        
        decompressButton.setPreferredSize(new Dimension(40, 30));
        cancelButton.setPreferredSize(new Dimension(40,30));
        decompressButton.setBackground(Color.LIGHT_GRAY);
        cancelButton.setBackground(Color.LIGHT_GRAY);
        
        add(checkboxPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20)); 
        
    }
    
    
    @Override
    public void actionPerformed(ActionEvent e) {
        if(e.getActionCommand().equals(containsHeader)){
            this.header = !this.header;
        }
        else if(e.getActionCommand().equals(fieldsAreQuoted)){
            this.quoted = !this.quoted;
        }
        else if(e.getActionCommand().equals(decompress)){
            
            try {
                
                CsvCompressionPluginFinal.decompressAndOpen(header, quoted);
                
            } catch (CloseVetoException ex) {}
              catch (IOException ex) {}
            
            frame.setVisible(false);
            frame.dispose();
        }
        else if(e.getActionCommand().equals(cancel)){
            frame.setVisible(false);
            frame.dispose();
        }
    }
    
    private static void createAndShowGUI(){
        frame = new JFrame();
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(580, 200));
        frame.setBackground(Color.GRAY);
        
        JComponent newContentPane = new DecompressionOptions();
        newContentPane.setOpaque(true);
        
        frame.setContentPane(newContentPane);
        
        // display the new window
        frame.pack();
        frame.setVisible(true);        
    }
    
    public static void execute(){
        SwingUtilities.invokeLater(new Runnable(){
            
            @Override
            public void run(){
                createAndShowGUI();
            }
        });
    }
    
}
