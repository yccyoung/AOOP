import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class CustomDialog extends JDialog {
    private JCheckBox checkBox1;
    private JCheckBox checkBox2;
    private JCheckBox checkBox3;

    private boolean option1Selected;
    private boolean option2Selected;
    private boolean option3Selected;
    public CustomDialog(Frame parent) {
        super(parent, "Set Options", true);
        JPanel panel = new JPanel(new GridLayout(4, 1));
        checkBox1 = new JCheckBox("Display target equation");
        checkBox2 = new JCheckBox("Set target is not fixed");
        checkBox3 = new JCheckBox("Display error message");

        // Set the font size of the checkbox
        Font checkBoxFont = new Font("Arial", Font.BOLD, 25);
        checkBox1.setFont(checkBoxFont);
        checkBox2.setFont(checkBoxFont);
        checkBox3.setFont(checkBoxFont);

        panel.add(checkBox1);
        panel.add(checkBox2);
        panel.add(checkBox3);

        JButton okButton = new JButton("OK");

        // Set the font size of the button
        Font buttonFont = new Font("Arial", Font.PLAIN, 25);
        okButton.setFont(buttonFont);

        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                option1Selected = checkBox1.isSelected();
                option2Selected = checkBox2.isSelected();
                option3Selected = checkBox3.isSelected();
                dispose();
            }
        });

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER)); // Centered layout
        buttonPanel.add(okButton);
        panel.add(buttonPanel);
        getContentPane().add(panel);
        pack();
        setLocationRelativeTo(parent);
    }

    public boolean isOption1Selected() {
        return option1Selected;
    }

    public boolean isOption2Selected() {
        return option2Selected;
    }
    public boolean isOption3Selected() {
        return option3Selected;
    }
}
