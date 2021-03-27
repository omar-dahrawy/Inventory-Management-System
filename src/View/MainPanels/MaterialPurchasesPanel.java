package View.MainPanels;

import Controller.SystemController;
import View.HomeView;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class MaterialPurchasesPanel extends JPanel implements MainPanel, ActionListener {

    private JPanel materialPurchasesPanel;
    private HomeView homeView;

    //  ADD MATERIAL PURCHASE

    private JComboBox addMaterialComboBox;
    private JComboBox addVendorComboBox;
    private DatePicker addDopPicker;
    private JPanel addDopPanel;
    private JTextField addQuantityField;
    private JTextField addInvoiceField;
    private JButton addPurchaseButton;

    //  VIEW MATERIAL PURCHASES

    private JTable purchasesTable;
    private JPanel tablePanel;

    private DatePicker filterFromDatePicker;
    private DatePicker filterToDatePicker;
    private JPanel filterFromDatePanel;
    private JPanel filterToDatePanel;

    private JRadioButton filterDopButton;
    private JRadioButton filterDoeButton;
    private ButtonGroup buttonGroup;

    private JButton viewPurchasesButton;
    private JButton clearFiltersButton;
    private JButton deletePurchaseButton;

    public MaterialPurchasesPanel(HomeView homeView) {
        this.homeView = homeView;
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(materialPurchasesPanel, BorderLayout.CENTER);

        //  ADD MATERIAL PURCHASE

        addDopPicker = new DatePicker();
        addDopPicker.setDateToToday();
        addDopPanel.add(addDopPicker);

        //  VIEW MATERIAL PURCHASES

        purchasesTable = new JTable();
        tablePanel.add(new JScrollPane(purchasesTable));

        buttonGroup = new ButtonGroup();
        buttonGroup.add(filterDoeButton);
        buttonGroup.add(filterDopButton);

        filterFromDatePicker = new DatePicker();
        filterToDatePicker = new DatePicker();
        filterFromDatePicker.setDateToToday();
        filterToDatePicker.setDateToToday();
        filterFromDatePicker.setEnabled(false);
        filterToDatePicker.setEnabled(false);
        filterFromDatePanel.add(filterFromDatePicker);
        filterToDatePanel.add(filterToDatePicker);
    }

    public void showMaterialExpenses(ResultSet results, SystemController controller) throws SQLException {
        int columnCount = results.getMetaData().getColumnCount();
        int rowCount = getRowCount(results);

        String[] columnNames = getColumnNames(results, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (results.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[results.getRow() - 1][i] = results.getString(i + 1);
            }
        }
        purchasesTable = new JTable(data, columnNames);
        setTableFont(purchasesTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(purchasesTable));

        purchasesTable.getModel().addTableModelListener(controller);

        this.validate();
    }

    public void clearAddFields() {
        addMaterialComboBox.setSelectedIndex(0);
        addVendorComboBox.setSelectedIndex(0);
        addQuantityField.setText("");
        addInvoiceField.setText("");
    }


    //  ADD MATERIAL PURCHASE GETTERS


    public String getAddMaterial() {
        return addMaterialComboBox.getSelectedItem().toString();
    }

    public int getAddVendor() {
        int row = addVendorComboBox.getSelectedIndex() - 1;

        return Integer.parseInt(homeView.getVendorsPanel().getVendorsTable().getValueAt(row, 0).toString());
    }

    public LocalDate getAddDop() {
        return addDopPicker.getDate();
    }

    public double getAddQuantity() {
        if (!addQuantityField.getText().equals("")) {
            try {
                return Double.parseDouble(addQuantityField.getText());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            return 0.0;
        }
        return -13.11;
    }

    public String getAddInvoice() {
        return addInvoiceField.getText();
    }

    public JButton getAddPurchaseButton() {
        return addPurchaseButton;
    }


    //  VIEW MATERIAL PURCHASES GETTERS


    public LocalDate getFilterFromDate() {
        return filterFromDatePicker.getDate();
    }

    public LocalDate getFilterToDate() {
        return filterToDatePicker.getDate();
    }

    public boolean getFilterDopSelected() {
        return filterDopButton.isSelected();
    }

    public boolean getFilterDoeSelected() {
        return filterDoeButton.isSelected();
    }

    public JButton getViewPurchasesButton() {
        return viewPurchasesButton;
    }

    public JButton getDeletePurchaseButton() {
        return deletePurchaseButton;
    }

    public JTable getPurchasesTable() {
        return purchasesTable;
    }


    //  OTHER METHODS


    public void getVendors() {
        JTable vendorsTable = homeView.getVendorsPanel().getVendorsTable();
        int rowCount = vendorsTable.getRowCount();

        addVendorComboBox.removeAllItems();
        addVendorComboBox.addItem("Select Vendor");

        for (int i = 0 ; i < rowCount ; i++) {
            addVendorComboBox.addItem(vendorsTable.getValueAt(i, 1).toString());
        }
    }

    public void getMaterials() {
        JTable materialsTable = homeView.getRawMaterialsPanel().getMaterialsTable();
        int rowCount = materialsTable.getRowCount();

        addMaterialComboBox.removeAllItems();
        addMaterialComboBox.addItem("Select Material");

        for (int i = 0 ; i < rowCount ; i++) {
            addMaterialComboBox.addItem(materialsTable.getValueAt(i, 0).toString());
        }
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
    }

    @Override
    public void addActionListeners(SystemController controller) {
        addPurchaseButton.addActionListener(controller);
        viewPurchasesButton.addActionListener(controller);
        deletePurchaseButton.addActionListener(controller);
        clearFiltersButton.addActionListener(this);
        filterDoeButton.addActionListener(this);
        filterDopButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearFiltersButton) {
            filterFromDatePicker.setEnabled(false);
            filterToDatePicker.setEnabled(false);
            buttonGroup.clearSelection();
        } else {
            filterFromDatePicker.setEnabled(true);
            filterToDatePicker.setEnabled(true);
        }
    }
}
