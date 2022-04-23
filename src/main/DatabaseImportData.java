package main;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/*
 * This class reads the data from the CSV and prepares it to convert into binary.
 * @author Josiah Kowalski and Michael Nasuti
 * @version 0.1
 */
public class DatabaseImportData {
    
    /*
    Reads entire CSV file.
    @param path  Filepath for file reader to use.
    @return fileData  A list containing all data read from the CSV.
     */
    public static List<String> readFile(String path){
        List<String> fileData = new ArrayList<>();
        try (FileReader fr = new FileReader(path);
             BufferedReader br = new BufferedReader(fr))
        {
            String line = br.readLine();
            while (line != null){
                fileData.add(line);
                line = br.readLine();
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return fileData;
    }
}
