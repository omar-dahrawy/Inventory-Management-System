package View.MainPanels;

import Controller.SystemController;
import Model.Constants;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoragePanel extends JPanel implements MainPanel, ActionListener {

    private final HomeView homeView;
    private JPanel storagePanel;
    private final Constants K;

    //  VIEW STORAGE

    private JTable storageTable;
    private JPanel tablePanel;

    private JComboBox<String> filterProductsComboBox;
    private JComboBox<String> filterContainerComboBox;

    private JRadioButton filterProductButton;
    private JRadioButton filterContainerButton;
    private ButtonGroup buttonGroup;

    private JButton viewStorageButton;
    private JButton clearFiltersButton;
    private JButton deleteStorageButton;


    public StoragePanel(HomeView homeView) {
        this.homeView = homeView;
        K = new Constants();
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(storagePanel, BorderLayout.CENTER);

        storageTable = new JTable();
        tablePanel.add(new JScrollPane(storageTable));

        //  VIEW STORAGE

        buttonGroup = new ButtonGroup();
        buttonGroup.add(filterProductButton);
        buttonGroup.add(filterContainerButton);

        filterContainerComboBox.addItem("Select Container");
        filterContainerComboBox.addItem(K.container_Tank);
        filterContainerComboBox.addItem(K.container_Carton);
        filterContainerComboBox.addItem(K.container_Drum);
        filterContainerComboBox.addItem(K.container_Gallon);
        filterContainerComboBox.addItem(K.container_Pail);
    }

    public void getStorageItems(ResultSet items, SystemController controller) throws SQLException {
        int columnCount = items.getMetaData().getColumnCount();
        int rowCount = getRowCount(items);

        String[] columnNames = getColumnNames(items, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (items.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[items.getRow() - 1][i] = items.getString(i + 1);
            }
        }
        storageTable = new JTable(data, columnNames);
        setTableFont(storageTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(storageTable));
        storageTable.getModel().addTableModelListener(controller);

        this.validate();
    }


    //  VIEW STORAGE GETTERS


    public JTable getStorageTable() {
        return storageTable;
    }

    public String getFilterProduct() {
        return filterProductsComboBox.getSelectedItem().toString();
    }

    public String getFilterContainerType() {
        return filterContainerComboBox.getSelectedItem().toString();
    }

    public boolean getFilterProductSelected() {
        return filterProductButton.isSelected();
    }

    public boolean getFilterContainerSelected() {
        return filterContainerButton.isSelected();
    }

    public JButton getViewStorageButton() {
        return viewStorageButton;
    }

    public JButton getDeleteStorageButton() {
        return deleteStorageButton;
    }


    //  OTHER METHODS


    public void getFormulas() {
        JTable formulasTable = homeView.getFormulasPanel().getFormulasTable();
        int rowCount = formulasTable.getRowCount();

        filterProductsComboBox.removeAllItems();
        filterProductsComboBox.addItem("Select Product");

        for (int i = 0 ; i < rowCount ; i++) {
            filterProductsComboBox.addItem(formulasTable.getValueAt(i, 0).toString());
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
        filterProductButton.addActionListener(this);
        filterContainerButton.addActionListener(this);
        viewStorageButton.addActionListener(controller);
        clearFiltersButton.addActionListener(this);
        deleteStorageButton.addActionListener(controller);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearFiltersButton) {
            filterProductsComboBox.setEnabled(false);
            filterContainerComboBox.setEnabled(false);
            buttonGroup.clearSelection();
        } else {
            filterContainerComboBox.setEnabled(filterContainerButton.isSelected());
            filterProductsComboBox.setEnabled(filterProductButton.isSelected());
        }
    }
}
