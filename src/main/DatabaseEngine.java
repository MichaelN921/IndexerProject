package main;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.zip.DeflaterOutputStream;

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

    private static boolean isNumeric(String strNum) {
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
        File dataFile = new File(fileLocation);
        File indexFile = new File("src/main/pokemon.index");

        try (RandomAccessFile data = new RandomAccessFile(dataFile, "rw");
             RandomAccessFile index = new RandomAccessFile(indexFile, "rw"))
        {
            long startingByte = 0;
            int entry = 0;
            for(String line : fileData){
                String[] props = line.split(",");
                int entryByteLen = 0;
                for(String prop : props){
                    entryByteLen += writeData(prop, data);
                }
                index.writeInt(entry);
                index.writeLong(startingByte);
                startingByte += entryByteLen;
                entry++;
            }
            // indicates end of index file
            index.writeInt(-1);
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static int writeData(String prop, RandomAccessFile data) throws IOException {
        int propInt;
        boolean propBool;
        int len = 0;
        if (isNumeric(prop)) {
            propInt = Integer.parseInt(prop);
            data.writeInt(propInt);
            // ints are 4 bytes
            len+=4;
        }
        else if ("true".equals(prop) || "false".equals(prop)) {
            propBool = "true".equals(prop);
            data.writeBoolean(propBool);
            // boolean is 1 byte
            len+=1;
        }
        else {
            data.writeInt(prop.length());
            data.writeChars(prop);
            // a char is 2 bytes, an int is 4
            len += (prop.length() * 2) + 4;
        }
        return len;
    }

    public static Pokemon readBinaryFile(String fileLocation, int id) {
        File file = new File(fileLocation);
        File index = new File("src/main/pokemon.index");

        try (RandomAccessFile data = new RandomAccessFile(file, "rw");
             RandomAccessFile in = new RandomAccessFile(index, "rw"))
        {
            int nextInt;
            while ((nextInt = in.readInt()) != -1){
                if (nextInt == id){
                    return readPokemon(in.readLong(), data);
                }
            }
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    public static List<Pokemon> readEntireBinaryFile(String fileLocation){
        File file = new File(fileLocation);
        File index = new File("src/main/pokemon.index");

        try (RandomAccessFile data = new RandomAccessFile(file, "rw");
             RandomAccessFile in = new RandomAccessFile(index, "rw"))
        {
            List<Pokemon> pokemon = new ArrayList<>();
            // skip the first line which is a header
            in.readInt();
            in.readLong();
            while (in.readInt() != -1){
                pokemon.add(readPokemon(in.readLong(), data));
            }
            return pokemon;
        }
        catch (IOException e){
            e.printStackTrace();
        }
        return null;
    }

    private static Pokemon readPokemon(long offset, RandomAccessFile data) throws IOException {
        data.seek(offset);

        int number = data.readInt();
        String name = readString(data);
        String type = readString(data);
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

    private static String readString(RandomAccessFile data) throws IOException {
        int numChars = data.readInt();
        List<Character> charList = new ArrayList<>();
        for (int i=0;i<numChars;i++){
            charList.add(data.readChar());
        }
        return charArrayToString(charList);
    }

    // no longer needed but still cool (will delete later if not needed for anything)
    private static List<Character> asciiToCharArray(List<Integer> asciiValues){
        return asciiValues.stream().map(v -> (char) ((int) v)).toList();
    }

    private static String charArrayToString(List<Character> charList){
        return charList.stream().map(String::valueOf).collect(Collectors.joining());
    }

    /*public static void main(String[] args) {
        writeBinaryFile("src/main/pokemon.data", DatabaseImportData.readFile("src/main/Pokemon.csv"));
        System.out.println(readBinaryFile("src/main/pokemon.data", 100));
    }*/

}
