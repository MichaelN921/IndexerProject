package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
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
    JLabel label, label1, label2, lblError, errorNum;
    final JComboBox<String> cb;
    Map<String, Integer> pokemonIndex;
    TreeMap<Integer, List<Integer>> indexTree;
    static String dataFileLocation = "src/main/pokemon.data";

    DatabaseSearch() {
        f = new JFrame("Pokedex DataBase");

        String[] columns = {"NUM","NAME","TYPE","TOTAL","HP","ATK","DEF","spATK","spDEF","SPEED","GEN","LEG"};
        tableModel = new DefaultTableModel(columns, 0);
        table = new JTable(tableModel);
        scroll = new JScrollPane(table);
        scroll.setBounds(20,200,600,200);

        b = new JButton("Search");
        b.setBounds(200,100,75,20);
        b.addActionListener(this);

        String[] searches = {"Exact Match","Range Query"};
        cb = new JComboBox<>(searches);
        cb.setBounds(50,100,100,20);

        tf = new JTextField();
        tf.setBounds(85,75,150,20);
        tf.setToolTipText("Enter Charmander");
        // fires when enter is pushed, can add to other tf but will need other validation
        tf.addActionListener(this);

        tf1 = new JTextField(); tf2 = new JTextField();
        tf1.setBounds(50,75,100,20);
        tf2.setBounds(200,75,100,20);
        tf1.setToolTipText("Enter 20");
        tf1.addActionListener(this);
        tf2.setToolTipText("Enter 50");
        tf2.addActionListener(this);

        label = new JLabel("Enter Pokemon Name");
        label.setBounds(97, 55, 150, 20);

        label1 = new JLabel("Enter min HP");
        label1.setBounds(60, 55, 150, 20);

        label2 = new JLabel("Enter max HP");
        label2.setBounds(220, 55, 150, 20);

        lblError = new JLabel();
        lblError.setBounds(95, 40, 250, 20);

        errorNum = new JLabel();
        errorNum.setBounds(95, 40, 200, 20);

        f.add(cb); f.add(b); f.add(scroll); f.add(tf1); f.add(tf2); f.add(tf);
        f.add(label); f.add(label1); f.add(label2);
        f.add(lblError); f.add(errorNum);
        f.setLayout(null);
        f.setSize(650,450);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);

        label.setVisible(true);
        label1.setVisible(false);
        label2.setVisible(false);
        lblError.setVisible(false);
        errorNum.setVisible(false);

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
            clearRows();
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
            clearRows();
            toggle = "range";
            revalidate();
            repaint();
        }
    }

    private void clearRows(){
        tableModel.setRowCount(0);
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
            setError("Be sure max HP is higher than min HP!");
        } return false;
    }

    public void setError(String message) {
        lblError.setText(message);
        lblError.setVisible(true);
    }

    public void clearErrors() {
        lblError.setVisible(false);
        errorNum.setVisible(false);
    }

    public void actionPerformed(ActionEvent e) {
        clearErrors();
        if (Objects.equals(toggle, "exact")) {
            try {
                String name = tf.getText();
                pokemonExactQuery(name);
            } catch (Exception ex) {
                System.out.println(ex);

            }
        } else if (Objects.equals(toggle, "range")) {
            try {
                int hp1 = Integer.parseInt(tf1.getText());
                int hp2 = Integer.parseInt(tf2.getText());
                pokemonRangeQuery(hp1, hp2);
            } catch (NumberFormatException ex) {
                setError("Be sure to enter integer values!");
            }
        }
    }

    private void pokemonExactQuery(String name){
        if (checkName(name)) {
            lblError.setVisible(false);
            if (!pokemonIndex.containsKey(name)) {
                setError("Pokemon does not exist, try Charizard!");
                return;
            }
            int number = pokemonIndex.get(name);
            DatabaseEngine.Pokemon pokemon = readBinaryFile(dataFileLocation, number);
            if (pokemon != null) {
                addRow(pokemon);
            }
        } else {
            setError("Be sure to capitalize the name!");
        }
    }

    private void pokemonRangeQuery(int num1, int num2){
        clearRows();
        DatabaseEngine.Pokemon pokemon;
        boolean rowAdded = false;
        if (checkNum(num1, num2)) {
            for(int i=num1;i<=num2;i++){
                if (indexTree.containsKey(i)) {
                    List<Integer> numbers = indexTree.get(i);
                    for (int number : numbers) {
                        pokemon = readBinaryFile(dataFileLocation, number);
                        if (pokemon != null) {
                            rowAdded = true;
                            addRow(pokemon);
                        }
                    }
                }
            }
            if (!rowAdded){
                setError("No Pokemon with these HP values!");
            }
        }
    }

    private void addRow(DatabaseEngine.Pokemon pokemon){
        tableModel.addRow(new Object[]{pokemon.number(), pokemon.name(), pokemon.type(),
                pokemon.total(), pokemon.hp(), pokemon.attack(), pokemon.defense(), pokemon.spAttack(),
                pokemon.spDefense(), pokemon.speed(), pokemon.generation(), pokemon.legendary()});
    }

    private void populateTree() {
        indexTree = new TreeMap<>();
        List<DatabaseEngine.Pokemon> pokemonList = DatabaseEngine.readEntireBinaryFile(dataFileLocation);
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
        List<DatabaseEngine.Pokemon> pokemonList = DatabaseEngine.readEntireBinaryFile(dataFileLocation);
        if (pokemonList != null) {
            pokemonList.forEach(e -> pokemonIndex.put(e.name(), e.number()));
        }
    }

    public static void main(String[] args) {
        DatabaseSearch db = new DatabaseSearch();
        DatabaseEngine.writeBinaryFile(dataFileLocation, DatabaseImportData.readFile("src/main/Pokemon.csv"));
    }
}
