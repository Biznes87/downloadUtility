/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prepare;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.ByteBuffer;
import java.nio.channels.Channels;
import java.nio.channels.FileChannel;
import java.nio.channels.ReadableByteChannel;
import java.util.logging.Level;
import java.util.logging.Logger;


public class Download  {   
   
    public static String threadName;
    public static volatile int flag = 0;

   static class ReadAndDownloadClass implements Runnable{
        private static URL url;
        private  String path="";
        private  String urlPath="";
        long timestamp = System.currentTimeMillis();
        int counter = 0;
        int INTERVAL = 1000; // one second
        int LIMIT ; // bytes per INTERVAL
        int BUFFERSIZE = 10;
       
        public ReadAndDownloadClass(String link, int limit, String  outFile) {    
           this.urlPath = link;
                this.path = outFile;
                this.LIMIT= limit;         
        }
   
        public void readlimit(int i) {
            if (counter > LIMIT) {
              long now = System.currentTimeMillis();
                    if (timestamp + INTERVAL >= now) {
                  try {  
                      Thread.sleep(timestamp + INTERVAL - now);
                     // System.out.println("wait");
                  } catch (InterruptedException ex) {
                      ex.printStackTrace();
                  }
                 }
                 timestamp = now;
                    counter = 0;
            }
            int res = i;
            if (res >= 0) {
            counter=counter  + BUFFERSIZE;
            } 
          }
        @Override
        public  void run() {
             
            try {
                System.out.println(Thread.currentThread().getName()+"start");
                long start = + System.currentTimeMillis();
                
                URL website = new URL(urlPath);          
                ReadableByteChannel rbc = Channels.newChannel(website.openStream());      
                ByteBuffer buffer = ByteBuffer.allocate(BUFFERSIZE);
                FileChannel outChannel = new FileOutputStream(path).getChannel();
              
                while (rbc.read(buffer) >= 0 || buffer.position() != 0)
                    {       
                       // readlimit(rbc.read(buffer));
                        buffer.flip();  
                        outChannel.write(buffer);    
                        buffer.compact();          
                    }
                rbc.close();outChannel.close();
                
                long stop =  System.currentTimeMillis();
                System.out.println(Thread.currentThread().getName()+"stoped - " + (stop-start));
                              
            } catch (IOException e) {    e.printStackTrace(); } 
        }
    
    }
}
