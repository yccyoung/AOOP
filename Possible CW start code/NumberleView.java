import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Locale;
import java.util.Observer;

public class NumberleView extends JFrame implements Observer {
    private JPanel gridPanel;
    private JPanel buttonPanel;
    private StringBuilder currentRowInput;
    private StringBuilder currentGuess; // Add StringBuilder to store current guesses
    private JLabel[][] gridLabels;
    private final INumberleModel model;
    private final NumberleController controller;

    public NumberleView(INumberleModel model, NumberleController controller) {
        this.controller = controller;
        this.model = model;
        this.controller.startNewGame();
        ((NumberleModel)this.model).addObserver(this);
        initializeFrame();
        this.controller.setView(this);
        update((NumberleModel)this.model, null);
    }

    public void initializeFrame() {
        setTitle("Numberle");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(1200, 1000);
        setLocationRelativeTo(null);

        // Initialize current row input
        currentRowInput = new StringBuilder();
        currentGuess = new StringBuilder(); // Initialize currentGuess

        // Create a grid panel
        gridPanel = new JPanel(new GridLayout(6, 7, 5, 5));
        add(gridPanel, BorderLayout.CENTER);
        initializeGrid();

        // Create a button panel and use the GridBagLayout layout manager
        buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        // Add Number Button (0-9)
        for (int i = 0; i < 10; i++) {
            addButton(String.valueOf(i), buttonPanel, gbc, i, 0);
        }

        // Add other buttons
        addButton("Del", buttonPanel, gbc, 0, 1);
        addButton("+", buttonPanel, gbc, 1, 1);
        addButton("-", buttonPanel, gbc, 2, 1);
        addButton("*", buttonPanel, gbc, 3, 1);
        addButton("/", buttonPanel, gbc, 4, 1);
        addButton("=", buttonPanel, gbc, 5, 1);
        addButton("Enter", buttonPanel, gbc, 6, 1,2);

        //Add new game start button
        addButton("New", buttonPanel, gbc, 8, 1,1);
        addButton("Set", buttonPanel, gbc, 9, 1,1);
        // Add the button panel to the OUT position of the main panel
        add(buttonPanel, BorderLayout.SOUTH);

        showMessageDialog("Guess the first equation");
    }

    // Method of adding buttons (without crossing columns)
    private void addButton(String label, JPanel panel, GridBagConstraints gbc, int column, int row) {
        addButton(label, panel, gbc, column, row, 1); //Calling overloaded methods, defaults to spanning 1 column
    }

    //Method of adding buttons (across columns)
    private void addButton(String label, JPanel panel, GridBagConstraints gbc, int column, int row, int width) {
        JButton button = new JButton(label);
        button.setPreferredSize(new Dimension(100 * width, 100)); //Set button size

        // Set the font size of the text inside the button
        Font buttonFont = button.getFont();
        Font newFont;
        if (label.equals("New")) {
            newFont = new Font(buttonFont.getName(), Font.BOLD, 33);
        } else {
            newFont = new Font(buttonFont.getName(), Font.BOLD, 40); // Font size for other buttons
        }
        button.setFont(newFont);

        button.addActionListener(new NumberleView.ButtonClickListener(label));
        // Set the background color of the button to light gray
        button.setBackground(Color.LIGHT_GRAY);

        // Set the position of the button in GridBagLayout and the number of columns it spans
        gbc.gridx = column;
        gbc.gridy = row;
        gbc.gridwidth = width;
        gbc.gridheight = 1;
        gbc.fill = GridBagConstraints.BOTH;
        gbc.insets = new Insets(5, 5, 5, 5);
        panel.add(button, gbc);
    }

    public void setButtonColor(StringBuilder currentGuess,int[] colorArray){
        for (int i = 0; i < currentGuess.length(); i++) {
            char c = currentGuess.charAt(i);

            // Convert characters to strings
            String characterAsString = String.valueOf(c);

            JButton button=findButtonByText(characterAsString);

            // Set the foreground color of the button to white
            button.setForeground(Color.WHITE);

            if(colorArray[i]==1){
                if(button.getBackground()==Color.lightGray) {
                    button.setBackground(Color.orange);
                }
            } else if (colorArray[i]==2) {
                button.setBackground(Color.green);
            } else if (colorArray[i]==3) {
                if(button.getBackground()==Color.lightGray ){
                    button.setBackground(Color.gray);
                }

            }
        }
    }
    public JButton findButtonByText(String text) {
        Component[] components = buttonPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                if (button.getText().equals(text)) {
                    return button;
                }
            }
        }
        return null; // If a button with specific text cannot be found, return null
    }

    //Initialize Grid
    private void initializeGrid() {
        gridLabels = new JLabel[6][7];
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                gridLabels[i][j] = new JLabel("", SwingConstants.CENTER);
                gridLabels[i][j].setPreferredSize(new Dimension(30, 30)); // Set the size of character labels
                gridLabels[i][j].setFont(new Font("Arial", Font.BOLD, 80)); // Set font size
                gridLabels[i][j].setBorder(BorderFactory.createLineBorder(Color.BLACK));
                gridPanel.add(gridLabels[i][j]);
            }
        }
    }

    // The method of clearing the input grid and restoring the initial color
    private void clearGridLabels() {
        for (int i = 0; i < 6; i++) {
            for (int j = 0; j < 7; j++) {
                gridLabels[i][j].setText("");
                gridLabels[i][j].setBackground(null); // Clear background color
                gridLabels[i][j].setForeground(Color.BLACK); // Restore font color to black
            }
        }
    }

    // Restore the initial background and font colors of the button panel
    private void restoreButtonPanel() {
        Component[] components = buttonPanel.getComponents();
        for (Component component : components) {
            if (component instanceof JButton) {
                JButton button = (JButton) component;
                button.setBackground(Color.LIGHT_GRAY); // Restore button background color
                button.setForeground(Color.BLACK); // Restore button font color
            }
        }
    }

    // The method of adding characters to the grid
    private void appendToGrid(String character) {
        if (currentRowInput.length() < 7) {
            currentRowInput.append(character);
            gridLabels[getCurrentRow()][currentRowInput.length() - 1].setText(character);
        }
    }

    // Method of deleting the last character on the grid
    private void deleteLastCharacter() {
        if (currentRowInput.length() > 0) {
            currentRowInput.deleteCharAt(currentRowInput.length() - 1);
            gridLabels[getCurrentRow()][currentRowInput.length()].setText("");
        }
    }

    //Set Error Popup
    private void showMessageDialog(String message) {
        setOptionPaneFont(); // Set font size
        JOptionPane optionPane = new JOptionPane(message, JOptionPane.INFORMATION_MESSAGE, JOptionPane.DEFAULT_OPTION, null, new Object[]{}, null);
        JDialog dialog = optionPane.createDialog(NumberleView.this, ""); // Set to no title
        dialog.setLocationRelativeTo(null);
        // Create a timer and close the dialog box in 2 seconds
        Timer timer = new Timer(1500, e -> dialog.dispose());
        timer.setRepeats(false); // Run only once
        timer.start();

        dialog.setModal(false);
        dialog.setVisible(true);
    }

    // Set error pop-up font size
    private void setOptionPaneFont() {
        Font font = new Font("Arial", Font.PLAIN, 30);
        UIManager.put("OptionPane.messageFont", font);
    }

    // Method of disabling input
    private void disableInput() {
        buttonPanel.setEnabled(false);
    }

    // Method for setting the background color of rows
    private void setRowColor(int row, int[] colorCode) {
        for (int i = 0; i < colorCode.length; i++) {
            if(colorCode[i]==1){
                gridLabels[row][i].setOpaque(true);
                gridLabels[row][i].setBackground(Color.ORANGE);
            } else if (colorCode[i]==2) {
                gridLabels[row][i].setOpaque(true);
                gridLabels[row][i].setBackground(Color.GREEN);
            } else if (colorCode[i]==3) {
                gridLabels[row][i].setOpaque(true);
                gridLabels[row][i].setBackground(Color.GRAY);
            }
        }
    }

    // Set the color of labels in the grid
    private void setGridLabelsColor(int currentRow) {
        for (int j = 0; j < 7; j++) {
            gridLabels[currentRow][j].setForeground(Color.white);
        }

    }

    // Button click event listener
    private class ButtonClickListener implements ActionListener {
        private String label;
        public ButtonClickListener(String label) {
            this.label = label;
        }

        @Override
        public void actionPerformed(ActionEvent e) {
            if (label.equals("Del")) {
                deleteLastCharacter();
            } else if (label.equals("Enter")) {
                currentGuess.setLength(0);
                currentGuess.append(currentRowInput.toString()).append("");
                controller.isGameWon(currentGuess.toString());
            } else if (label.equals("New")) {
                NewButtonOperation();
            }else if(label.equals("Set")){
                SetButtonOperation();
            }
            else {
                appendToGrid(label);
            }
        }
    }

    private void SetButtonOperation(){
        if(getCurrentRow()==0){
            CustomDialog customDialog = new CustomDialog(NumberleView.this);
            customDialog.setVisible(true);

            //Select the corresponding operation based on the user's flag selection
            if (customDialog.isOption1Selected() && customDialog.isOption2Selected()) {
                controller.flag1AndFlag2();
            }

            if (customDialog.isOption1Selected() && !customDialog.isOption2Selected()) {
                controller.flag1AndNotFlag2();
            }

            if(!customDialog.isOption1Selected() && customDialog.isOption2Selected()){
                controller.NotFlag1AndFlag2();
            }

            //Set the third flag
            if(customDialog.isOption3Selected()){
                controller.setFlag3();
            }
        }else{
            showMessageDialog("During the game!!!");
        }
    }

    private void NewButtonOperation(){
        if(controller.getRemainingAttempts()<=5 || controller.isGameOver()){
            // Set default language to English
            Locale.setDefault(Locale.ENGLISH);
            // Set UIManager properties to force JOptionPanel buttons to display in English
            UIManager.put("OptionPane.yesButtonText", "Yes");
            UIManager.put("OptionPane.noButtonText", "No");
            // Set button font
            Font buttonFont = new Font("Arial", Font.PLAIN, 20);
            UIManager.put("OptionPane.buttonFont", buttonFont);
            // Set button background color
            UIManager.put("Button.background", Color.white);

            int choice = JOptionPane.showConfirmDialog(NumberleView.this, "Start a new game?", null, JOptionPane.YES_NO_OPTION);
            if (choice == JOptionPane.YES_OPTION){
                startNewGame();
            }
        } else {
            showMessageDialog("Should enter an valid guess");
        }
    }

    // to start a new game
    private void startNewGame() {
        // Clear the current guess and input from the current line
        currentGuess.setLength(0);
        currentRowInput.setLength(0);
        // Clear the input grid and restore the initial color
        clearGridLabels();
        // Re enable button panel
        buttonPanel.setEnabled(true);
        // Restore the initial background and font colors of the button panel
        restoreButtonPanel();
        // Start a new game
        controller.startNewGame();
    }

    private void error6Operation(){
        int[] colorCode = controller.getColorArray();
        setRowColor(getCurrentRow()-1, colorCode);
        setGridLabelsColor(getCurrentRow()-1);

        currentRowInput = new StringBuilder();

        if (getCurrentRow() == 6) {
            showMessageDialog("You lost!!!");

            // Create a timer to delay calling the NewbuttonOperation() method
            Timer timer = new Timer(1500, new ActionListener() {
                @Override
                public void actionPerformed(ActionEvent e) {
                    NewButtonOperation(); // Delay calling the NewbuttonOperation() method
                }
            });
            timer.setRepeats(false); // Run Only Once
            timer.start();

            disableInput();
        }

        setButtonColor(controller.getCurrentGuess(), colorCode);
    }


    @Override
    public void update(java.util.Observable o, Object arg) {
        if (o instanceof NumberleModel) {
            if(!model.isGameOver()) {
                if(model.getFlag3()==1){
                    switch (model.getError()) {
                        case "1":
                            showMessageDialog("Null!!");
                            break;
                        case "2":
                            showMessageDialog("Too short");
                            break;
                        case "3":
                            showMessageDialog("No equal '=' sign");
                            break;
                        case "4":
                            showMessageDialog("The left side is not equal to the right");
                            break;
                        case "7":
                            showMessageDialog("There must be at least one sign +-*/");
                            break;
                        case "8":
                            showMessageDialog("Multiple math symbols in a row");
                            break;
                        case "6":
                            error6Operation();
                            break;
                        default:
                            break;
                    }
                }else{
                    //User requests not to display error messages
                    if(model.getError().equals("6")){
                        error6Operation();
                    }
                }
            }else if(model.isGameOver()){
                if(getCurrentRow()!=6){
                    int[] colorCode={2,2,2,2,2,2,2};
                    showMessageDialog("You win!!");
                    setRowColor(getCurrentRow(),colorCode);
                    setGridLabelsColor(getCurrentRow());
                    setButtonColor(controller.getCurrentGuess(), colorCode);

                    // Create a timer to delay calling the NewbuttonOperation() method
                    Timer timer = new Timer(1500, new ActionListener() {
                        @Override
                        public void actionPerformed(ActionEvent e) {
                            NewButtonOperation(); // Delay calling the NewbuttonOperation() method
                        }
                    });
                    timer.setRepeats(false); // Run Only Once
                    timer.start();

                    disableInput();
                } else if (getCurrentRow()==6) {
                    error6Operation();
                }
            }
        }
    }
    public int getCurrentRow(){
        return 6-controller.getRemainingAttempts();
    }
}