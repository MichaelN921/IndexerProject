package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import static java.lang.Character.isLowerCase;
import static java.lang.Character.isUpperCase;
import static main.DatabaseEngine.readBinaryFile;

public class DatabaseSearch extends JFrame implements ActionListener {
    String toggle = null;
    JFrame f;
    JTextField tf, tf1, tf2;
    JTable table;
    DefaultTableModel tableModel;
    JScrollPane scroll;
    JButton b;
    JLabel label, label1, label2, errorName, errorNum, errorNumSize;
    final JComboBox<String> cb;
    Map<String, Integer> pokemonIndex;
    NavigableMap<Integer, List<Integer>> indexTree;

    DatabaseSearch() {
        f = new JFrame("Pokedex DataBase");

        String[] columns = {"#","NAME","TYPE","TOTAL","HP","ATTACK","DEFENSE","spATTACK","spDEFENSE","SPEED","GENERATION","LEGENDARY"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        scroll = new JScrollPane(table);
        scroll.setBounds(20,200,600,200);

        b = new JButton("Search");
        b.setBounds(200,100,75,20);
        b.addActionListener(this);

        String[] searches = {"Exact Match","Range Query"};
        cb = new JComboBox<>(searches);
        cb.setBounds(50,100,90,20);

        tf = new JTextField();
        tf.setBounds(85,75,150,20);
        tf.setToolTipText("Enter Charmander");

        tf1 = new JTextField(); tf2 = new JTextField();
        tf1.setBounds(50,75,100,20);
        tf2.setBounds(200,75,100,20);

        label = new JLabel("Enter Pokemon Name");
        label.setBounds(97, 55, 150, 20);

        label1 = new JLabel("Enter min HP");
        label1.setBounds(60, 55, 150, 20);

        label2 = new JLabel("Enter max HP");
        label2.setBounds(220, 55, 150, 20);

        errorName = new JLabel("Be sure to capitalize the name!");
        errorName.setBounds(95, 40, 200, 20);

        errorNum = new JLabel("Be sure to enter integer values!");
        errorNum.setBounds(95, 40, 200, 20);

        errorNumSize = new JLabel("Be sure max HP is higher than min HP!");
        errorNumSize.setBounds(80, 40, 240, 20);

        f.add(cb); f.add(b); f.add(scroll); f.add(tf1); f.add(tf2); f.add(tf);
        f.add(label); f.add(label1); f.add(label2);
        f.add(errorName); f.add(errorNum); f.add(errorNumSize);
        f.setLayout(null);
        f.setSize(650,650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        label.setVisible(true);
        label1.setVisible(false);
        label2.setVisible(false);
        errorName.setVisible(false);
        errorNum.setVisible(false);
        errorNumSize.setVisible(false);

        comboBoxAction();

        cb.addActionListener(e -> comboBoxAction());
        populateHashMap();
        populateTree();
    }

    public void comboBoxAction(){
        String data = "" + cb.getItemAt(cb.getSelectedIndex());
        updateSearch(data);
    }

    public void updateSearch(String search) {
        clearErrors();
        if (search.equals("Exact Match")) {
            label.setVisible(true);
            label1.setVisible(false);
            label2.setVisible(false);
            tf.setVisible(true);
            tf1.setVisible(false);
            tf2.setVisible(false);
            toggle = "exact";
            revalidate();
            repaint();
        } else if (search.equals("Range Query")) {
            label.setVisible(false);
            label1.setVisible(true);
            label2.setVisible(true);
            tf1.setVisible(true);
            tf2.setVisible(true);
            tf.setVisible(false);
            toggle = "range";
            revalidate();
            repaint();
        }
    }

    public boolean checkName(String name) {
        char[] check = name.toCharArray();
        for (char c : check) {
            if (isUpperCase(check[0]) && isLowerCase(c)) {
                return true;
            }
        } return false;
    }

    public boolean checkNum(int num1, int num2) {
        if (num1<num2) {
            return true;
        } else {
            setErrorNumSize();
        } return false;
    }

    public void setErrorName() {
        errorName.setVisible(true);
    }

    public void setErrorNum() {
        errorNum.setVisible(true);
    }

    public void setErrorNumSize() {
        errorNumSize.setVisible(true);
    }

    public void clearErrors() {
        errorName.setVisible(false);
        errorNum.setVisible(false);
        errorNumSize.setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {
        clearErrors();
        if (Objects.equals(toggle, "exact")) {
            try {
                String name = tf.getText();
                if (checkName(name)) {
                    errorName.setVisible(false);
                    int number = pokemonIndex.get(name);
                    DatabaseEngine.Pokemon pokemon = readBinaryFile("src/main/pokemon.data", number);
                    tableModel.addRow(new Object[]{pokemon.number(), pokemon.name(), pokemon.type(),
                            pokemon.total(), pokemon.hp(), pokemon.attack(), pokemon.defense(), pokemon.spAttack(),
                            pokemon.spDefense(), pokemon.speed(), pokemon.generation(), pokemon.legendary()});
                } else {
                    setErrorName();
                }
            } catch (Exception ex) {
                System.out.println(ex);
            }
        } else if (Objects.equals(toggle, "range")) {
            try {
                int hp1 = Integer.parseInt(tf1.getText());
                int hp2 = Integer.parseInt(tf2.getText());
                DatabaseEngine.Pokemon pokemon;
                if (checkNum(hp1, hp2)) {
                    for(int i=hp1;i<=hp2;i++){
                        if(indexTree.get(i) != null){
                            List<Integer> numbers = indexTree.get(i);
                            for (int number : numbers){
                                pokemon = readBinaryFile("src/main/pokemon.data", number);
                                tableModel.addRow(new Object[]{pokemon.number(), pokemon.name(), pokemon.type(),
                                        pokemon.total(), pokemon.hp(), pokemon.attack(), pokemon.defense(), pokemon.spAttack(),
                                        pokemon.spDefense(), pokemon.speed(), pokemon.generation(), pokemon.legendary()});
                            }
                        }
                    }
                }
            } catch (NumberFormatException ex) {
                setErrorNum();
            }
        }
    }

    private void populateTree() {
        indexTree = new TreeMap<>();
        List<DatabaseEngine.Pokemon> pokemonList = DatabaseEngine.readEntireBinaryFile("src/main/pokemon.data");
        List<Integer> numbers;
        if (pokemonList != null) {
            for (DatabaseEngine.Pokemon pokemon: pokemonList) {
                if(indexTree.get(pokemon.hp()) == null){
                    numbers = new ArrayList<>();
                }
                else{
                    numbers = indexTree.get(pokemon.hp());
                }
                numbers.add(pokemon.number());
                indexTree.put(pokemon.hp(), numbers);
            }
        }
    }

    private void populateHashMap(){
        pokemonIndex = new DatabaseHashMap<>();
        List<DatabaseEngine.Pokemon> pokemonList = DatabaseEngine.readEntireBinaryFile("src/main/pokemon.data");
        if (pokemonList != null) {
            pokemonList.forEach(e -> pokemonIndex.put(e.name(), e.number()));
        }
    }

    public static void main(String[] args) {
        DatabaseSearch db = new DatabaseSearch();
        DatabaseEngine.writeBinaryFile("src/main/pokemon.data", DatabaseImportData.readFile("src/main/Pokemon.csv"));
    }
}
