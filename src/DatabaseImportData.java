import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseImportData {
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

    public static void writeBinary(List<String> fileData){
        DatabaseEngine.writeBinaryFile("IndexerProject/src/data.out", fileData);

    }


    public static void main(String[] args) {
        writeBinary(readFile("IndexerProject/src/Pokemon.csv"));

    }
}
