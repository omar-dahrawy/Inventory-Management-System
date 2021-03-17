package View;

import Controller.SystemController;
import Model.Constants;
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

    private Constants K = new Constants();

    private JPanel homePanel;

    /*
     *
     *      TABBED PANES
     *
     */

    private JTabbedPane mainTabbedPane;
    private JTabbedPane expensesTabbedPane;
    private JTabbedPane materialsTabbedPane;

    /*
     *
     *      MAIN PANELS
     *
     */

    private JPanel expensesPanel;
    private JPanel materialsPanel;
    private JPanel ordersPanel;
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

    private JComboBox MeMaterialComboBox;
    private ArrayList<String> materialUnits = new ArrayList<>();
    private ArrayList<String> materialIDs = new ArrayList<>();
    private JLabel MeQuantityLabel;
    private JTextField MeQuantityField;
    private JComboBox MeVendorComboBox;
    private JTextField MeInvoiceField;
    private JPanel MeDatePickerPanel;
    private DatePicker MeDatePicker = new DatePicker();
    private JButton MeAddButton;

    /*
     *
     *      VIEW GENERAL EXPENSES
     *
     */

    private JPanel VePanel;
    private JPanel VgePanel;
    private JTable VgeTable;
    private JPanel VgeTablePanel;
    private ButtonGroup VgeButtonGroup = new ButtonGroup();
    private JRadioButton VgeDopRadioButton;
    private JRadioButton VgeDoeRadioButton;
    private JPanel VgeFromDatePanel;
    private JPanel VgeToDatePanel;
    private DatePicker VgeFromDatePicker = new DatePicker();
    private DatePicker VgeToDatePicker = new DatePicker();
    private JButton VgeViewButton;
    private JButton VgeClearFilterButton;
    private JButton VgeDeleteButton;

    /*
     *
     *      VIEW MATERIAL EXPENSES
     *
     */

    private JPanel VmePanel;
    private JTable VmeTable;
    private JPanel VmeTablePanel;
    private ButtonGroup VmeButtonGroup = new ButtonGroup();
    private JRadioButton VmeDopRadioButton;
    private JRadioButton VmeDoeRadioButton;
    private JPanel VmeFromDatePanel;
    private JPanel VmeToDatePanel;
    private DatePicker VmeFromDatePicker = new DatePicker();
    private DatePicker VmeToDatePicker = new DatePicker();
    private JButton VmeViewButton;
    private JButton VmeClearFilterButton;
    private JButton VmeDeleteButton;

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

    private JPanel viewMaterialsPanel;
    private JPanel AmPanel;
    private JTextField AmNameField;
    private JTextField AmUnitField;
    private JButton AmAddButton;
    private JTable VmTable;
    private JPanel VmPanel;
    private JButton VmRefreshButton;

    /*
     *
     *      ADD PRODUCTION
     *
     */

    private JButton addNewProductionButton;
    private AddProductionView ApView;

    /*
     *
     *      VIEW PRODUCTIONS
     *
     */

    private JPanel VpPanel;
    private JTable VpTable;
    private JPanel VpTablePanel;
    private ButtonGroup VpRadioButtonGroup = new ButtonGroup();
    private JRadioButton VpSerialRadioButton;
    private JRadioButton VpOrderRadioButton;
    private JRadioButton VpFormulaRadioButton;
    private JRadioButton VpStatusRadioButton;
    private JTextField VpSerialField;
    private JTextField VpOrderIdField;
    private JComboBox VpFormulasComboBox;
    private JComboBox VpStatusComboBox;
    private JButton VpViewButton;
    private JButton deleteProductionButton;
    private JButton clearProductionSelectionButton;

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

    private JPanel VvTablePanel;
    private JButton VvViewButton;
    private ArrayList<String> vendorIDs = new ArrayList<>();
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

    private JComboBox AtFormulasComboBox;
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
    private JPanel VvPanel;
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
        vendorIDs.add("");
        MeMaterialComboBox.addItem("Select Material");

        VgeButtonGroup.add(VgeDopRadioButton);
        VgeButtonGroup.add(VgeDoeRadioButton);

        VgeFromDatePicker.setEnabled(false);
        VgeToDatePicker.setEnabled(false);

        VgeFromDatePanel.add(VgeFromDatePicker);
        VgeToDatePanel.add(VgeToDatePicker);

        VmeButtonGroup.add(VmeDopRadioButton);
        VmeButtonGroup.add(VmeDoeRadioButton);

        VmeFromDatePicker.setEnabled(false);
        VmeToDatePicker.setEnabled(false);

        VmeFromDatePanel.add(VmeFromDatePicker);
        VmeToDatePanel.add(VmeToDatePicker);

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

        VpRadioButtonGroup.add(VpSerialRadioButton);
        VpRadioButtonGroup.add(VpOrderRadioButton);
        VpRadioButtonGroup.add(VpFormulaRadioButton);
        VpRadioButtonGroup.add(VpStatusRadioButton);

        VpStatusComboBox.addItem("Select Status");
        VpStatusComboBox.addItem(K.status_1);
        VpStatusComboBox.addItem(K.status_2);
        VpStatusComboBox.addItem(K.status_3);
        VpStatusComboBox.addItem(K.status_4);

        VvRadioButtonGroup.add(VvVendorRadioButton);
        VvRadioButtonGroup.add(VvContactRadioButton);

        VgeTablePanel.add(new JScrollPane(VgeTable));
        VmeTablePanel.add(new JScrollPane(VmeTable));
        VoTablePanel.add(new JScrollPane(VoTable));
        VmPanel.add(new JScrollPane(VmTable));
        VpTablePanel.add(new JScrollPane(VpTable));
        VfTablePanel.add(new JScrollPane(VfTable));
        VvTablePanel.add(new JScrollPane(VvTable));
        VtTablePanel.add(new JScrollPane(VtTable));
        VsTablePanel.add(new JScrollPane(VsTable));
    }

    public void addActionListeners(SystemController controller) {
        MeAddButton.addActionListener(controller);
        GeAddButton.addActionListener(controller);
        MeMaterialComboBox.addActionListener(this);

        VgeDopRadioButton.addActionListener(this);
        VgeDoeRadioButton.addActionListener(this);
        VgeViewButton.addActionListener(controller);
        VgeDeleteButton.addActionListener(controller);
        VgeClearFilterButton.addActionListener(this);

        VmeDopRadioButton.addActionListener(this);
        VmeDoeRadioButton.addActionListener(this);
        VmeViewButton.addActionListener(controller);
        VmeDeleteButton.addActionListener(controller);
        VmeClearFilterButton.addActionListener(this);

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

        addNewProductionButton.addActionListener(controller);
        VpSerialRadioButton.addActionListener(this);
        VpOrderRadioButton.addActionListener(this);
        VpFormulaRadioButton.addActionListener(this);
        VpStatusRadioButton.addActionListener(this);
        VpViewButton.addActionListener(controller);
        clearProductionSelectionButton.addActionListener(this);
        deleteProductionButton.addActionListener(controller);

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
        return materialIDs.get(MeMaterialComboBox.getSelectedIndex());
    }

    public String getVendorID() {
        return vendorIDs.get(MeVendorComboBox.getSelectedIndex());
    }

    public ArrayList<String> getMaterialIDs() {
        return materialIDs;
    }

    public JComboBox getMeMaterialComboBox() {
        return MeMaterialComboBox;
    }

    public JComboBox getMeVendorComboBox() {
        return MeVendorComboBox;
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

    public String getMeInvoice() {
        return MeInvoiceField.getText();
    }

    public LocalDate getMeDate() {
        return MeDatePicker.getDate();
    }

    public String getMaterialUnit() {
        return materialUnits.get(MeMaterialComboBox.getSelectedIndex());
    }

    public JButton getMeAddButton() {
        return MeAddButton;
    }

    public void clearMaterialExpensesFields() {
        MeMaterialComboBox.setSelectedIndex(0);
        MeQuantityField.setText("");
    }

    /*
     *
     *      VIEW GENERAL EXPENSES
     *
     */

    public JButton getVgeViewButton() {
        return VgeViewButton;
    }

    public JButton getVgeDeleteButton() {
        return VgeDeleteButton;
    }

    public LocalDate getVgeFromDate() {
        return VgeFromDatePicker.getDate();
    }

    public LocalDate getVgeToDate() {
        return VgeToDatePicker.getDate();
    }

    public JRadioButton getVgeDopRadioButton() {
        return VgeDopRadioButton;
    }

    public JRadioButton getVgeDoeRadioButton() {
        return VgeDoeRadioButton;
    }

    public JTable getVgeTable() {
        return VgeTable;
    }

    /*
     *
     *      VIEW MATERIAL EXPENSES
     *
     */

    public JButton getVmeViewButton() {
        return VmeViewButton;
    }

    public JButton getVmeDeleteButton() {
        return VmeDeleteButton;
    }

    public LocalDate getVmeFromDate() {
        return VmeFromDatePicker.getDate();
    }

    public LocalDate getVmeToDate() {
        return VmeToDatePicker.getDate();
    }

    public JRadioButton getVmeDopRadioButton() {
        return VmeDopRadioButton;
    }

    public JRadioButton getVmeDoeRadioButton() {
        return VmeDoeRadioButton;
    }

    public JTable getVmeTable() {
        return VmeTable;
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
     *      ADD PRODUCTION
     *
     */

    public void showAddProductionView(SystemController controller) {
        ApView = new AddProductionView(controller, this);
    }

    public JButton getAddNewProductionButton() {
        return addNewProductionButton;
    }

    public AddProductionView getApView() {
        return ApView;
    }

    /*
     *
     *      VIEW PRODUCTIONS
     *
     */

    public String getVpSerial() {
        return VpSerialField.getText();
    }

    public String getVpOrderId() {
        return VpOrderIdField.getText();
    }

    public String getVpFormula() {
        return VpFormulasComboBox.getSelectedItem().toString();
    }

    public String getVpStatus() {
        return VpStatusComboBox.getSelectedItem().toString();
    }

    public JRadioButton getVpSerialRadioButton() {
        return VpSerialRadioButton;
    }

    public JRadioButton getVpOrderRadioButton() {
        return VpOrderRadioButton;
    }

    public JRadioButton getVpFormulaRadioButton() {
        return VpFormulaRadioButton;
    }

    public JRadioButton getVpStatusRadioButton() {
        return VpStatusRadioButton;
    }

    public JTable getVpTable() {
        return VpTable;
    }

    public JButton getVpViewButton() {
        return VpViewButton;
    }

    public JButton getDeleteProductionButton() {
        return deleteProductionButton;
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

    public void showGeneralExpenses(ResultSet results, SystemController controller) throws SQLException {
        int columnCount = results.getMetaData().getColumnCount();
        int rowCount = getRowCount(results);

        String[] columnNames = getColumnNames(results, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (results.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[results.getRow()-1][i] = results.getString(i+1);
            }
        }
        VgeTable = new JTable(data, columnNames);
        setTableFont(VgeTable);
        VgeTablePanel.remove(0);
        VgeTablePanel.add(new JScrollPane(VgeTable));

        VgeTable.getModel().addTableModelListener(controller);

        VgeTablePanel.repaint();
        VgePanel.repaint();
        expensesPanel.repaint();
    }

    public void showMaterialExpenses(ResultSet results, SystemController controller) throws SQLException {
        int columnCount = results.getMetaData().getColumnCount();
        int rowCount = getRowCount(results);

        String[] columnNames = getColumnNames(results, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (results.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                if (i == 1) {
                    data[results.getRow() - 1][i] = (String) MeMaterialComboBox.getItemAt(materialIDs.indexOf(results.getString(i + 1)));
                } else {
                    data[results.getRow() - 1][i] = results.getString(i + 1);
                }
            }
        }
        VmeTable = new JTable(data, columnNames);
        setTableFont(VmeTable);
        VmeTablePanel.remove(0);
        VmeTablePanel.add(new JScrollPane(VmeTable));

        VmeTable.getModel().addTableModelListener(controller);

        VmeTablePanel.repaint();
        VmePanel.repaint();
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

        MeMaterialComboBox.removeAllItems();
        MeMaterialComboBox.addItem("Select Material");

        while (materials.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[materials.getRow()-1][i] = materials.getString(i+1);
            }
            MeMaterialComboBox.addItem(materials.getString(2));
            materialUnits.add(materials.getString(5));
            materialIDs.add(materials.getString(1));
        }

        VmTable = new JTable(data, columnNames);
        setTableFont(VmTable);
        VmPanel.remove(0);
        VmPanel.add(new JScrollPane(VmTable));
        VmTable.getModel().addTableModelListener(controller);
        VmTable.addPropertyChangeListener(controller);

        VmPanel.repaint();
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
        VpTable = new JTable(data, columnNames);
        setTableFont(VpTable);
        VpTablePanel.remove(0);
        VpTablePanel.add(new JScrollPane(VpTable));

        VpTable.getModel().addTableModelListener(controller);

        VpTablePanel.repaint();
        VpPanel.repaint();
        batchesPanel.repaint();
        homePanel.repaint();
    }

    public void showFormulas(ResultSet formulas, SystemController controller) throws SQLException {
        int columnCount = formulas.getMetaData().getColumnCount();
        int rowCount = getRowCount(formulas);

        String[] columnNames = getColumnNames(formulas, columnCount);
        String [][] data = new String[rowCount][columnCount];

        VpFormulasComboBox.removeAllItems();
        AtFormulasComboBox.removeAllItems();
        VpFormulasComboBox.addItem("Select Formula");
        AtFormulasComboBox.addItem("Select Formula");

        while (formulas.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[formulas.getRow() - 1][i] = formulas.getString(i + 1);
            }
            VpFormulasComboBox.addItem(formulas.getString(1));
            AtFormulasComboBox.addItem(formulas.getString(1));
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

        MeVendorComboBox.removeAllItems();
        MeVendorComboBox.addItem("Select Vendor");

        while (vendors.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[vendors.getRow() - 1][i] = vendors.getString(i + 1);
            }
            MeVendorComboBox.addItem(vendors.getString(2));
            vendorIDs.add(vendors.getString(1));
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
        if (e.getSource() == MeMaterialComboBox) {
            if (MeMaterialComboBox.getItemCount() > 0) {
                MeQuantityLabel.setText("Quantity/" + materialUnits.get(MeMaterialComboBox.getSelectedIndex()));
            }
        }

        else if (e.getSource() == VgeDoeRadioButton || e.getSource() == VgeDopRadioButton) {
            VgeFromDatePicker.setEnabled(true);
            VgeToDatePicker.setEnabled(true);
        } else if (e.getSource() == VgeClearFilterButton) {
            VgeButtonGroup.clearSelection();
            VgeFromDatePicker.clear();
            VgeToDatePicker.clear();
            VgeFromDatePicker.setEnabled(false);
            VgeToDatePicker.setEnabled(false);
        } else if (e.getSource() == VmeDoeRadioButton || e.getSource() == VmeDopRadioButton) {
            VmeFromDatePicker.setEnabled(true);
            VmeToDatePicker.setEnabled(true);
        } else if (e.getSource() == VmeClearFilterButton) {
            VmeButtonGroup.clearSelection();
            VmeFromDatePicker.clear();
            VmeToDatePicker.clear();
            VmeFromDatePicker.setEnabled(false);
            VmeToDatePicker.setEnabled(false);
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

        else if (e.getSource() == VpSerialRadioButton) {
            VpSerialField.setEnabled(!VpSerialField.isEnabled());
            VpOrderIdField.setEnabled(false);
            VpFormulasComboBox.setEnabled(false);
            VpStatusComboBox.setEnabled(false);
        } else if (e.getSource() == VpOrderRadioButton) {
            VpOrderIdField.setEnabled(!VpOrderIdField.isEnabled());
            VpSerialField.setEnabled(false);
            VpFormulasComboBox.setEnabled(false);
            VpStatusComboBox.setEnabled(false);
        } else if (e.getSource() == VpFormulaRadioButton) {
            VpFormulasComboBox.setEnabled(!VpFormulasComboBox.isEnabled());
            VpSerialField.setEnabled(false);
            VpOrderIdField.setEnabled(false);
            VpStatusComboBox.setEnabled(false);
        } else if (e.getSource() == VpStatusRadioButton) {
            VpStatusComboBox.setEnabled(!VpStatusComboBox.isEnabled());
            VpSerialField.setEnabled(false);
            VpOrderIdField.setEnabled(false);
            VpFormulasComboBox.setEnabled(false);
        } else if (e.getSource() == clearProductionSelectionButton) {
            VpRadioButtonGroup.clearSelection();
            VpSerialField.setEnabled(false);
            VpOrderIdField.setEnabled(false);
            VpFormulasComboBox.setEnabled(false);
            VpStatusComboBox.setEnabled(false);
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
