import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

public class DatabaseEngine {
    public static void writeBinaryFile(String fileLocation, List<String> fileData){
        File file = new File(fileLocation);
        File index = new File("IndexerProject/src/index.out");

        try (RandomAccessFile data = new RandomAccessFile(file, "rw");
             RandomAccessFile i = new RandomAccessFile(index, "rw"))
        {
            long b = 0;
            int entry = 0;
            for(String line : fileData){
                byte[] byteLine = line.getBytes(StandardCharsets.UTF_8);
                data.write(byteLine);
                i.writeBytes(entry + "," + b + "-");
                b += byteLine.length;
                i.writeBytes(b + "\n");
                entry++;
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static List<Integer> readBinaryFile(String fileLocation, int id) {
        File file = new File(fileLocation);
        File index = new File("IndexerProject/src/index.out");
        List<Integer> fileData = new ArrayList<>();
        List<String> fileIndex = new ArrayList<>();

        try (RandomAccessFile data = new RandomAccessFile(file, "rw");
             RandomAccessFile in = new RandomAccessFile(index, "rw"))
        {
            String readLine;
            while ((readLine = in.readLine()) != null){
                fileIndex.add(readLine);
            }
            List<Integer> dataIndex;
            for(String line : fileIndex){
                if (line.startsWith(id + ",")){
                    dataIndex = Arrays.stream(line.split(",")[1].split("-")).map(Integer::parseInt).collect(Collectors.toList());
                    int len = dataIndex.get(1)-dataIndex.get(0);
                    int offset = dataIndex.get(0);
                    data.seek(offset);
                    for(int i=0; i<len; i++){
                        fileData.add(data.read());
                    }
                }
            }

        }
        catch (IOException e){
            e.printStackTrace();
        }
        return fileData;
    }

    public static List<Character> asciiToCharArray(List<Integer> asciiValues){
        return asciiValues.stream().map(v -> (char) ((int) v)).toList();
    }

    public static void main(String[] args) {
        List<String> fileData = new ArrayList<>();
        fileData.add("Hello");
        fileData.add("Test");
//        writeBinaryFile("IndexerProject/src/test.out", fileData);
        List<Character> data = asciiToCharArray(readBinaryFile("IndexerProject/src/data.out", 14));
        data.forEach(System.out::print);
    }

}
