package View;

import Controller.SystemController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class CreateFormulaView extends JFrame implements ActionListener {

    private Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

    private HomeView homeView;
    private SystemController controller;
    private ArrayList<JCheckBox> checkBoxes = new ArrayList<>();
    private ArrayList<JTextField> textFields = new ArrayList<>();

    private JPanel CfPanel;
    private JTextField CfNameField;
    private JPanel CfMaterialsPanel;
    private JButton createFormulaButton;

    public CreateFormulaView(SystemController controller, HomeView homeView) {
        this.controller = controller;
        this.homeView = homeView;

        add(CfPanel);

        setSize(600,500);
        setLocation(screenDimensions.width/2 - getSize().width/2, screenDimensions.height/2 - getSize().height/2);

        setResizable(false);
        setVisible(true);

        getMaterials();
        createFormulaButton.addActionListener(controller);
    }

    private void getMaterials() {

        int materialCount = homeView.getRawMaterialsPanel().getMaterialsTable().getRowCount();
        
        CfMaterialsPanel.setLayout(new GridLayout(materialCount+1,1));

        JLabel label1 = new JLabel("Materials");
        JLabel label2 = new JLabel("Quantity");
        label1.setHorizontalAlignment(JLabel.CENTER);
        label2.setHorizontalAlignment(JLabel.CENTER);

        Font font = new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize());
        label1.setFont(font);
        label2.setFont(font);


        JPanel panel = new JPanel(new GridLayout(1,2));
        panel.add(label1);
        panel.add(label2);

        CfMaterialsPanel.add(panel);

        for (int i = 0 ; i < materialCount ; i++) {
            JTextField field = new JTextField();
            field.setEnabled(false);
            textFields.add(field);

            String materialName = homeView.getRawMaterialsPanel().getMaterialsTable().getValueAt(i, 0).toString();
            JCheckBox checkBox = new JCheckBox(materialName);
            checkBox.addActionListener(this);
            checkBoxes.add(checkBox);

            String unit = " " + homeView.getRawMaterialsPanel().getMaterialsTable().getValueAt(i, 3).toString() + "   ";
            JPanel panel2 = new JPanel(new BorderLayout());
            panel2.add(field, BorderLayout.CENTER);
            panel2.add(new JLabel(unit), BorderLayout.EAST);

            panel = new JPanel(new GridLayout(1,2));
            panel.add(checkBox);
            panel.add(panel2);
            CfMaterialsPanel.add(panel);
        }
    }

    public String getFormulaName() {
        return CfNameField.getText();
    }

    public ArrayList<JCheckBox> getCheckBoxes() {
        return checkBoxes;
    }

    public ArrayList<JTextField> getTextFields() {
        return textFields;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        JCheckBox checkbox = (JCheckBox) e.getSource();
        boolean fieldEnabled = textFields.get(checkBoxes.indexOf(checkbox)).isEnabled();
        textFields.get(checkBoxes.indexOf(checkbox)).setEnabled(!fieldEnabled);
    }
}
