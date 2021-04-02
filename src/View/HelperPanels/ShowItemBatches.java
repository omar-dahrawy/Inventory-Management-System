package View.HelperPanels;

import javax.swing.*;
import java.awt.*;

public class ShowItemBatches extends JDialog{
    private JPanel itemBatchesPanel;
    private JTextArea textArea;
    private JButton okButton;

    public ShowItemBatches(String itemBatches) {
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

        Font font = new Font(textArea.getFont().getName(), textArea.getFont().getStyle(), 18);
        textArea.setFont(font);
    }
}
