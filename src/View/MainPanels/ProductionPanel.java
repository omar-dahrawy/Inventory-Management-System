package View.MainPanels;

import Controller.SystemController;
import Model.Constants;
import View.AddProductionView;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class ProductionPanel extends JPanel implements MainPanel, PropertyChangeListener, ActionListener {

    private HomeView homeView;
    private JPanel productionPanel;
    private Constants K;

    //  ADD PRODUCTION

    private JButton addNewProductionButton;
    private AddProductionView addProductionView;

    //  VIEW PRODUCTIONS

    private JTable productionTable;
    private JPanel tablePanel;

    private JTextField filterOrderIdField;
    private JTextField filterSerialField;
    private JComboBox<String> filterStatusComboBox;
    private JComboBox<String> filterFormulasComboBox;

    private JRadioButton filterSerialButton;
    private JRadioButton filterOrderIdButton;
    private JRadioButton filterFormulaButton;
    private JRadioButton filterStatusButton;
    private ButtonGroup buttonGroup;

    private JButton viewProductionsButton;
    private JButton clearFiltersButton;
    private JButton deleteProductionButton;

    public ProductionPanel(HomeView homeView) {
        this.homeView = homeView;
        K = new Constants();
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(productionPanel, BorderLayout.CENTER);

        //  VIEW PRODUCTIONS

        productionTable = new JTable();
        tablePanel.add(new JScrollPane(productionTable));

        buttonGroup = new ButtonGroup();
        buttonGroup.add(filterFormulaButton);
        buttonGroup.add(filterOrderIdButton);
        buttonGroup.add(filterSerialButton);
        buttonGroup.add(filterStatusButton);

        filterStatusComboBox.addItem("Select Status");
        filterStatusComboBox.addItem(K.productionStatus_1);
        filterStatusComboBox.addItem(K.productionStatus_2);
        filterStatusComboBox.addItem(K.productionStatus_3);
        filterStatusComboBox.addItem(K.productionStatus_4);
    }

    public void showAddProductionView(SystemController controller) {
        addProductionView = new AddProductionView(controller, homeView);
    }

    public void showProductions(ResultSet batches, SystemController controller) throws SQLException {
        int columnCount = batches.getMetaData().getColumnCount();
        int rowCount = getRowCount(batches);

        String[] columnNames = getColumnNames(batches, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (batches.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[batches.getRow()-1][i] = batches.getString(i+1);
            }
        }
        productionTable = new JTable(data, columnNames);
        setTableFont(productionTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(productionTable));

        productionTable.getModel().addTableModelListener(controller);
        productionTable.addPropertyChangeListener(this);

        this.validate();
    }


    //  ADD PRODUCTION GETTERS


    public JButton getAddNewProductionButton() {
        return addNewProductionButton;
    }

    public AddProductionView getAddProductionView() {
        return addProductionView;
    }


    //  VIEW PRODUCTION GETTERS


    public JTable getProductionTable() {
        return productionTable;
    }

    public String getFilterOrderId() {
        return filterOrderIdField.getText();
    }

    public String getFilterSerial() {
        return filterSerialField.getText();
    }

    public String getFilterStatus() {
        return filterStatusComboBox.getSelectedItem().toString();
    }

    public String getFilterFormula() {
        return filterFormulasComboBox.getSelectedItem().toString();
    }

    public Boolean getFilterSerialSelected() {
        return filterSerialButton.isSelected();
    }

    public Boolean getFilterOrderIdSelected() {
        return filterOrderIdButton.isSelected();
    }

    public Boolean getFilterFormulaSelected() {
        return filterFormulaButton.isSelected();
    }

    public Boolean getFilterStatusSelected() {
        return filterStatusButton.isSelected();
    }

    public JButton getViewProductionsButton() {
        return viewProductionsButton;
    }

    public JButton getDeleteProductionButton() {
        return deleteProductionButton;
    }


    //  OTHER METHODS


    public void getFormulas() {
        JTable formulasTable = homeView.getFormulasPanel().getFormulasTable();
        int rowCount = formulasTable.getRowCount();

        filterFormulasComboBox.removeAllItems();
        filterFormulasComboBox.addItem("Select Formula");

        for (int i = 0 ; i < rowCount ; i++) {
            filterFormulasComboBox.addItem(formulasTable.getValueAt(i, 0).toString());
        }
    }

    void showDropBoxMessage() {
        String[] options = {"Select Status", K.productionStatus_1, K.productionStatus_2, K.productionStatus_3, K.productionStatus_4};
        String selectedStatus = (String)JOptionPane.showInputDialog(null, " \nChange order status:\n ",
                "Update status", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        int row = productionTable.getSelectedRow();
        int column = productionTable.getSelectedColumn();
        productionTable.setValueAt(selectedStatus, row, column);
    }

    @Override
    public int getRowCount(ResultSet set) {
        int rowCount = 0;

        try {
            set.last();
            rowCount = set.getRow();
            set.beforeFirst();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
        return rowCount;
    }

    @Override
    public String[] getColumnNames(ResultSet set, int columnCount) {
        String[] columnNames = new String[columnCount];

        for (int i = 0 ; i < columnCount ; i++) {
            try {
                columnNames[i] = set.getMetaData().getColumnName(i+1);
                if (set.getMetaData().getTableName(1).equals("Material_Expenses")) {
                    if (columnNames[i].equals("Material_ID")) {
                        columnNames[i] = "Material Name";
                    }
                }
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        return columnNames;
    }

    @Override
    public void setTableFont(JTable table) {
        Font font = new Font(table.getFont().getName(), table.getFont().getStyle(), 15);
        table.setFont(font);
        table.setRowHeight(25);
        table.getTableHeader().setFont(font);
    }

    @Override
    public void addActionListeners(SystemController controller) {
        addNewProductionButton.addActionListener(controller);
        viewProductionsButton.addActionListener(controller);
        clearFiltersButton.addActionListener(this);
        deleteProductionButton.addActionListener(controller);

        filterFormulaButton.addActionListener(this);
        filterStatusButton.addActionListener(this);
        filterOrderIdButton.addActionListener(this);
        filterSerialButton.addActionListener(this);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == productionTable) {
            if (evt.getPropertyName().equals("tableCellEditor")) {
                if (productionTable.getColumnName(productionTable.getSelectedColumn()).equals("Production_status")) {
                    showDropBoxMessage();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearFiltersButton) {
            filterStatusComboBox.setEnabled(false);
            filterSerialField.setEnabled(false);
            filterFormulasComboBox.setEnabled(false);
            filterOrderIdField.setEnabled(false);
            buttonGroup.clearSelection();
        } else {
            filterStatusComboBox.setEnabled(filterStatusButton.isSelected());
            filterSerialField.setEnabled(filterSerialButton.isSelected());
            filterFormulasComboBox.setEnabled(filterFormulaButton.isSelected());
            filterOrderIdField.setEnabled(filterOrderIdButton.isSelected());
        }
    }
}
