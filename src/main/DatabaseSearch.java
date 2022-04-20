package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.*;

import static main.DatabaseEngine.readBinaryFile;

public class DatabaseSearch extends JFrame implements ActionListener {
    String toggle = null;
    JFrame f;
    JTextField tf, tf1, tf2;
    JTable table;
    DefaultTableModel tableModel;
    JScrollPane scroll;
    JButton b;
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

        f.add(cb); f.add(b); f.add(scroll); f.add(tf1); f.add(tf2); f.add(tf);
        f.setLayout(null);
        f.setSize(650,650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
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
        if (search.equals("Exact Match")) {
            tf.setVisible(true);
            tf1.setVisible(false);
            tf2.setVisible(false);
            revalidate();
            repaint();
            toggle = "exact";
        } else if (search.equals("Range Query")) {
            tf1.setVisible(true);
            tf2.setVisible(true);
            tf.setVisible(false);
            revalidate();
            repaint();
            toggle = "range";
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(toggle, "exact")) {
            try {
                String name = tf.getText();
                int number = pokemonIndex.get(name);
                DatabaseEngine.Pokemon pokemon = readBinaryFile("src/main/pokemon.data", number);
                tableModel.addRow(new Object[]{pokemon.number(), pokemon.name(), pokemon.type(),
                        pokemon.total(), pokemon.hp(), pokemon.attack(), pokemon.defense(), pokemon.spAttack(),
                        pokemon.spDefense(), pokemon.speed(), pokemon.generation(), pokemon.legendary()});

            } catch (Exception ex) {
                System.out.println(ex);
            }
        } else if (Objects.equals(toggle, "range")) {
            try {
                int hp1 = Integer.parseInt(tf1.getText());
                int hp2 = Integer.parseInt(tf2.getText());
                //int number1 = indexTree.;
                //int number2 = BST;
//                List<DatabaseEngine.Pokemon> list = new ArrayList<>();
                DatabaseEngine.Pokemon pokemon;
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

                List<Integer> o;


                System.out.println("RANGE!!");
            } catch (Exception ex) {
                System.out.println(ex);
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
