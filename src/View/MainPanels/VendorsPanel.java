package View.MainPanels;

import Controller.SystemController;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class VendorsPanel extends JPanel implements MainPanel, ActionListener {

    private JPanel vendorsPanel;
    private HomeView homeView;

    //  ADD VENDOR

    private JTextField addVendorNameField;
    private JTextField addContactNameField;
    private JTextField addContactNumberField;
    private JTextField addContactEmailField;
    private JButton addVendorButton;

    //  VIEW VENDORS

    private JTable vendorsTable;
    private JPanel tablePanel;

    private JTextField filterVendorNameField;
    private JTextField filterContactNameField;

    private JRadioButton filterVendorNameButton;
    private JRadioButton filterContactNameButton;
    private ButtonGroup buttonGroup;

    private JButton viewVendorsButton;
    private JButton clearFiltersButton;
    private JButton deleteVendorButton;

    public VendorsPanel(HomeView homeView) {
        this.homeView = homeView;
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(vendorsPanel, BorderLayout.CENTER);

        //  VIEW VENDORS

        vendorsTable = new JTable();
        tablePanel.add(new JScrollPane(vendorsTable));

        buttonGroup = new ButtonGroup();
        buttonGroup.add(filterContactNameButton);
        buttonGroup.add(filterVendorNameButton);
    }

    public void showVendors(ResultSet vendors, SystemController controller) throws SQLException {
        int columnCount = vendors.getMetaData().getColumnCount();
        int rowCount = getRowCount(vendors);

        String[] columnNames = getColumnNames(vendors, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (vendors.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[vendors.getRow() - 1][i] = vendors.getString(i + 1);
            }
        }
        vendorsTable.setAutoCreateRowSorter(false);
        vendorsTable.setRowSorter(null);
        vendorsTable = new JTable(data, columnNames);
        vendorsTable.setAutoCreateRowSorter(false);
        vendorsTable.setRowSorter(null);
        setTableFont(vendorsTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(vendorsTable));
        vendorsTable.getModel().addTableModelListener(controller);

        this.validate();
        homeView.getMaterialPurchasesPanel().getVendors();
    }

    public void clearAddFields() {
        addVendorNameField.setText("");
        addContactNameField.setText("");
        addContactNumberField.setText("");
        addContactEmailField.setText("");
    }


    //  ADD VENDOR GETTERS


    public String getAddVendorName() {
        return addVendorNameField.getText();
    }

    public String getAddContactName() {
        return addContactNameField.getText();
    }

    public String getAddContactNumber() {
        return addContactNumberField.getText();
    }

    public String getAddContactEmail() {
        return addContactEmailField.getText();
    }

    public JButton getAddVendorButton() {
        return addVendorButton;
    }


    //  VIEW VENDORS GETTERS


    public JTable getVendorsTable() {
        return vendorsTable;
    }

    public String getFilterVendorName() {
        return filterVendorNameField.getText();
    }

    public String getFilterContactName() {
        return filterContactNameField.getText();
    }

    public boolean getFilterVendorNameSelected() {
        return filterVendorNameButton.isSelected();
    }

    public boolean getFilterContactNameSelected() {
        return filterContactNameButton.isSelected();
    }

    public JButton getViewVendorsButton() {
        return viewVendorsButton;
    }

    public JButton getDeleteVendorButton() {
        return deleteVendorButton;
    }


    //  OTHER METHODS
    

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
        addVendorButton.addActionListener(controller);
        viewVendorsButton.addActionListener(controller);
        filterContactNameButton.addActionListener(this);
        filterVendorNameButton.addActionListener(this);
        clearFiltersButton.addActionListener(this);
        deleteVendorButton.addActionListener(controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearFiltersButton) {
            filterContactNameField.setEnabled(false);
            filterVendorNameField.setEnabled(false);
            buttonGroup.clearSelection();
        } else {
            filterVendorNameField.setEnabled(filterVendorNameButton.isSelected());
            filterContactNameField.setEnabled(filterContactNameButton.isSelected());
        }
    }
}
