package View.HelperPanels;

import javax.swing.*;
import java.awt.*;

public class ShowTextAreaView extends JDialog {
    private JPanel itemBatchesPanel;
    private JTextArea textArea;
    private JButton updateButton;
    private String textAreaText;

    private int row;

    public ShowTextAreaView(String inputString, int row) {
        this.row = row;
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setSize(350,250);
        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenDimensions.width/2 - getSize().width/2, screenDimensions.height/2 - getSize().height/2);

        initializeTextArea(inputString);
        add(itemBatchesPanel);

        setResizable(false);
        setVisible(true);
    }

    private void initializeTextArea(String inputString) {
        String[] array = inputString.split(",");

        textArea.append(array[0]);
        for (int i = 1 ; i < array.length ; i++) {
            textArea.append("\n" + array[i]);
        }

        textAreaText = textArea.getText();

        Font font = new Font(textArea.getFont().getName(), textArea.getFont().getStyle(), 18);
        textArea.setFont(font);
    }

    public JButton getUpdateButton() {
        return updateButton;
    }

    public String getTextArea() {
        return textArea.getText();
    }

    public String getTextAreaText() {
        return textAreaText;
    }

    public int getRow() {
        return row;
    }
}
