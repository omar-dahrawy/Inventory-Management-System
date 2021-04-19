package View.MainPanels;

import Controller.SystemController;
import Model.Constants;
import View.HomeView;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;

public class OrdersPanel extends JPanel implements MainPanel, ActionListener, PropertyChangeListener {

    private final HomeView homeView;
    private JPanel ordersPanel;
    private final Constants K;

    //  ADD ORDER

    private JTextField addCustomerField;
    private JTextField addBatchSerialField;
    private JTextArea addDetailsArea;
    private DatePicker addDopPicker;
    private DatePicker addDodPicker;
    private JPanel addDopPanel;
    private JPanel addDodPanel;
    private JButton addOrderButton;

    //  VIEW ORDERS

    private JTable ordersTable;
    private JPanel tablePanel;

    private JTextField filterCustomerField;
    private JTextField filterSerialField;
    private JComboBox<String> filterStatusComboBox;
    private JComboBox<String> filterDateComboBox;
    private DatePicker filterToDatePicker;
    private DatePicker filterFromDatePicker;
    private JPanel filterToDatePanel;
    private JPanel filterFromDatePanel;

    private JRadioButton filterDateButton;
    private JRadioButton filterCustomerButton;
    private JRadioButton filterStatusButton;
    private JRadioButton filterBatchSerialButton;
    private ButtonGroup buttonGroup;

    private JButton viewOrdersButton;
    private JButton clearFiltersButton;
    private JButton deleteOrderButton;

    public OrdersPanel(HomeView homeView) {
        this.homeView = homeView;
        K = new Constants();
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(ordersPanel, BorderLayout.CENTER);

        //  ADD ORDER

        addDopPicker = new DatePicker();
        addDodPicker = new DatePicker();
        addDopPicker.setDateToToday();
        addDodPicker.setDateToToday();
        addDopPanel.add(addDopPicker);
        addDodPanel.add(addDodPicker);

        //  VIEW ORDERS

        ordersTable = new JTable();
        tablePanel.add(new JScrollPane(ordersTable));

        buttonGroup = new ButtonGroup();
        buttonGroup.add(filterDateButton);
        buttonGroup.add(filterBatchSerialButton);
        buttonGroup.add(filterCustomerButton);
        buttonGroup.add(filterStatusButton);

        filterToDatePicker = new DatePicker();
        filterFromDatePicker = new DatePicker();
        filterToDatePicker.setDateToToday();
        filterFromDatePicker.setDateToToday();
        filterToDatePicker.setEnabled(false);
        filterFromDatePicker.setEnabled(false);
        filterToDatePanel.add(filterToDatePicker);
        filterFromDatePanel.add(filterFromDatePicker);

        filterStatusComboBox.addItem("Select Status");
        filterStatusComboBox.addItem(K.productionStatus_1);
        filterStatusComboBox.addItem(K.productionStatus_2);
        filterStatusComboBox.addItem(K.productionStatus_3);
        filterStatusComboBox.addItem(K.productionStatus_4);
    }

    public void showOrders(ResultSet orders, SystemController controller) throws SQLException {
        int columnCount = orders.getMetaData().getColumnCount();
        int rowCount = getRowCount(orders);

        String[] columnNames = getColumnNames(orders, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (orders.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[orders.getRow() - 1][i] = orders.getString(i + 1);
            }
        }
        ordersTable = new JTable(data, columnNames);
        setTableFont(ordersTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(ordersTable));

        ordersTable.getModel().addTableModelListener(controller);
        ordersTable.addPropertyChangeListener(this);

        this.validate();
    }

    public void clearAddFields() {
        addCustomerField.setText("");
        addBatchSerialField.setText("");
        addDetailsArea.setText("");
    }


    //  ADD ORDER GETTERS


    public String getAddCustomer() {
        return addCustomerField.getText();
    }

    public String getAddBatchSerial() {
        return addBatchSerialField.getText();
    }

    public String getAddDetails() {
        return addDetailsArea.getText();
    }

    public LocalDate getAddDop() {
        return addDopPicker.getDate();
    }

    public LocalDate getAddDod() {
        return addDodPicker.getDate();
    }

    public JButton getAddOrderButton() {
        return addOrderButton;
    }


    //  VIEW ORDERS GETTERS


    public JTable getOrdersTable() {
        return ordersTable;
    }

    public JRadioButton getFilterDateButton() {
        return filterDateButton;
    }

    public JRadioButton getFilterCustomerButton() {
        return filterCustomerButton;
    }

    public JRadioButton getFilterStatusButton() {
        return filterStatusButton;
    }

    public JRadioButton getFilterBatchSerialButton() {
        return filterBatchSerialButton;
    }

    public String getFilterCustomer() {
        return filterCustomerField.getText();
    }

    public String getFilterSerial() {
        return filterSerialField.getText();
    }

    public String getFilterStatus() {
        return filterStatusComboBox.getSelectedItem().toString();
    }

    public String getFilterDateType() {
        return filterDateComboBox.getSelectedItem().toString();
    }

    public LocalDate getFilterToDate() {
        return filterToDatePicker.getDate();
    }

    public LocalDate getFilterFromDate() {
        return filterFromDatePicker.getDate();
    }

    public JButton getViewOrdersButton() {
        return viewOrdersButton;
    }

    public JButton getDeleteOrderButton() {
        return deleteOrderButton;
    }


    //  OTHER METHODS


    void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
        viewOrdersButton.doClick();
    }

    void showDropBoxMessage() {
        String[] options = {"Select Status", K.productionStatus_1, K.productionStatus_2, K.productionStatus_3, K.productionStatus_4};
        String selectedStatus = (String)JOptionPane.showInputDialog(null, " \nChange order status:\n ",
                "Update status", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        int row = ordersTable.getSelectedRow();
        int column = ordersTable.getSelectedColumn();
        ordersTable.setValueAt(selectedStatus, row, column);
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
        addOrderButton.addActionListener(controller);
        viewOrdersButton.addActionListener(controller);
        clearFiltersButton.addActionListener(this);
        deleteOrderButton.addActionListener(controller);

        filterDateButton.addActionListener(this);
        filterCustomerButton.addActionListener(this);
        filterStatusButton.addActionListener(this);
        filterBatchSerialButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearFiltersButton) {
            filterCustomerField.setEnabled(false);
            filterDateComboBox.setEnabled(false);
            filterToDatePicker.setEnabled(false);
            filterFromDatePicker.setEnabled(false);
            filterStatusComboBox.setEnabled(false);
            filterSerialField.setEnabled(false);
            buttonGroup.clearSelection();
        } else {
            filterDateComboBox.setEnabled(filterDateButton.isSelected());
            filterToDatePicker.setEnabled(filterDateButton.isSelected());
            filterFromDatePicker.setEnabled(filterDateButton.isSelected());
            filterCustomerField.setEnabled(filterCustomerButton.isSelected());
            filterStatusComboBox.setEnabled(filterStatusButton.isSelected());
            filterSerialField.setEnabled(filterBatchSerialButton.isSelected());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == ordersTable) {
            if (evt.getPropertyName().equals("tableCellEditor")) {
                if (ordersTable.getColumnName(ordersTable.getSelectedColumn()).equals("Status")) {
                    showDropBoxMessage();
                } else if (ordersTable.getColumnName(ordersTable.getSelectedColumn()).equals("Order_ID")) {
                    showMessage("Error updating item","User ID cannot be edited.");
                }
            }
        }
    }
}
