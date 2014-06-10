package com.example;

import com.sun.star.beans.PropertyValue;
import com.sun.star.comp.helper.Bootstrap;
import com.sun.star.comp.helper.BootstrapException;
import com.sun.star.frame.XComponentLoader;
import com.sun.star.lang.XMultiComponentFactory;
import com.sun.star.uno.UnoRuntime;
import com.sun.star.uno.XComponentContext;
import com.sun.star.lib.uno.helper.Factory;
import com.sun.star.lang.XSingleComponentFactory;
import com.sun.star.registry.XRegistryKey;
import com.sun.star.lib.uno.helper.WeakBase;
import com.sun.star.sheet.XSpreadsheetDocument;
import com.sun.star.uno.Exception;
import com.sun.star.util.CloseVetoException;
import com.sun.star.util.XCloseable;
import externallist.ListNames;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JOptionPane;
import myexternalsort.CompressionUnit;
import myexternalsort.OS_Properties;



public final class CsvCompressionPluginFinal
        extends WeakBase
        implements com.sun.star.lang.XInitialization,
        com.sun.star.frame.XDispatch,
        com.sun.star.lang.XServiceInfo,
        com.sun.star.frame.XDispatchProvider {
    
    
   
    
    public static String command = null;
    public static boolean compressionTypeChosen = false;

    private static XComponentContext m_xContext;
    private static com.sun.star.frame.XFrame m_xFrame;
    private static final String m_implementationName = CsvCompressionPluginFinal.class.getName();
    private static final String[] m_serviceNames = {
        "com.sun.star.frame.ProtocolHandler"};
    
    private static boolean IS_SERVICE_STARTED=false;

    public CsvCompressionPluginFinal(XComponentContext context) {
        m_xContext = context;
    }

    

    public static XSingleComponentFactory __getComponentFactory(String sImplementationName) {
        XSingleComponentFactory xFactory = null;

        if (sImplementationName.equals(m_implementationName)) {
            xFactory = Factory.createComponentFactory(CsvCompressionPluginFinal.class, m_serviceNames);
        }
        return xFactory;
    }

    public static boolean __writeRegistryServiceInfo(XRegistryKey xRegistryKey) {
        return Factory.writeRegistryServiceInfo(m_implementationName,
                m_serviceNames,
                xRegistryKey);
    }

    // com.sun.star.lang.XInitialization:
    public void initialize(Object[] object)
            throws com.sun.star.uno.Exception {
        if (object.length > 0) {
            m_xFrame = (com.sun.star.frame.XFrame) UnoRuntime.queryInterface(
                    com.sun.star.frame.XFrame.class, object[0]);
        }
    }
    
    public static String getPathFromUrl(String url){
        String path = url.toLowerCase();
        path = path.replaceAll("file:///", "");
        return path;
    }
    
    private static void openCsvDocument_compress(String csvURL) throws Exception, BootstrapException {
        XMultiComponentFactory xRemoteServiceManager = null;
        m_xContext = Bootstrap.bootstrap();
        if (xRemoteServiceManager == null && m_xContext != null) {
            xRemoteServiceManager = m_xContext.getServiceManager();
            Object desktop = xRemoteServiceManager.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);

            XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, desktop);

            PropertyValue[] loadProperties = new PropertyValue[1];
            loadProperties[0] = new PropertyValue();
            loadProperties[0].Name = "Hidden";
            loadProperties[0].Value = false;

            xComponentLoader.loadComponentFromURL(csvURL, "_blank", 0, loadProperties);
        }
    }
    
    private static void openCsvDocument_decompress(String csvURL) throws Exception, BootstrapException {
        XMultiComponentFactory xRemoteServiceManager = null;
        
        if (xRemoteServiceManager == null && m_xContext != null) {
            xRemoteServiceManager = m_xContext.getServiceManager();
            Object desktop = xRemoteServiceManager.createInstanceWithContext("com.sun.star.frame.Desktop", m_xContext);

            XComponentLoader xComponentLoader = (XComponentLoader) UnoRuntime.queryInterface(XComponentLoader.class, desktop);

            PropertyValue[] loadProperties = new PropertyValue[1];
            loadProperties[0] = new PropertyValue();
            loadProperties[0].Name = "Hidden";
            loadProperties[0].Value = false;

            xComponentLoader.loadComponentFromURL(csvURL, "_blank", 0, loadProperties);
        }
    }
    
    public static String URL = null;
    public static boolean killProcess = false;
    
    public static void executePlugin(int type, boolean header, boolean quoted) throws CloseVetoException, IOException, Exception, BootstrapException {
        
        try {
            XSpreadsheetDocument csvDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(XSpreadsheetDocument.class, m_xFrame.getController().getModel());
            XCloseable xCsvCloseable = (XCloseable) UnoRuntime.queryInterface(XCloseable.class, csvDoc);

            URL = m_xFrame.getController().getModel().getURL();
            
            CompressionUnit compressionUnit = new CompressionUnit();
            
            
            
            switch(type){
                case 0:{
                    // save a compressed copy and continue editing
                    String csvPath = getPathFromUrl(URL);
                    xCsvCloseable.close(IS_SERVICE_STARTED);
                    compressionUnit.compressCSV(csvPath, header, quoted);                    
                    openCsvDocument_compress(URL);
                    return;
                }
                case 1:{
                    String csvPath = getPathFromUrl(URL);
                    xCsvCloseable.close(IS_SERVICE_STARTED);
                    compressionUnit.compressCSV(csvPath, header, quoted);
                    
                    return;
                }
                case 2:{
                    String csvPath = getPathFromUrl(URL);
                    xCsvCloseable.close(IS_SERVICE_STARTED);
                    compressionUnit.compressCSV(csvPath, header, quoted);
                    return;
                }
                default:
                    return;
            }

        }  finally {
            
          

            Runtime.getRuntime().addShutdownHook(new Thread() {
                @Override
                public void run() {
                    /* WINDOWS - Create a batch script used for erasing temporary files as sooon as JVM terminates */
                    if (OS_Properties.OS_NAME.contains("Windows")) {
                        File batchScript = new File(OS_Properties.TEMP_DIR_PATH + "eraser.bat");
                        PrintWriter pw = null;
                        String scriptExecutionCommand = "cmd /c start " + batchScript.getAbsolutePath();
                        String commandToWait = "ping 127.0.0.1 -n 3 > nul";
                        String commandDelete1 = "del /F " + OS_Properties.TEMP_DIR_PATH + "fwt.dat";
                        String commandDelete2 = "del /F " + OS_Properties.TEMP_DIR_PATH + "test_output.dat";
                        String regexExternalList = "erase /F " + OS_Properties.TEMP_DIR_PATH + "ext_list*.tmp";

                        try {
                            pw = new PrintWriter(new FileWriter(batchScript));
                            pw.println(commandToWait);
                            pw.println("echo Deleting temporary files . . .");
                            pw.println(commandDelete1);
                            pw.println(commandDelete2);
                            pw.println(regexExternalList);
                            pw.println("exit");
                        } catch (IOException ex) {
                            
                        } finally {
                            if (pw != null) {
                                pw.close();

                                try {

                                    /* Execute the script as a new independent process */
                                    Process p = Runtime.getRuntime().exec(scriptExecutionCommand);
                                    p.waitFor();

                                } catch (IOException ex) { } catch (InterruptedException ex) { }

                            }
                        }
                    }
                    else{
                       
                        try {
                            
                            //unix based OS --- Linux, Solaris, MAC OS and so on...
                            
                            Files.delete(Paths.get(OS_Properties.TEMP_DIR_PATH + "fwt.dat"));
                            Files.delete(Paths.get(OS_Properties.TEMP_DIR_PATH + "test_output.dat"));
                            File a = new File(OS_Properties.TEMP_DIR_PATH);
                            FilenameFilter filter = new FilenameFilter(){
                                @Override
                                public boolean accept(File dir, String name){
                                    String lowercaseName = name.toLowerCase();
                                    if(lowercaseName.contains("ext_list")){
                                        return true;
                                    }
                                    else{
                                        return false;
                                    }
                                }
                            };
                            
                            File[] files = a.listFiles(filter);
                            for(int i=0; i<files.length; i++){
                                Files.delete(files[i].toPath());
                            }
                        } catch (IOException ex) {
                            Logger.getLogger(CsvCompressionPluginFinal.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                }
            });


            System.exit(0);

 
        }
    }
    
    public static String decompressAndOpen(boolean header, boolean quoted) throws CloseVetoException, IOException {
        
        XSpreadsheetDocument csvDoc = (XSpreadsheetDocument) UnoRuntime.queryInterface(XSpreadsheetDocument.class, m_xFrame.getController().getModel());
        XCloseable xCsvCloseable = (XCloseable) UnoRuntime.queryInterface(XCloseable.class, csvDoc);
        xCsvCloseable.close(IS_SERVICE_STARTED);
        
        String csvCompPath = new FileChooser().execute();
        
        CompressionUnit compressionUnit = new CompressionUnit();
        compressionUnit.decompressCSV(csvCompPath, header, quoted);
        
        String newURL = "file:///" + csvCompPath.replace(".csvcomp", "_d.csv");
        
        try {
            
            openCsvDocument_decompress(newURL);
            
        } catch (Exception ex) {
            Logger.getLogger(CsvCompressionPluginFinal.class.getName()).log(Level.SEVERE, null, ex);
        } catch (BootstrapException ex) {
            Logger.getLogger(CsvCompressionPluginFinal.class.getName()).log(Level.SEVERE, null, ex);
        } finally{
            System.gc();
            File compFile = new File(csvCompPath);
            String fwtPath = compFile.getParent() + OS_Properties.FILE_SEPARATOR + "fwt.dat";
            
            Path pathToFwt = (new File(fwtPath)).toPath();
            
            compFile = null;
            System.gc();
            Files.deleteIfExists(pathToFwt);
        }
        return csvCompPath;
    }
    
    // com.sun.star.frame.XDispatch:
    public void dispatch(com.sun.star.util.URL aURL,
            com.sun.star.beans.PropertyValue[] aArguments) {
        if (aURL.Protocol.compareTo("com.example.csvcompressionpluginfinal:") == 0) {
            
            if (aURL.Path.compareTo("Compress") == 0) {
               
                CompressionOptions.execute();
                
                
                             
            }
            if(aURL.Path.compareTo("Decompress") == 0){
                
                DecompressionOptions.execute();
                
            }
        }
    }

    public void addStatusListener(com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL) {
        // add your own code here
    }

    public void removeStatusListener(com.sun.star.frame.XStatusListener xControl,
            com.sun.star.util.URL aURL) {
        // add your own code here
    }

    // com.sun.star.lang.XServiceInfo:
    public String getImplementationName() {
        return m_implementationName;
    }

    public boolean supportsService(String sService) {
        int len = m_serviceNames.length;

        for (int i = 0; i < len; i++) {
            if (sService.equals(m_serviceNames[i])) {
                return true;
            }
        }
        return false;
    }

    public String[] getSupportedServiceNames() {
        return m_serviceNames;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch queryDispatch(com.sun.star.util.URL aURL,
            String sTargetFrameName,
            int iSearchFlags) {
        if (aURL.Protocol.compareTo("com.example.csvcompressionpluginfinal:") == 0) {
            if (aURL.Path.compareTo("Compress") == 0 || aURL.Path.compareTo("Decompress") == 0) {
                return this;
            }
            
        }
        return null;
    }

    // com.sun.star.frame.XDispatchProvider:
    public com.sun.star.frame.XDispatch[] queryDispatches(
            com.sun.star.frame.DispatchDescriptor[] seqDescriptors) {
        int nCount = seqDescriptors.length;
        com.sun.star.frame.XDispatch[] seqDispatcher =
                new com.sun.star.frame.XDispatch[seqDescriptors.length];

        for (int i = 0; i < nCount; ++i) {
            seqDispatcher[i] = queryDispatch(seqDescriptors[i].FeatureURL,
                    seqDescriptors[i].FrameName,
                    seqDescriptors[i].SearchFlags);
        }
        return seqDispatcher;
    }
}
