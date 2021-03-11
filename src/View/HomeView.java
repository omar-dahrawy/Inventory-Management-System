package View;

import Controller.SystemController;
import com.github.lgooddatepicker.components.DatePicker;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;


public class HomeView implements ActionListener {

    private JPanel homePanel;

    /*
     *
     *      TABBED PANES
     *
     */

    private JTabbedPane mainTabbedPane;
    private JTabbedPane expensesTabbedPane;

    /*
     *
     *      MAIN PANELS
     *
     */

    private JPanel expensesPanel;
    private JPanel viewExpensesPanel;
    private JPanel ordersPanel;
    private JPanel materialsPanel;
    private JPanel batchesPanel;
    private JPanel formulasPanel;
    private JPanel vendorsPanel;
    private JPanel testingPanel;

    /*
     *
     *      ADD GENERAL EXPENSE
     *
     */

    private JTextField GeItemNameField;
    private JTextField GeQuantityField;
    private JPanel GeDatePickerPanel;
    private DatePicker GeDatePicker = new DatePicker();
    private JButton GeAddButton;

    /*
     *
     *      ADD MATERIAL EXPENSE
     *
     */

    private JComboBox MeComboBox;
    private ArrayList<String> materialUnits = new ArrayList<>();
    private ArrayList<String> materialIDs = new ArrayList<>();
    private JLabel MeQuantityLabel;
    private JTextField MeQuantityField;
    private JPanel MeDatePickerPanel;
    private DatePicker MeDatePicker = new DatePicker();
    private JButton MeAddButton;

    /*
     *
     *      VIEW EXPENSES
     *
     */

    private JTable VeTable;
    private JPanel VeTablePanel;
    private JLabel VeTotalExpensesLabel;
    private double VeTotalExpenses = 0;
    private JCheckBox VeGeCheckBox;
    private JCheckBox VeMeCheckBox;
    private ButtonGroup VeButtonGroup = new ButtonGroup();
    private JRadioButton VeDopRadioButton;
    private JRadioButton VeDoeRadioButton;
    private JPanel VeFromDatePanel;
    private JPanel VeToDatePanel;
    private DatePicker VeFromDatePicker = new DatePicker();
    private DatePicker VeToDatePicker = new DatePicker();
    private JButton VeViewButton;
    private JButton VeClearFilterButton;
    private JButton VeDeleteButton;

    /*
     *
     *      ADD ORDER
     *
     */

    private JTextField AoCustomerField;
    private JTextField AoPriceField;
    private JTextField AoBatchSerialField;
    private JTextArea AoDetailsArea;
    private JPanel AoDopPanel;
    private JPanel AoDodPanel;
    private DatePicker AoDopPicker = new DatePicker();
    private DatePicker AoDodPicker = new DatePicker();
    private JButton AoAddButton;

    /*
     *
     *      VIEW ORDERS
     *
     */

    private JPanel VoPanel;
    private JTable VoTable;
    private JPanel VoTablePanel;
    private ButtonGroup VoRadioButtonGroup = new ButtonGroup();
    private JRadioButton VoDateRadioButton;
    private JRadioButton VoCustomerRadioButton;
    private JRadioButton VoStatusRadioButton;
    private JRadioButton VoBatchRadioButton;
    private JButton VoViewButton;
    private JButton clearOrdersSelectionButton;
    private JTextField VoCustomerField;
    private JTextField VoSerialField;
    private JComboBox VoStatusComboBox;
    private JPanel VoFromDatePanel;
    private JPanel VoToDatePanel;
    private JComboBox VoDateComboBox;
    private DatePicker VoFromDatePicker = new DatePicker();
    private DatePicker VoToDatePicker = new DatePicker();
    private JButton deleteOrderButton;

    /*
     *
     *      VIEW MATERIALS
     *
     */

    private JPanel AmPanel;
    private JTextField AmNameField;
    private JTextField AmUnitField;
    private JButton AmAddButton;
    private JTable VmTable;
    private JPanel VmTablePanel;
    private JButton VmRefreshButton;

    /*
     *
     *      CREATE BATCH
     *
     */

    private JTextField CbSerialField;
    private JComboBox CbFormulaComboBox;
    private JTextArea CbOrderIDsArea;
    private JButton CbCreateButton;

    /*
     *
     *      VIEW BATCHES
     *
     */

    private JPanel VbPanel;
    private JTable VbTable;
    private JPanel VbTablePanel;
    private ButtonGroup VbRadioButtonGroup = new ButtonGroup();
    private JRadioButton VbSerialRadioButton;
    private JRadioButton VbOrderRadioButton;
    private JTextField VbSerialField;
    private JTextField VbOrderIdField;
    private JTextField VbFormulaField;
    private JButton VbViewButton;
    private JButton deleteBatchButton;
    private JButton clearBatchesSelectionButton;
    private JRadioButton VbFormulaRadioButton;

    /*
     *
     *      CREATE NEW FORMULA
     *
     */

    private CreateFormulaView CfView;
    private JButton createNewFormulaButton;

    /*
     *
     *      VIEW FORMULAS
     *
     */

    private JPanel VfTablePanel;
    private JButton VfRefreshButton;
    private JButton deleteFormulaButton;
    private JTable VfTable;

    /*
     *
     *      ADD VENDOR
     *
     */

    private JTextField AvVendorNameField;
    private JTextArea AvProductsArea;
    private JTextField AvContactNameField;
    private JTextField AvContactNumberField;
    private JTextField AvContactEmailField;
    private JButton AvAddButton;

    /*
     *
     *      VIEW VENDORS
     *
     */

    private JPanel VvPanel;
    private JPanel VvTablePanel;
    private JButton VvViewButton;
    private ButtonGroup VvRadioButtonGroup = new ButtonGroup();
    private JRadioButton VvVendorRadioButton;
    private JRadioButton VvContactRadioButton;
    private JTextField VvVendorField;
    private JTextField VvContactField;
    private JButton clearVendorsSelectionButton;
    private JButton deleteVendorButton;
    private JTable VvTable;

    /*
     *
     *      TESTING
     *
     */

    private JComboBox AtFormulaComboBox;
    private JPanel VtTablePanel;
    private JPanel VtPanel;
    private JTextField AtTestIdField;
    private JButton addTestButton;
    private JTable VtTable;

    /*
     *
     *      STANDARD SPECIFICATION
     *
     */

    private JTextField AsSpecificationIdField;
    private JPanel VsTablePanel;
    private JPanel VsPanel;
    private JTable VsTable;

    /*
     *  ------------------------
     *  ------------------------
     *      GENERAL SETTINGS
     *  ------------------------
     *  ------------------------
     */

    public HomeView() {
        GeDatePicker.setDateToToday();
        MeDatePicker.setDateToToday();
        GeDatePickerPanel.add(GeDatePicker);
        MeDatePickerPanel.add(MeDatePicker);

        materialUnits.add("");
        materialIDs.add("");
        MeComboBox.addItem("Select Material");

        VeButtonGroup.add(VeDopRadioButton);
        VeButtonGroup.add(VeDoeRadioButton);

        VeFromDatePicker.setEnabled(false);
        VeToDatePicker.setEnabled(false);

        VeFromDatePanel.add(VeFromDatePicker);
        VeToDatePanel.add(VeToDatePicker);

        AoDopPicker.setDateToToday();
        AoDodPicker.setDateToToday();
        AoDopPanel.add(AoDopPicker);
        AoDodPanel.add(AoDodPicker);

        VoFromDatePanel.add(VoFromDatePicker);
        VoFromDatePicker.setEnabled(false);
        VoToDatePanel.add(VoToDatePicker);
        VoToDatePicker.setEnabled(false);

        VoRadioButtonGroup.add(VoDateRadioButton);
        VoRadioButtonGroup.add(VoCustomerRadioButton);
        VoRadioButtonGroup.add(VoStatusRadioButton);
        VoRadioButtonGroup.add(VoBatchRadioButton);

        VbRadioButtonGroup.add(VbSerialRadioButton);
        VbRadioButtonGroup.add(VbOrderRadioButton);
        VbRadioButtonGroup.add(VbFormulaRadioButton);

        VvRadioButtonGroup.add(VvVendorRadioButton);
        VvRadioButtonGroup.add(VvContactRadioButton);

        VeTablePanel.add(new JScrollPane(VeTable));
        VoTablePanel.add(new JScrollPane(VoTable));
        VmTablePanel.add(new JScrollPane(VmTable));
        VbTablePanel.add(new JScrollPane(VbTable));
        VfTablePanel.add(new JScrollPane(VfTable));
        VvTablePanel.add(new JScrollPane(VvTable));
        VtTablePanel.add(new JScrollPane(VtTable));
        VsTablePanel.add(new JScrollPane(VsTable));
    }

    public void addActionListeners(SystemController controller) {
        MeAddButton.addActionListener(controller);
        GeAddButton.addActionListener(controller);
        MeComboBox.addActionListener(this);
        VeDopRadioButton.addActionListener(this);
        VeDoeRadioButton.addActionListener(this);
        VeViewButton.addActionListener(controller);
        VeDeleteButton.addActionListener(controller);
        VeClearFilterButton.addActionListener(this);

        AoAddButton.addActionListener(controller);
        VoViewButton.addActionListener(controller);
        VoDateRadioButton.addActionListener(this);
        VoCustomerRadioButton.addActionListener(this);
        VoStatusRadioButton.addActionListener(this);
        VoBatchRadioButton.addActionListener(this);
        clearOrdersSelectionButton.addActionListener(this);
        deleteOrderButton.addActionListener(controller);

        AmAddButton.addActionListener(controller);
        VmRefreshButton.addActionListener(controller);

        CbCreateButton.addActionListener(controller);
        VbSerialRadioButton.addActionListener(this);
        VbOrderRadioButton.addActionListener(this);
        VbFormulaRadioButton.addActionListener(this);
        VbViewButton.addActionListener(controller);
        clearBatchesSelectionButton.addActionListener(this);
        deleteBatchButton.addActionListener(controller);

        createNewFormulaButton.addActionListener(controller);
        VfRefreshButton.addActionListener(controller);
        deleteFormulaButton.addActionListener(controller);

        AvAddButton.addActionListener(controller);
        VvVendorRadioButton.addActionListener(this);
        VvContactRadioButton.addActionListener(this);
        VvViewButton.addActionListener(controller);
        clearVendorsSelectionButton.addActionListener(this);
        deleteVendorButton.addActionListener(controller);
    }

    public JPanel getHomePanel() {
        return homePanel;
    }

    /*
     *
     *      ADD GENERAL EXPENSE
     *
     */

    public String getGeItemName() {
        return GeItemNameField.getText();
    }

    public double getGeQuantity() {
        if (!GeQuantityField.getText().equals("")) {
            try {
                return Double.parseDouble(GeQuantityField.getText());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            return 0.0;
        }
        return -13.11;
    }

    public LocalDate getGeDate() {
        return GeDatePicker.getDate();
    }

    public JButton getGeAddButton() {
        return GeAddButton;
    }

    public void clearGeneralExpensesFields() {
        GeItemNameField.setText("");
        GeQuantityField.setText("");
    }

    /*
     *
     *      ADD MATERIAL EXPENSE
     *
     */

    public String getMaterialID() {
        return materialIDs.get(MeComboBox.getSelectedIndex());
    }

    public ArrayList<String> getMaterialIDs() {
        return materialIDs;
    }

    public JComboBox getMeComboBox() {
        return MeComboBox;
    }

    public double getMeQuantity() {
        if (!MeQuantityField.getText().equals("")) {
            try {
                return Double.parseDouble(MeQuantityField.getText());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            return 0.0;
        }
        return -13.11;
    }

    public LocalDate getMeDate() {
        return MeDatePicker.getDate();
    }

    public String getMaterialUnit() {
        return materialUnits.get(MeComboBox.getSelectedIndex());
    }

    public JButton getMeAddButton() {
        return MeAddButton;
    }

    public void clearMaterialExpensesFields() {
        MeComboBox.setSelectedIndex(0);
        MeQuantityField.setText("");
    }

    /*
     *
     *      VIEW EXPENSES
     *
     */

    public JButton getVeViewButton() {
        return VeViewButton;
    }

    public JButton getVeDeleteButton() {
        return VeDeleteButton;
    }

    public LocalDate getVeFromDate() {
        return VeFromDatePicker.getDate();
    }

    public LocalDate getVeToDate() {
        return VeToDatePicker.getDate();
    }

    public JRadioButton getVeDopRadioButton() {
        return VeDopRadioButton;
    }

    public JRadioButton getVeDoeRadioButton() {
        return VeDoeRadioButton;
    }

    public JCheckBox getVeGeCheckBox() {
        return VeGeCheckBox;
    }

    public JCheckBox getVeMeCheckBox() {
        return VeMeCheckBox;
    }

    public JTable getVeTable() {
        return VeTable;
    }

    /*
     *
     *      ADD ORDER
     *
     */

    public String getAoCustomer() {
        return AoCustomerField.getText();
    }

    public Double getAoPrice() {
        if (!AoPriceField.getText().equals("")) {
            try {
                return Double.parseDouble(AoPriceField.getText());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            return 0.0;
        }
        return -13.11;
    }

    public String getAoBatchSerial() {
        return AoBatchSerialField.getText();
    }

    public String getAoDetails() {
        return AoDetailsArea.getText();
    }

    public LocalDate getAoDop() {
        return AoDopPicker.getDate();
    }

    public LocalDate getAoDod() {
        return AoDodPicker.getDate();
    }

    public JButton getAoAddButton() {
        return AoAddButton;
    }

    public void clearAddOrderFields() {
        AoPriceField.setText("");
        AoBatchSerialField.setText("");
        AoCustomerField.setText("");
        AoDetailsArea.setText("");
    }

    public void getStatus(ResultSet status) throws SQLException {

        VoStatusComboBox.addItem("Select status");
        while (status.next()) {
            VoStatusComboBox.addItem(status.getString(1));
        }
    }

    /*
     *
     *      VIEW ORDERS
     *
     */

    public JTable getVoTable() {
        return VoTable;
    }

    public String getVoCustomer() {
        return VoCustomerField.getText();
    }

    public String getVoSerial() {
        return VoSerialField.getText();
    }

    public int getVoStatus() {
        return VoStatusComboBox.getSelectedIndex();
    }

    public String getVoDateType() {
        return VoDateComboBox.getSelectedItem().toString();
    }

    public LocalDate getVoFromDate() {
        return VoFromDatePicker.getDate();
    }

    public LocalDate getVoToDate() {
        return VoToDatePicker.getDate();
    }

    public JRadioButton getVoDateRadioButton() {
        return VoDateRadioButton;
    }

    public JRadioButton getVoCustomerRadioButton() {
        return VoCustomerRadioButton;
    }

    public JRadioButton getVoStatusRadioButton() {
        return VoStatusRadioButton;
    }

    public JRadioButton getVoSerialRadioButton() {
        return VoBatchRadioButton;
    }

    public JButton getVoViewButton() {
        return VoViewButton;
    }

    public JButton getDeleteOrderButton() {
        return deleteOrderButton;
    }

    /*
     *
     *      ADD MATERIAL
     *
     */

    public String getAmNameField() {
        return AmNameField.getText();
    }

    public String getAmUnitField() {
        return AmUnitField.getText();
    }

    public JButton getAmAddButton() {
        return AmAddButton;
    }

    public void clearAddMaterialFields() {
        AmNameField.setText("");
        AmUnitField.setText("");
    }

    /*
     *
     *      VIEW MATERIALS
     *
     */

    public JButton getVmRefreshButton() {
        return VmRefreshButton;
    }

    public JTable getVmTable() {
        return VmTable;
    }

    /*
     *
     *      CREATE BATCH
     *
     */

    public JButton getCbCreateButton() {
        return CbCreateButton;
    }

    public String getCbSerial() {
        return CbSerialField.getText();
    }

    public String getCbOrderIDs() {
        String ordersString = "";
        String[] orderIDs = CbOrderIDsArea.getText().split("\n");
        for (int i = 0; i < orderIDs.length; i++) {
            String orderID = orderIDs[i];
            ordersString += i != orderIDs.length - 1 ? ("\"" + orderID + "\",") : ("\"" + orderID + "\"}', ");
        }
        return ordersString;
    }

    public String getCbFormula() {
        return CbFormulaComboBox.getSelectedItem().toString();
    }

    /*
     *
     *      VIEW BATCHES
     *
     */

    public String getVbSerial() {
        return VbSerialField.getText();
    }

    public String getVbOrderId() {
        return VbOrderIdField.getText();
    }

    public String getVbFormula() {
        return VbFormulaField.getText();
    }

    public JRadioButton getVbSerialRadioButton() {
        return VbSerialRadioButton;
    }

    public JRadioButton getVbOrderRadioButton() {
        return VbOrderRadioButton;
    }

    public JRadioButton getVbFormulaRadioButton() {
        return VbFormulaRadioButton;
    }

    public JTable getVbTable() {
        return VbTable;
    }

    public JButton getVbViewButton() {
        return VbViewButton;
    }

    public JButton getDeleteBatchButton() {
        return deleteBatchButton;
    }

    /*
     *
     *      CREATE NEW FORMULA
     *
     */

    public void showCreateFormulaView(SystemController controller) {
        CfView = new CreateFormulaView(controller, this);
    }

    public JButton getCreateNewFormulaButton() {
        return createNewFormulaButton;
    }

    public CreateFormulaView getCfView() {
        return CfView;
    }

    /*
     *
     *      VIEW FORMULAS
     *
     */

    public JButton getVfRefreshButton() {
        return VfRefreshButton;
    }

    public JButton getDeleteFormulaButton() {
        return deleteFormulaButton;
    }

    public JTable getVfTable() {
        return VfTable;
    }

    /*
     *
     *      ADD VENDOR
     *
     */

    public JButton getAvAddButton() {
        return AvAddButton;
    }

    public String getAvVendorName() {
        return AvVendorNameField.getText();
    }

    public String getAvContactName() {
        return AvContactNameField.getText();
    }

    public String getAvContactNumber() {
        return AvContactNumberField.getText();
    }

    public String getAvContactEmail() {
        return AvContactEmailField.getText();
    }

    public void clearAddVendorFields() {
        AvVendorNameField.setText("");
        AvProductsArea.setText("");
        AvContactNameField.setText("");
        AvContactNumberField.setText("");
        AvContactEmailField.setText("");
    }

    /*
     *
     *      VIEW VENDORS
     *
     */

    public JButton getVvViewButton() {
        return VvViewButton;
    }

    public JButton getDeleteVendorButton() {
        return deleteVendorButton;
    }

    public JRadioButton getVvVendorRadioButton() {
        return VvVendorRadioButton;
    }

    public JRadioButton getVvContactRadioButton() {
        return VvContactRadioButton;
    }

    public String getVvVendor() {
        return VvVendorField.getText();
    }

    public String getVvContact() {
        return VvContactField.getText();
    }

    public JTable getVvTable() {
        return VvTable;
    }

    /*
    --
    --
    --
     */

    public void showExpenses(ArrayList<ResultSet> results, SystemController controller) throws SQLException {

        VeTotalExpenses = 0;
        int columnCount = results.get(0).getMetaData().getColumnCount();
        int rowCount = 0;
        int rowCounter = 0;

        for (ResultSet expensesSet: results) {
            rowCount += getRowCount(expensesSet);
        }

        String[] columnNames = getColumnNames(results.get(0), columnCount);
        String [][] data = new String[rowCount][columnCount];

        for (ResultSet expensesSet: results) {
            while (expensesSet.next()) {
                for (int j = 0 ; j < columnCount ; j++) {
//                    if (j == 2) {
//                        VeTotalExpenses += expensesSet.getDouble(j+1);
//                    }
                    if (expensesSet.getMetaData().getColumnName(j+1).equals("Material_ID") && j == 1) {
                        data[expensesSet.getRow()+rowCounter-1][j] = (String) MeComboBox.getItemAt(materialIDs.indexOf(expensesSet.getString(j + 1)));
                    } else {
                        data[expensesSet.getRow()+rowCounter-1][j] = expensesSet.getString(j+1);
                    }
                }
            }
            expensesSet.last();
            rowCounter += expensesSet.getRow();
        }

        VeTotalExpensesLabel.setText("Total: " + round(VeTotalExpenses, 2) + " EGP");

        VeTable = new JTable(data, columnNames);
        setTableFont(VeTable);
        VeTablePanel.remove(0);
        VeTablePanel.add(new JScrollPane(VeTable));

        VeTable.getModel().addTableModelListener(controller);

        VeTablePanel.repaint();
        viewExpensesPanel.repaint();
        expensesPanel.repaint();
    }

    public void showOrders(ResultSet orders, SystemController controller) throws SQLException {
        int columnCount = orders.getMetaData().getColumnCount();
        int rowCount = getRowCount(orders);

        String[] columnNames = getColumnNames(orders, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (orders.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                if (orders.getMetaData().getColumnName(i+1).equals("Status")) {

                    data[orders.getRow() - 1][i] = VoStatusComboBox.getItemAt(orders.getInt(i+1)).toString();
                } else {
                    data[orders.getRow() - 1][i] = orders.getString(i + 1);
                }
            }
        }
        VoTable = new JTable(data, columnNames);
        setTableFont(VoTable);
        VoTablePanel.remove(0);
        VoTablePanel.add(new JScrollPane(VoTable));
        VoTable.getModel().addTableModelListener(controller);
        VoTable.addPropertyChangeListener(controller);

        VoTablePanel.repaint();
        VoPanel.repaint();
        ordersPanel.repaint();
        homePanel.repaint();
    }

    public void getMaterials(ResultSet materials, SystemController controller) throws SQLException {

        int columnCount = materials.getMetaData().getColumnCount();
        int rowCount = getRowCount(materials);

        String[] columnNames = getColumnNames(materials, columnCount);
        String [][] data = new String[rowCount][columnCount];

        MeComboBox.removeAllItems();
        MeComboBox.addItem("Select Material");

        while (materials.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[materials.getRow()-1][i] = materials.getString(i+1);
            }
            MeComboBox.addItem(materials.getString(2));
            materialUnits.add(materials.getString(5));
            materialIDs.add(materials.getString(1));
        }

        VmTable = new JTable(data, columnNames);
        setTableFont(VmTable);
        VmTablePanel.remove(0);
        VmTablePanel.add(new JScrollPane(VmTable));
        VmTable.getModel().addTableModelListener(controller);
        VmTable.addPropertyChangeListener(controller);

        VmTablePanel.repaint();
        materialsPanel.repaint();
    }

    public void showBatches(ResultSet batches, SystemController controller) throws SQLException {
        int columnCount = batches.getMetaData().getColumnCount();
        int rowCount = getRowCount(batches);

        String[] columnNames = getColumnNames(batches, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (batches.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[batches.getRow()-1][i] = batches.getString(i+1);
            }
        }
        VbTable = new JTable(data, columnNames);
        setTableFont(VbTable);
        VbTablePanel.remove(0);
        VbTablePanel.add(new JScrollPane(VbTable));

        VbTable.getModel().addTableModelListener(controller);

        VbTablePanel.repaint();
        VbPanel.repaint();
        batchesPanel.repaint();
        homePanel.repaint();
    }

    public void showFormulas(ResultSet formulas, SystemController controller) throws SQLException {
        int columnCount = formulas.getMetaData().getColumnCount();
        int rowCount = getRowCount(formulas);

        String[] columnNames = getColumnNames(formulas, columnCount);
        String [][] data = new String[rowCount][columnCount];

        CbFormulaComboBox.removeAllItems();
        CbFormulaComboBox.addItem("Select Formula");
        AtFormulaComboBox.addItem("Select Formula");

        while (formulas.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[formulas.getRow() - 1][i] = formulas.getString(i + 1);
            }
            CbFormulaComboBox.addItem(formulas.getString(1));
            AtFormulaComboBox.addItem(formulas.getString(1));
        }
        VfTable = new JTable(data, columnNames);
        VfTable.setAutoCreateRowSorter(true);
        setTableFont(VfTable);
        VfTablePanel.remove(0);
        VfTablePanel.add(new JScrollPane(VfTable));
        VfTable.getModel().addTableModelListener(controller);
        VfTable.addPropertyChangeListener(controller);

        VfTablePanel.repaint();
        formulasPanel.repaint();
        homePanel.repaint();
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
        VvTable = new JTable(data, columnNames);
        VvTable.setAutoCreateRowSorter(false);
        setTableFont(VvTable);
        VvTablePanel.remove(0);
        VvTablePanel.add(new JScrollPane(VvTable));
        VvTable.getModel().addTableModelListener(controller);
        VvTable.addPropertyChangeListener(controller);

        VvTablePanel.repaint();
        vendorsPanel.repaint();
        homePanel.repaint();
    }

    private String[] getColumnNames(ResultSet set, int columnCount) {

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

    int getRowCount(ResultSet set) {

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

    static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();

        BigDecimal bd = BigDecimal.valueOf(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    void setTableFont(JTable table) {
        Font font = new Font(table.getFont().getName(), table.getFont().getStyle(), 15);
        table.setFont(font);
        table.setRowHeight(25);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == MeComboBox) {
            if (MeComboBox.getItemCount() > 0) {
                MeQuantityLabel.setText("Quantity/" + materialUnits.get(MeComboBox.getSelectedIndex()));
            }
        }

        else if (e.getSource() == VeDoeRadioButton || e.getSource() == VeDopRadioButton) {
            VeFromDatePicker.setEnabled(true);
            VeToDatePicker.setEnabled(true);
        } else if (e.getSource() == VeClearFilterButton) {
            VeButtonGroup.clearSelection();
            VeFromDatePicker.clear();
            VeToDatePicker.clear();
            VeFromDatePicker.setEnabled(false);
            VeToDatePicker.setEnabled(false);
        }

        else if (e.getSource() == VoDateRadioButton) {
            VoFromDatePicker.setEnabled(!VoFromDatePicker.isEnabled());
            VoToDatePicker.setEnabled(!VoToDatePicker.isEnabled());
            VoDateComboBox.setEnabled(!VoDateComboBox.isEnabled());
            VoStatusComboBox.setEnabled(false);
            VoSerialField.setEnabled(false);
            VoCustomerField.setEnabled(false);
        } else if (e.getSource() == VoStatusRadioButton) {
            VoStatusComboBox.setEnabled(!VoStatusComboBox.isEnabled());
            VoToDatePicker.setEnabled(false);
            VoFromDatePicker.setEnabled(false);
            VoDateComboBox.setEnabled(false);
            VoSerialField.setEnabled(false);
            VoCustomerField.setEnabled(false);
        } else if (e.getSource() == VoBatchRadioButton) {
            VoSerialField.setEnabled(!VoSerialField.isEnabled());
            VoFromDatePicker.setEnabled(false);
            VoToDatePicker.setEnabled(false);
            VoDateComboBox.setEnabled(false);
            VoStatusComboBox.setEnabled(false);
            VoCustomerField.setEnabled(false);
        } else if (e.getSource() == VoCustomerRadioButton) {
            VoCustomerField.setEnabled(!VoCustomerField.isEnabled());
            VoFromDatePicker.setEnabled(false);
            VoToDatePicker.setEnabled(false);
            VoDateComboBox.setEnabled(false);
            VoStatusComboBox.setEnabled(false);
            VoSerialField.setEnabled(false);
        } else if (e.getSource() == clearOrdersSelectionButton) {
            VoRadioButtonGroup.clearSelection();
            VoFromDatePicker.setEnabled(false);
            VoToDatePicker.setEnabled(false);
            VoDateComboBox.setEnabled(false);
            VoStatusComboBox.setEnabled(false);
            VoSerialField.setEnabled(false);
            VoCustomerField.setEnabled(false);
        }

        else if (e.getSource() == VbSerialRadioButton) {
            VbSerialField.setEnabled(!VbSerialField.isEnabled());
            VbOrderIdField.setEnabled(false);
            VbFormulaField.setEnabled(false);
        } else if (e.getSource() == VbOrderRadioButton) {
            VbOrderIdField.setEnabled(!VbOrderIdField.isEnabled());
            VbSerialField.setEnabled(false);
            VbFormulaField.setEnabled(false);
        } else if (e.getSource() == VbFormulaRadioButton) {
            VbFormulaField.setEnabled(!VbFormulaField.isEnabled());
            VbSerialField.setEnabled(false);
            VbOrderIdField.setEnabled(false);
        } else if (e.getSource() == clearBatchesSelectionButton) {
            VbRadioButtonGroup.clearSelection();
            VbSerialField.setEnabled(false);
            VbOrderIdField.setEnabled(false);
            VbFormulaField.setEnabled(false);
        }

        else if (e.getSource() == VvVendorRadioButton) {
            VvVendorField.setEnabled(!VvVendorField.isEnabled());
            VvContactField.setEnabled(false);
        } else if (e.getSource() == VvContactRadioButton) {
            VvContactField.setEnabled(!VvContactField.isEnabled());
            VvVendorField.setEnabled(false);
        } else if (e.getSource() == clearVendorsSelectionButton) {
            VvRadioButtonGroup.clearSelection();
            VvVendorField.setEnabled(false);
            VvContactField.setEnabled(false);
        }

        VoPanel.repaint();
        ordersPanel.repaint();
    }
}
