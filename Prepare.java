/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package prepare;



import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.channels.Channels;
import java.nio.channels.ReadableByteChannel;
import java.util.ArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;


/**
 *
 * @author dex
 */



public class Prepare {
    private static URL url;
    private static ArrayList<String> linksArr = new ArrayList<>();
   private static ArrayList<String> namesArr= new ArrayList<>();
    private static int numOfThreads ;
    private static int downLimit ;
    private static String pathToLinksFile;
    private static String pathToOutFolder ;
    
         
    public static void openLinksFile(String path) throws FileNotFoundException{
        
        BufferedReader in = new BufferedReader(new FileReader(path));
        String str = "";
        try {
            int i =0;
            while((str=in.readLine())!= null){
                String[] parts = str.split("\\s+");
                linksArr.add(parts[0]);
                namesArr.add(parts[1]);
                i++;
            }
        } catch (IOException ex) {
           ex.printStackTrace();
        }
   
    }
    
    public static void main(String[] args)  {
     /* -n количество одновременно качающих потоков (1,2,3,4....)
      * -l общее ограничение на скорость скачивания, для всех потоков, размерность - байт/секунда, можно использовать суффиксы k,m (k=1024, m=1024*1024)
      * -f путь к файлу со списком ссылок
      * -o имя папки, куда складывать скачанные файлы
     */   
        try {
            //getting start parameters
            numOfThreads = Integer.valueOf(args[0]);
            downLimit = Integer.valueOf(args[1]);
            pathToLinksFile = args[2];
            pathToOutFolder = args[3];
            
            //parse Links File
            openLinksFile(pathToLinksFile);
        } catch (FileNotFoundException ex) {System.out.println("There is no such file");}
          catch (ArrayIndexOutOfBoundsException ex){
              System.out.println("Missing arguments" + "\n" + 
                      "correct way is: [number of Threads] [Download limit] [Path to link file] [Path to destination folder]");
          }
     
     ExecutorService exec = Executors.newFixedThreadPool(numOfThreads);
     
        for (int i = 0; i < linksArr.size(); i++) {
            String tmp = pathToOutFolder + namesArr.get(i);
            exec.submit(new Download.ReadAndDownloadClass(linksArr.get(i),downLimit,tmp));
        }
             exec.shutdown();
    
       }


}
