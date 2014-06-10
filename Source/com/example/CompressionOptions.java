package com.example;

import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.uno.Exception;
import com.sun.star.util.CloseVetoException;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;


class CheckboxListener implements ItemListener, //only event type needed
				   ActionListener, //for curiosity only
				   ChangeListener {  //for curiosity only
    boolean c1 = false;
    boolean c2 = false;
    @Override	
    public void itemStateChanged(ItemEvent e) {	    
	
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        
        if(e.getActionCommand().equals(CompressionOptions.containsHeader)){
            CompressionOptions.header = !CompressionOptions.header;
        }
        if(e.getActionCommand().equals(CompressionOptions.quotedFields)){
            CompressionOptions.quoted = !CompressionOptions.quoted;
        }
        System.out.println(CompressionOptions.header + " " + CompressionOptions.quoted);
    }

    @Override
    public void stateChanged(ChangeEvent e) {
    
    }
}


public class CompressionOptions extends JPanel
        implements ActionListener {

    static String compressAndContinueEditing = "Save a compressed copy of the document and keep the original version - continue editing in Open/Libre Office (.csvcomp)";
    static String compressAndKeepOriginal = "Save a compressed copy of the document and keep the original version - exit Open/Libre Office after compression (.csvcomp)";
    static String compressOverwrite = "Overwrite current document with a compressed copy and close it (.csvcomp)";
    static String compressOverwriteCsv = "Overwrite current document with a compressed copy and close it (.csv)";
    static String cancel = "Cancel";
    static String compress = "Compress";
    static String containsHeader = "The file contains a header";
    static String quotedFields = "Field(cell) text is quoted";
    
    public static JFrame frame;
    private static int compressionType = -1;
    
    public static boolean header = false;
    public static boolean quoted = false;

    public CompressionOptions() {
        super(new BorderLayout());

        //Create the radio buttons.
        JRadioButton continueButton = new JRadioButton(compressAndContinueEditing);
        continueButton.setMnemonic(KeyEvent.VK_C);
        continueButton.setActionCommand(compressAndContinueEditing);
        //continueButton.setSelected(true);

        JRadioButton keepOriginalButton = new JRadioButton(compressAndKeepOriginal);
        keepOriginalButton.setMnemonic(KeyEvent.VK_K);
        keepOriginalButton.setActionCommand(compressAndKeepOriginal);

        JCheckBox checkBox1 = new JCheckBox(containsHeader);
        JCheckBox checkBox2 = new JCheckBox(quotedFields);
        
        JRadioButton overwriteButton = new JRadioButton(compressOverwrite);
        overwriteButton.setMnemonic(KeyEvent.VK_O);
        overwriteButton.setActionCommand(compressOverwrite);
        
        checkBox1.setActionCommand(containsHeader);
        checkBox2.setActionCommand(quotedFields);
        
        JButton cancelButton = new JButton(cancel);
        cancelButton.setActionCommand(cancel);

        JButton compressButton = new JButton(compress);
        compressButton.setActionCommand(compress);
        
        

        //Group the radio buttons.
        ButtonGroup group = new ButtonGroup();
        group.add(continueButton);
        group.add(keepOriginalButton);
        group.add(overwriteButton);


        //Register a listener for the radio buttons.
        continueButton.addActionListener(this);
        keepOriginalButton.addActionListener(this);
        overwriteButton.addActionListener(this);
        cancelButton.addActionListener(this);
        compressButton.addActionListener(this);
        
        CheckboxListener chkListener = new CheckboxListener();
        
        checkBox1.addActionListener(chkListener);
        checkBox1.addItemListener(chkListener);
        checkBox1.addChangeListener(chkListener);
        checkBox2.addActionListener(chkListener);
        checkBox2.addItemListener(chkListener);
        checkBox2.addChangeListener(chkListener);
        
        
        
        //Put the radio buttons in a column in a panel.
        JPanel radioPanel = new JPanel(new GridLayout(0, 1));
        radioPanel.add(continueButton);
        radioPanel.add(keepOriginalButton);
        radioPanel.add(overwriteButton);

        JPanel buttonPanel = new JPanel(new GridLayout(1, 0));
        buttonPanel.add(compressButton);
        buttonPanel.add(cancelButton);
        
        JPanel checkBoxPanel = new JPanel(new GridLayout(2, 1));
        checkBoxPanel.add(checkBox1);
        checkBoxPanel.add(checkBox2);


        compressButton.setPreferredSize(new Dimension(40, 30));
        compressButton.setBackground(Color.LIGHT_GRAY);

        cancelButton.setPreferredSize(new Dimension(40, 30));
        cancelButton.setBackground(Color.LIGHT_GRAY);

        add(radioPanel, BorderLayout.NORTH);
        add(buttonPanel, BorderLayout.SOUTH);
        add(checkBoxPanel, BorderLayout.CENTER);
        
        setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
    }

    /**
     * Listens to the radio buttons.
     */
    public void actionPerformed(ActionEvent e) {
        
        if (e.getActionCommand().equals(compressAndContinueEditing)
                || e.getActionCommand().equals(compressAndKeepOriginal)
                || e.getActionCommand().equals(compressOverwrite)) {

            if (e.getActionCommand().equals(compressAndContinueEditing)) {
                compressionType = 0;
            } else if (e.getActionCommand().equals(compressAndKeepOriginal)) {
                compressionType = 1;
            } else if (e.getActionCommand().equals(compressOverwrite)) {
                compressionType = 2;
            }

        } else if (compressionType != -1 && e.getActionCommand().equals(compress)) {
            
            try {
               
                CsvCompressionPluginFinal.executePlugin(compressionType, header, quoted);
                frame.setVisible(false);
                frame.dispose();

            
            } catch (CloseVetoException ex) {
                Logger.getLogger(CompressionOptions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (IOException ex) {
                Logger.getLogger(CompressionOptions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (Exception ex) {
                Logger.getLogger(CompressionOptions.class.getName()).log(Level.SEVERE, null, ex);
            } catch (BootstrapException ex) {
                Logger.getLogger(CompressionOptions.class.getName()).log(Level.SEVERE, null, ex);
            } finally {
                System.gc();
                if (compressionType == 1 || compressionType == 2) {
                  
                }
            }
            
        } else if (compressionType == -1 && e.getActionCommand().equals(compress)) {
            
            //WARNING
            String warningMessage = "You have to select a compression type.";
            JOptionPane.showMessageDialog(null, warningMessage, "Warning", JOptionPane.WARNING_MESSAGE);
            
        } else if (e.getActionCommand().equals(cancel)) {
            
            frame.setVisible(false);
            frame.dispose();
            
        }
    }


    /**
     * Create the GUI and show it. For thread safety, this method should be
     * invoked from the event-dispatching thread.
     */
    
    private static void createAndShowGUI() {
        //Create and set up the window.
        frame = new JFrame("Compression options");
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setPreferredSize(new Dimension(850, 270));
        frame.setBackground(Color.GRAY);
        //Create and set up the content pane.
        JComponent newContentPane = new CompressionOptions();
        newContentPane.setOpaque(true); //content panes must be opaque
        frame.setContentPane(newContentPane);

        //Display the window.
        frame.pack();
        frame.setVisible(true);
    }

    public static void execute() {
        javax.swing.SwingUtilities.invokeLater(new Runnable() {
            public void run() {
                createAndShowGUI();
            }
        });
    }
}