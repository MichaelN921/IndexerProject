package main;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.List;
import java.util.Map;
import java.util.Objects;

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
    DatabaseTree indexTree;

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

        f.add(cb); f.add(b); f.add(scroll);
        f.setLayout(null);
        f.setSize(650,650);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        comboBoxAction();

        cb.addActionListener(e -> comboBoxAction());
        //populateHashMap();
    }

    public void comboBoxAction(){
        String data = "" + cb.getItemAt(cb.getSelectedIndex());

        updateSearch(data);
        updateFrame();
    }

    public void updateSearch(String search) {
        if (search.equals("Exact Match")) {
            tf = new JTextField();
            tf.setBounds(85,75,150,20);
            tf.setToolTipText("Enter Charmander");
            f.add(tf);
            toggle = "exact";
        } else if (search.equals("Range Query")) {
            tf1 = new JTextField(); tf2 = new JTextField();
            tf1.setBounds(50,75,100,20);
            tf2.setBounds(200,75,100,20);
            f.add(tf1); f.add(tf2);
            toggle = "range";
        }
    }

    public void updateFrame() {
        if (toggle.equals("exact")) {
            if (tf1 != null && tf1.isVisible() && tf2.isVisible()) {
                f.remove(tf1); f.remove(tf2);
                revalidate();
                repaint();
            }
        } else if (toggle.equals("range")) {
            if (tf.isVisible()) {
                f.remove(tf);
                revalidate();
                repaint();
            }
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (Objects.equals(toggle, "exact")) {
            populateHashMap();
            try {
                String name = tf.getText();
                int number = pokemonIndex.get(name);
                DatabaseEngine.Pokemon pokemon = DatabaseEngine.readBinaryFile("src/main/pokemon.data", number);
                tableModel.addRow(new Object[]{pokemon.number(), pokemon.name(), pokemon.type(),
                        pokemon.total(), pokemon.hp(), pokemon.attack(), pokemon.defense(), pokemon.spAttack(),
                        pokemon.spDefense(), pokemon.speed(), pokemon.generation(), pokemon.legendary()});

            } catch (Exception ex) {
                System.out.println(ex);
            }
        } else if (Objects.equals(toggle, "range")) {
            try {
                String name1 = tf1.getText();
                String name2 = tf2.getText();
                //int number1 = indexTree.;
                //int number2 = BST;

                System.out.println("RANGE!!");
            } catch (Exception ex) {
                System.out.println(ex);
            }
        }
    }

    private void populateTree() {
        indexTree = new DatabaseTree();

        //for (int data : )
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
