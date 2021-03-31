package View.HelperPanels;

import Controller.SystemController;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AddProductionView extends JFrame {

    private Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
    private Font font;

    private HomeView homeView;
    private SystemController controller;

    private JPanel ApPanel;

    private JComboBox ApFormulasComboBox;
    private JTextField ApProductionQuantityField;
    private JTextField ApBatchQuantityField;
    private JTextArea ApOrdersArea;
    private JButton ApAddButton;


    public AddProductionView(SystemController controller, HomeView homeView) {
        this.controller = controller;
        this.homeView = homeView;

        getFormulas();
        add(ApPanel);

        ApAddButton.addActionListener(controller);

        setSize(600,500);
        setLocation(screenDimensions.width/2 - getSize().width/2, screenDimensions.height/2 - getSize().height/2);

        setResizable(false);
        setVisible(true);
    }

    private void getFormulas() {
        int formulasCount = homeView.getFormulasPanel().getFormulasTable().getRowCount();
        ApFormulasComboBox.addItem("Select Formula");

        for (int i = 0 ; i < formulasCount ; i++) {
            ApFormulasComboBox.addItem(homeView.getFormulasPanel().getFormulasTable().getValueAt(i, 0));
        }

    }

    public String getApFormula() {
        return ApFormulasComboBox.getSelectedItem().toString();
    }

    public Double getApProductionQuantity() {
        if (!ApProductionQuantityField.getText().equals("")) {
            try {
                return Double.parseDouble(ApProductionQuantityField.getText());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            return 0.0;
        }
        return -13.11;
    }

    public Double getApBatchQuantity() {
        if (!ApBatchQuantityField.getText().equals("")) {
            try {
                return Double.parseDouble(ApBatchQuantityField.getText());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            return 0.0;
        }
        return -13.11;
    }

    public Object[] getApOrders() {
        Object[] array = ApOrdersArea.getText().split("\n");
        return array;
    }

}
