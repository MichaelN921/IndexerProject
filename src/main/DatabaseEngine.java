package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DatabaseEngine {

    public record Pokemon(int number, String name, String type, int total, int hp, int attack, int defence, int spAttack, int spDefence, int speed, int generation, boolean legendary){
        @Override
        public int number() {
            return number;
        }
        @Override
        public String name() {
            return name;
        }
        @Override
        public String type() {
            return type;
        }
        @Override
        public int total() {
            return total;
        }
        @Override
        public int hp() {
            return hp;
        }
        @Override
        public int attack() {
            return attack;
        }
        @Override
        public int defence() {
            return defence;
        }
        @Override
        public int spAttack() {
            return spAttack;
        }
        @Override
        public int spDefence() {
            return spDefence;
        }
        @Override
        public int speed() {
            return speed;
        }
        @Override
        public int generation() {
            return generation;
        }
        @Override
        public boolean legendary() {
            return legendary;
        }
    }

    public static boolean isNumeric(String strNum) {
        if (strNum == null) {
            return false;
        }
        try {
            double d = Double.parseDouble(strNum);
        } catch (NumberFormatException nfe) {
            return false;
        }
        return true;
    }


    public static void writeBinaryFile(String fileLocation, List<String> fileData){
        File file = new File(fileLocation);
        File index = new File("src/main/index.out");

        try (RandomAccessFile data = new RandomAccessFile(file, "rw");
             RandomAccessFile i = new RandomAccessFile(index, "rw"))
        {
            long b = 0;
            int entry = 0;
            for(String line : fileData){
                String[] props = line.split(",");
                int propInt;
                boolean propBool;
                int len = 0;
                for(String prop : props){
                    if (isNumeric(prop)) {
                        propInt = Integer.parseInt(prop);
                        data.writeInt(propInt);
                        len+=4;
                    }
                    else if ("true".equals(prop) || "false".equals(prop)) {
                        propBool = "true".equals(prop);
                        data.writeBoolean(propBool);
                        len+=1;
                    }
                    else {
                        data.writeInt(prop.length());
                        data.writeChars(prop);
                        // a char is 2 bytes, an int is 4
                        len += (prop.length() * 2) + 4;
                    }
                }
                i.writeInt(entry);
                i.writeLong(b);
                b += len;
                entry++;
            }
            // indicates end of index file
            i.writeInt(-1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Pokemon readBinaryFile(String fileLocation, int id) {
        File file = new File(fileLocation);
        File index = new File("src/main/index.out");

        try (RandomAccessFile data = new RandomAccessFile(file, "rw");
             RandomAccessFile in = new RandomAccessFile(index, "rw"))
        {
            int numChars;
            List<Character> charList;
            int nextInt;
            while ((nextInt = in.readInt()) != -1){
                if (nextInt == id){
                    long offset = in.readLong();
                    data.seek(offset);
                    int number = data.readInt();

                    numChars = data.readInt();
                    charList = new ArrayList<>();
                    for (int i=0;i<numChars;i++){
                        charList.add(data.readChar());
                    }
                    String name = charArrayToString(charList);

                    numChars = data.readInt();
                    charList = new ArrayList<>();
                    for (int i=0;i<numChars;i++){
                        charList.add(data.readChar());
                    }
                    String type = charArrayToString(charList);

                    int total = data.readInt();
                    int hp = data.readInt();
                    int attack = data.readInt();
                    int defense = data.readInt();
                    int spAttack = data.readInt();
                    int spDefense = data.readInt();
                    int speed = data.readInt();
                    int gen = data.readInt();
                    boolean legendary = data.readBoolean();

                    return new Pokemon(number, name, type, total, hp,
                                                attack, defense, spAttack, spDefense,
                                                speed, gen, legendary);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    // no longer needed but still cool (will delete later if not needed for anything)
    public static List<Character> asciiToCharArray(List<Integer> asciiValues){
        return asciiValues.stream().map(v -> (char) ((int) v)).toList();
    }

    private static String charArrayToString(List<Character> charList){
        return charList.stream().map(String::valueOf).collect(Collectors.joining());
    }

    public static void main(String[] args) {
        writeBinaryFile("src/main/data.out", DatabaseImportData.readFile("src/main/Pokemon.csv"));
        System.out.println(readBinaryFile("src/main/data.out", 100));
    }

}
