import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.awt.event.MouseEvent;
import java.net.URI;


public class SmartKitchenPlanner extends JFrame {

    // Database Connection
    private static Connection conn;

    // GUI Components
    private JTextField ingredientField, amountField, timeField, nutritionField;
    private JTextArea outputArea;
    private Map<String, Integer> availableIngredients = new HashMap<>();

    // Initialize Recipes (Sample Dishes with Ingredient Amounts)
    private static void initializeDatabase() {
        try {
            conn = DriverManager.getConnection("jdbc:sqlite::memory:");
            Statement stmt = conn.createStatement();
            stmt.execute("CREATE TABLE recipes (name TEXT, ingredients TEXT, ingredient_amounts TEXT, time INTEGER, nutrition TEXT, link TEXT)");

// Add sample recipes with required ingredient amounts and links
            String[] recipes = {
                    "INSERT INTO recipes VALUES ('Tomato Pasta', 'tomato, pasta, olive oil', '2, 200, 10', 30, 'calories:500, protein:10g', 'https://www.allrecipes.com/recipe/11691/tomato-and-garlic-pasta/')",
                    "INSERT INTO recipes VALUES ('Salad', 'lettuce, tomato, cucumber, olive oil', '50, 1, 1, 5', 10, 'calories:150, protein:3g', 'https://foodflavorz.com/lettuce-tomato-cucumber-salad/')",
                    "INSERT INTO recipes VALUES ('Fruit Smoothie', 'milk, banana, honey', '200, 1, 10', 5, 'calories:200, protein:5g', 'https://www.inspiredtaste.net/19907/simple-banana-smoothie-recipe/')",
                    "INSERT INTO recipes VALUES ('Chicken Soup', 'chicken, carrot, celery', '100, 50, 50', 60, 'calories:250, protein:20g', 'https://www.indianhealthyrecipes.com/chicken-soup/')",
                    "INSERT INTO recipes VALUES ('Pancakes', 'flour, milk, egg', '100, 100, 1', 20, 'calories:300, protein:6g', 'https://www.eatingbirdfood.com/protein-pancakes/')",
                    "INSERT INTO recipes VALUES ('Veg Stir-fry', 'potato, capsicum, oil', '4, 2, 25', 15, 'calories:180, protein:4g', 'https://alishacooking.com/2016/04/25/capsicum-potato-stir-fry/')",
                    "INSERT INTO recipes VALUES ('Rice & Beans', 'rice, beans, onion', '150, 100, 1', 40, 'calories:400, protein:12g', 'https://dishingouthealth.com/spanish-rice-and-beans-one-pot/')",
                    "INSERT INTO recipes VALUES ('Grilled Cheese', 'bread, cheese, butter', '2, 50, 5', 10, 'calories:300, protein:7g', 'https://www.allrecipes.com/recipe/23891/grilled-cheese-sandwich/')",
                    "INSERT INTO recipes VALUES ('Omelette', 'egg, cheese, spinach', '2, 20, 30', 10, 'calories:220, protein:12g', 'https://www.allrecipes.com/recipe/69471/baby-spinach-omelet/')",
                    "INSERT INTO recipes VALUES ('Ladyfinger', 'ladyfinger, oil, chilli', '15, 25, 5', 15, 'calories:180, protein:3g', 'https://www.truemeds.in/blog/lady-finger-bhindi-uses-benefits-nutritional-value')"

            };
            for (String recipe : recipes) {
                stmt.execute(recipe);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }




    // Constructor for GUI Setup
    public SmartKitchenPlanner() {
        setTitle("Autonomous Smart Kitchen Planner");
        setSize(550, 750);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(248, 248, 255));  // Off-white background

        // Set layout to GridBagLayout for vertical alignment
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(10, 10, 10, 10); // padding for each component
        gbc.fill = GridBagConstraints.HORIZONTAL;

        // Define colors
        Color lightBlue = new Color(224, 174, 208);
        Color pastelBlue = new Color(255, 229, 229);
        Color darkPastelBlue = new Color(117, 106, 182);
        Color pastelPink = new Color(172, 135, 197);
        Color pastelDarkPink = new Color(172, 135, 197);

        // Title Label
        JLabel titleLabel = new JLabel("Smart Kitchen Planner");
        titleLabel.setFont(new Font("Arial", Font.BOLD, 24));
        titleLabel.setForeground(darkPastelBlue);
        gbc.gridx = 0; gbc.gridy = 0; gbc.gridwidth = 2;
        add(titleLabel, gbc);
//
        gbc.gridx = 0;
        gbc.gridy = 10;
        gbc.gridwidth = 2;
// Sources Label
        JLabel sourcesLabel = new JLabel("<html><u>Sources:<br></u>"
                + "<a href='https://weightlosssurgery.wustl.edu/wp-content/uploads/2015/09/Protein-List.pdf'>Protein List</a>");
        sourcesLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
        sourcesLabel.setForeground(darkPastelBlue);
        add(sourcesLabel, gbc);

// Enable clickable links
        sourcesLabel.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                try {
                    if (evt.getButton() == java.awt.event.MouseEvent.BUTTON1)
                    {
                        Desktop.getDesktop().browse(new URI(
                                "https://weightlosssurgery.wustl.edu/wp-content/uploads/2015/09/Protein-List.pdf"
                        ));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 1;
        JLabel ingredientLabel = new JLabel("Enter Ingredient:");
        ingredientLabel.setForeground(darkPastelBlue);
        add(ingredientLabel, gbc);

        gbc.gridx = 1;
        ingredientField = new JTextField(20);
        ingredientField.setBackground(lightBlue);
        add(ingredientField, gbc);

        // Amount Label and Field
        gbc.gridx = 0; gbc.gridy = 2;
        JLabel amountLabel = new JLabel("Enter Amount (g/ml/number):");
        amountLabel.setForeground(darkPastelBlue);
        add(amountLabel, gbc);

        gbc.gridx = 1;
        amountField = new JTextField(20);
        amountField.setBackground(lightBlue);
        add(amountField, gbc);

        // Add Ingredient Button
        gbc.gridx = 0; gbc.gridy = 3; gbc.gridwidth = 2;
        JButton addIngredientButton = new JButton("Add Ingredient");
        addIngredientButton.setBackground(pastelPink);
        addIngredientButton.setForeground(Color.BLACK);
        add(addIngredientButton, gbc);

        // Time Label and Field
        gbc.gridwidth = 1; gbc.gridx = 0; gbc.gridy = 4;
        JLabel timeLabel = new JLabel("Enter Available Time (min):");
        timeLabel.setForeground(darkPastelBlue);
        add(timeLabel, gbc);

        gbc.gridx = 1;
        timeField = new JTextField(20);
        timeField.setBackground(lightBlue);
        add(timeField, gbc);

        // Nutrition Label and Field
        gbc.gridx = 0; gbc.gridy = 5;
        JLabel nutritionLabel = new JLabel("Enter Nutrition Constraints (e.g., calories, protein):");
        nutritionLabel.setForeground(darkPastelBlue);
        add(nutritionLabel, gbc);

        gbc.gridx = 1;
        nutritionField = new JTextField(20);
        nutritionField.setBackground(lightBlue);
        add(nutritionField, gbc);

        // Suggest Recipes Button
        gbc.gridx = 0; gbc.gridy = 6; gbc.gridwidth = 2;
        JButton suggestButton = new JButton("Suggest Recipes");
        suggestButton.setBackground(pastelDarkPink);
        suggestButton.setForeground(Color.BLACK);
        add(suggestButton, gbc);

        // Output Area
        gbc.gridx = 0; gbc.gridy = 7; gbc.gridwidth = 2;
        outputArea = new JTextArea(15, 35);
        outputArea.setWrapStyleWord(true);
        outputArea.setLineWrap(true);
        outputArea.setEditable(false);
        outputArea.setBackground(pastelBlue);
        outputArea.setForeground(Color.BLACK);
        add(new JScrollPane(outputArea), gbc);

        // Add Ingredient Button Action
        addIngredientButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                String ingredient = ingredientField.getText().toLowerCase();
                int amount;
                try {
                    amount = Integer.parseInt(amountField.getText());
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(SmartKitchenPlanner.this, "Please enter a valid number for amount.");
                    return;
                }
                availableIngredients.put(ingredient, amount);
                ingredientField.setText("");
                amountField.setText("");
                JOptionPane.showMessageDialog(SmartKitchenPlanner.this, "Ingredient added!");
            }
        });

        // Suggest Recipes Button Action
        suggestButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                findMatchingRecipes();
            }
        });
    }

    // Method to Find Matching Recipes Based on Constraints
    private void findMatchingRecipes() {
        int availableTime = 0;
        try {
            availableTime = Integer.parseInt(timeField.getText());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid number for time.");
            return;
        }
        String nutritionConstraint = nutritionField.getText().toLowerCase();
        List<String> matchedRecipes = new ArrayList<>();

        try {
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM recipes");

            while (rs.next()) {
                String name = rs.getString("name");
                String[] ingredients = rs.getString("ingredients").split(", ");
                String[] ingredientAmounts = rs.getString("ingredient_amounts").split(", ");
                int time = rs.getInt("time");
                String nutrition = rs.getString("nutrition");
                String link = rs.getString("link");

                boolean meetsIngredients = true;
                for (int i = 0; i < ingredients.length; i++) {
                    String ingredient = ingredients[i];
                    int requiredAmount = Integer.parseInt(ingredientAmounts[i]);

                    if (!availableIngredients.containsKey(ingredient) || availableIngredients.get(ingredient) < requiredAmount) {
                        meetsIngredients = false;
                        break;
                    }
                }

                if (meetsIngredients && time <= availableTime && nutrition.contains(nutritionConstraint)) {
                    matchedRecipes.add(name + " (Time: " + time + " min, Nutrition: " + nutrition + ") \n Link to the recipe: " + link );
                }
            }

            outputArea.setText(matchedRecipes.isEmpty() ? "No matching recipes found." : String.join("\n", matchedRecipes));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        initializeDatabase();
        SwingUtilities.invokeLater(() -> {
            SmartKitchenPlanner app = new SmartKitchenPlanner();
            app.setVisible(true);
        });
    }
}