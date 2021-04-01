package View.MainPanels;

import Controller.SystemController;
import Model.Constants;
import View.HelperPanels.AddBatchContainersView;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;

public class BatchesPanel extends JPanel implements MainPanel, ActionListener, PropertyChangeListener {

    private JPanel batchesPanel;
    private HomeView homeView;
    private final Constants K;

    //  VIEW BATCHES

    private JTable batchesTable;
    private JPanel tablePanel;

    private JTextField filterBatchField;
    private JTextField filterProductionField;
    private JComboBox<String> filterFormulasComboBox;
    private JComboBox<String> filterStatusComboBox;

    private JRadioButton filterFormulaButton;
    private JRadioButton filterStatusButton;
    private JRadioButton filterBatchButton;
    private JRadioButton filterProductionButton;
    private ButtonGroup buttonGroup;

    private JButton viewBatchesButton;
    private JButton deleteBatchButton;
    private JButton clearFiltersButton;

    private AddBatchContainersView addBatchContainersView;

    public BatchesPanel(HomeView homeView) {
        this.homeView = homeView;
        K = new Constants();
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(batchesPanel, BorderLayout.CENTER);

        batchesTable = new JTable();
        tablePanel.add(new JScrollPane(batchesTable));

        //  VIEW BATCHES

        buttonGroup = new ButtonGroup();
        buttonGroup.add(filterBatchButton);
        buttonGroup.add(filterProductionButton);
        buttonGroup.add(filterFormulaButton);
        buttonGroup.add(filterStatusButton);

        filterStatusComboBox.addItem("Select Status");
        filterStatusComboBox.addItem(K.batchStatus_1);
        filterStatusComboBox.addItem(K.batchStatus_2);
        filterStatusComboBox.addItem(K.batchStatus_3);
    }

    public void viewBatches(ResultSet batches, SystemController controller) throws SQLException {
        int columnCount = batches.getMetaData().getColumnCount();
        int rowCount = getRowCount(batches);

        String[] columnNames = getColumnNames(batches, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (batches.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[batches.getRow() - 1][i] = batches.getString(i + 1);
            }
        }
        batchesTable = new JTable(data, columnNames);
        setTableFont(batchesTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(batchesTable));

        batchesTable.getModel().addTableModelListener(controller);
        batchesTable.addPropertyChangeListener(this);

        this.validate();
    }

    public void showAddBatchContainersView(SystemController controller, String batchSerial, String formulaName) {
        addBatchContainersView = new AddBatchContainersView(controller, homeView, batchSerial, formulaName);
    }

    public AddBatchContainersView getAddBatchContainersView() {
        return addBatchContainersView;
    }

    //  VIEW BATCHES GETTERS


    public JTable getBatchesTable() {
        return batchesTable;
    }

    public String getFilterBatch() {
        return filterBatchField.getText();
    }

    public String getFilterProduction() {
        return filterProductionField.getText();
    }

    public String getFilterFormula() {
        return filterFormulasComboBox.getSelectedItem().toString();
    }

    public String getFilterStatus() {
        return filterStatusComboBox.getSelectedItem().toString();
    }

    public boolean getFilterFormulaSelected() {
        return filterFormulaButton.isSelected();
    }

    public boolean getFilterStatusSelected() {
        return filterStatusButton.isSelected();
    }

    public boolean getFilterBatchSelected() {
        return filterBatchButton.isSelected();
    }

    public boolean getFilterProductionSelected() {
        return filterProductionButton.isSelected();
    }

    public JButton getViewBatchesButton() {
        return viewBatchesButton;
    }

    public JButton getDeleteBatchButton() {
        return deleteBatchButton;
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
        String[] options = {"Select Status", K.batchStatus_1, K.batchStatus_2, K.batchStatus_3};

        String selectedStatus = (String)JOptionPane.showInputDialog(null, " \nChange batch status:\n ",
                "Update status", JOptionPane.QUESTION_MESSAGE, null, options, options[0]);

        int row = batchesTable.getSelectedRow();
        int column = batchesTable.getSelectedColumn();
        batchesTable.setValueAt(selectedStatus, row, column);
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
        viewBatchesButton.addActionListener(controller);
        deleteBatchButton.addActionListener(controller);
        clearFiltersButton.addActionListener(this);
        filterBatchButton.addActionListener(this);
        filterProductionButton.addActionListener(this);
        filterFormulaButton.addActionListener(this);
        filterStatusButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearFiltersButton) {
            filterBatchField.setEnabled(false);
            filterProductionField.setEnabled(false);
            filterFormulasComboBox.setEnabled(false);
            filterStatusComboBox.setEnabled(false);
            buttonGroup.clearSelection();
        } else {
            filterBatchField.setEnabled(filterBatchButton.isSelected());
            filterProductionField.setEnabled(filterProductionButton.isSelected());
            filterFormulasComboBox.setEnabled(filterFormulaButton.isSelected());
            filterStatusComboBox.setEnabled(filterStatusButton.isSelected());
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == batchesTable) {
            if (evt.getPropertyName().equals("tableCellEditor")) {
                if (batchesTable.getColumnName(batchesTable.getSelectedColumn()).equals("Batch_status")) {
                    showDropBoxMessage();
                }
            }
        }
    }
}
