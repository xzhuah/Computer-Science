

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

/**
 * Created by xzhua on 2017/12/17.
 */
public class Csvdatabase {
    private static final String db_location = "study.csv";


    public static void main(String[] args){
        String hh = "123!#!hello!#!1\n";
        insert(new DatabaseRecord(hh));
        String[] a=readAll();
        for(int i = 0 ; i<a.length;i++){
            System.out.println(a[i]);
        }
    }
    public static void insert(DatabaseRecord dbr){
        try {

            Writer writer = new BufferedWriter( new OutputStreamWriter(new FileOutputStream(db_location,true),"UTF-8"));


            //FileWriter writer=new FileWriter(db_location,true);
            writer.write(dbr.toString().replaceAll("\n","<div>")+"\n");
            writer.flush();
            writer.close();

        }catch (IOException e) {
            System.out.println("Failed to insert "+e);
        }
    }
    public static String[] readAll(){

        try(  InputStreamReader fileReader = new InputStreamReader(new FileInputStream(db_location),"UTF-8");) {
            BufferedReader bufferedReader = new BufferedReader(fileReader);
            String everything = "";
            String line = null;
            while((line = bufferedReader.readLine()) != null) {
                everything += line+"\n";
            }
            bufferedReader.close();
            System.out.print(everything);
            return everything.split("\n");
            // do something with everything string
        }catch (Exception e){
            System.out.println("Failed to read "+e);

            return null;
        }
    }

}
