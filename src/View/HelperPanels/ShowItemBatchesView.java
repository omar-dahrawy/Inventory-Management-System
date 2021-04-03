package View.HelperPanels;

import javax.swing.*;
import java.awt.*;

public class ShowItemBatchesView extends JDialog {
    private JPanel itemBatchesPanel;
    private JTextArea textArea;
    private JButton updateButton;
    private String textAreaText;

    private int row;
    private int column;

    public ShowItemBatchesView(String itemBatches, int row, int column) {
        setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);

        setSize(350,250);
        Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
        setLocation(screenDimensions.width/2 - getSize().width/2, screenDimensions.height/2 - getSize().height/2);

        initializeTextArea(itemBatches);
        add(itemBatchesPanel);

        setResizable(false);
        setVisible(true);
    }

    private void initializeTextArea(String itemBatches) {
        itemBatches = itemBatches.substring(1, itemBatches.length()-1);
        String[] array = itemBatches.split(",");

        textArea.append(array[0].substring(1, array[0].length()-1));
        for (int i = 1 ; i < array.length ; i++) {
            textArea.append("\n" + array[i].substring(1, array[i].length()-1));
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

    public int getColumn() {
        return column;
    }
}
