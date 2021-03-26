package View.MainPanels;

import Controller.SystemController;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class GeneralPurchasesPanel extends JPanel implements MainPanel, ActionListener {

    private JPanel generalPurchasesPanel;

    //  ADD GENERAL PURCHASE

    private JTextField addItemField;
    private DatePicker addDopPicker;
    private JPanel addDopPanel;
    private JTextField addQuantityField;
    private JButton addPurchaseButton;

    //  VIEW GENERAL PURCHASES

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

    public GeneralPurchasesPanel() {
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(generalPurchasesPanel, BorderLayout.CENTER);

        //  ADD GENERAL PURCHASE

        addDopPicker = new DatePicker();
        addDopPicker.setDateToToday();
        addDopPanel.add(addDopPicker);

        //  VIEW GENERAL PURCHASES

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

    public void showGeneralPurchases(ResultSet results, SystemController controller) throws SQLException {
        int columnCount = results.getMetaData().getColumnCount();
        int rowCount = getRowCount(results);

        String[] columnNames = getColumnNames(results, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (results.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[results.getRow()-1][i] = results.getString(i+1);
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
        addItemField.setText("");
        addQuantityField.setText("");
    }

    //  ADD GENERAL PURCHASE GETTERS


    public String getAddItem() {
        return addItemField.getText();
    }

    public LocalDate getAddDop() {
        return addDopPicker.getDate();
    }

    public Double getAddQuantity() {
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

    public JButton getAddPurchaseButton() {
        return addPurchaseButton;
    }


    //  VIEW GENERAL PURCHASES GETTERS


    public JTable getPurchasesTable() {
        return purchasesTable;
    }

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
