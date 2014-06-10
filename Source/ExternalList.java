/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package externallist;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.MappedByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Nino
 */
public class ExternalList {

    private File file;
    public RandomAccessFile raf;
    private MappedByteBuffer IO;
    int size;
    long bufferStart;
    long bufferSize;
    boolean isEmpty;
    
    public ExternalList() throws IOException{
        String fileName = "ext_list" + String.valueOf(System.currentTimeMillis());

        this.raf = new RandomAccessFile(this.file = File.createTempFile(fileName, ".tmp"), "rw");
        ListNames.names.add(this.file.getAbsolutePath());
        this.raf.seek(0);
        this.bufferStart = 0;
        this.bufferSize = 100 * 1024;
        this.IO = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, this.bufferStart, this.bufferSize);//null;
        this.size = 0;
        this.isEmpty = true;
    }
    
    public void add(int value) throws IOException{
        if(this.isEmpty)
            this.isEmpty = false;
        if(!IO.hasRemaining()){
            bufferStart += bufferSize;
            bufferSize = 100 * 1024;
            IO = raf.getChannel().map(FileChannel.MapMode.READ_WRITE, bufferStart, bufferSize);
            IO.rewind();
        }
        IO.putInt(value);
        //this.raf.writeInt(value);
        this.size++;
    }
    
    public int get(int index) throws IOException{
        long fileSize = Files.size(Paths.get(this.file.getAbsolutePath()));
        if(this.IO == null){
            this.bufferStart = 4 * index;
            this.bufferSize = (fileSize - this.bufferStart) < 100 * 1024 * 1024 ? (fileSize - this.bufferStart) : 100 * 1024 * 1024;
            this.IO = this.raf.getChannel().map(FileChannel.MapMode.READ_ONLY, this.bufferStart, this.bufferSize);
            return IO.getInt();
        } else if(4 * index < this.bufferStart || 4 * index + 3 > this.bufferStart + this.bufferSize){
            this.bufferStart = 4 * index;
            this.bufferSize = (fileSize - this.bufferStart) < 100 * 1024 * 1024 ? (fileSize - this.bufferStart) : 100 * 1024 * 1024;
            this.IO = this.raf.getChannel().map(FileChannel.MapMode.READ_ONLY, this.bufferStart, this.bufferSize);
            return IO.getInt();
        } else{
            return this.IO.getInt((int) (4 * index - this.bufferStart));
        }
     
    }
    
    public int size(){
        return this.size;
    }
    
    public boolean isEmpty(){
        return this.isEmpty;
    }
    
    public void destroy() throws IOException{   
        try{
            this.IO = null;
        } finally{
            this.raf.getChannel().close();
            this.raf.close();            
        }        
       
    }
    
    @Override
    public String toString(){
        StringBuilder sb = new StringBuilder("[");
        try {            
            for(int i=0; i<this.size-1; i++)
                sb.append(this.get(i) + ", ");
            
            sb.append(this.get(this.size-1));
        } catch (IOException ex) {
            
        }
        return sb.append(']').toString();
    }

    public File getFile() {
        return file;
    }

    public void setFile(File file) {
        this.file = file;
    }

    public RandomAccessFile getRaf() {
        return raf;
    }

    public void setRaf(RandomAccessFile raf) {
        this.raf = raf;
    }

    public MappedByteBuffer getIO() {
        return IO;
    }

    public void setIO(MappedByteBuffer IO) {
        this.IO = IO;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size;
    }

    public long getBufferStart() {
        return bufferStart;
    }

    public void setBufferStart(long bufferStart) {
        this.bufferStart = bufferStart;
    }

    public long getBufferSize() {
        return bufferSize;
    }

    public void setBufferSize(long bufferSize) {
        this.bufferSize = bufferSize;
    }

    public boolean isIsEmpty() {
        return isEmpty;
    }

    public void setIsEmpty(boolean isEmpty) {
        this.isEmpty = isEmpty;
    }

}
