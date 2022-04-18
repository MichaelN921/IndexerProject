package main;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class DatabaseSearch extends JFrame implements ActionListener {
    String toggle = null;
    JFrame f;
    JTextField tf, tf1, tf2;
    JLabel label;
    JButton b;
    final JComboBox<String> cb;
    HashMap<String, Integer> pokemonIndex;
    DatabaseTree indexTree;

    DatabaseSearch() {
        f = new JFrame("Pokedex DataBase");
        label = new JLabel();
        label.setHorizontalAlignment(JLabel.CENTER);
        label.setSize(400,100);

        b = new JButton("Search");
        b.setBounds(200,100,75,20);
        b.addActionListener(this);

        String[] searches = {"Exact Match","Range Query"};
        cb = new JComboBox<>(searches);
        cb.setBounds(50,100,90,20);

        f.add(cb); f.add(label); f.add(b);
        f.setLayout(null);
        f.setSize(350,350);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        f.setVisible(true);
        comboBoxAction();

        cb.addActionListener(e -> comboBoxAction());
        //populateHashMap();
    }

    public void comboBoxAction(){
        String data = "" + cb.getItemAt(cb.getSelectedIndex());
        label.setText(data);

        updateSearch(data);
        updateFrame();
    }

    public void updateSearch(String search) {
        if (search.equals("Exact Match")) {
            tf = new JTextField();
            tf.setBounds(85,75,150,20);
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
                label.setText(DatabaseEngine.readBinaryFile("src/main/pokemon.data", number).toString());
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
        pokemonIndex = new HashMap<>();

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
