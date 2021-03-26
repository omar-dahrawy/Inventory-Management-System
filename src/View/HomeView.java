package View;

import Controller.SystemController;
import Model.Constants;
import View.MainPanels.*;
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

    /*
     *
     *      MAIN PANELS
     *
     */

    private JPanel expensesPanel;
    private OrdersPanel ordersPanel;
    private StoragePanel storagePanel;
    private VendorsPanel vendorsPanel;
    private FormulasPanel formulasPanel;
    private ProductionPanel productionPanel;
    private RawMaterialsPanel rawMaterialsPanel;

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


        ordersPanel = new OrdersPanel(this);
        storagePanel = new StoragePanel(this);
        vendorsPanel = new VendorsPanel(this);
        formulasPanel = new FormulasPanel(this);
        productionPanel = new ProductionPanel(this);
        rawMaterialsPanel = new RawMaterialsPanel(this);

        JTabbedPane materialsTabbedPane = new JTabbedPane();
        materialsTabbedPane.add("Raw Materials", rawMaterialsPanel);
        materialsTabbedPane.add("Vendors", vendorsPanel);

        mainTabbedPane.add("Materials", materialsTabbedPane);
        mainTabbedPane.add("Orders", ordersPanel);
        mainTabbedPane.add("Production", productionPanel);
        mainTabbedPane.add("Formulas", formulasPanel);
        mainTabbedPane.add("Storage", storagePanel);

        GeDatePicker.setDateToToday();
        MeDatePicker.setDateToToday();
        GeDatePickerPanel.add(GeDatePicker);
        MeDatePickerPanel.add(MeDatePicker);

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

        VgeTablePanel.add(new JScrollPane(VgeTable));
        VmeTablePanel.add(new JScrollPane(VmeTable));
        VsTablePanel.add(new JScrollPane(VsTable));
    }

    public void addActionListeners(SystemController controller) {
        ordersPanel.addActionListeners(controller);
        storagePanel.addActionListeners(controller);
        vendorsPanel.addActionListeners(controller);
        formulasPanel.addActionListeners(controller);
        productionPanel.addActionListeners(controller);
        rawMaterialsPanel.addActionListeners(controller);

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
        return rawMaterialsPanel.getMaterialIDs().get(MeMaterialComboBox.getSelectedIndex());
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
        return rawMaterialsPanel.getMaterialUnits().get(MeMaterialComboBox.getSelectedIndex());
    }

    public JButton getMeAddButton() {
        return MeAddButton;
    }

    public void clearMaterialExpensesFields() {
        MeMaterialComboBox.setSelectedIndex(0);
        MeQuantityField.setText("");
    }

    public String getVendorID() {
        return vendorsPanel.getVendorIDs().get(MeVendorComboBox.getSelectedIndex());
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
     *      VIEW STORAGE
     *
     */

    public OrdersPanel getOrdersPanel() {
        return ordersPanel;
    }

    public StoragePanel getStoragePanel() {
        return storagePanel;
    }

    public VendorsPanel getVendorsPanel() {
        return vendorsPanel;
    }

    public FormulasPanel getFormulasPanel() {
        return formulasPanel;
    }

    public ProductionPanel getProductionPanel() {
        return productionPanel;
    }

    public RawMaterialsPanel getRawMaterialsPanel() {
        return rawMaterialsPanel;
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
                    data[results.getRow() - 1][i] = (String) MeMaterialComboBox.getItemAt(rawMaterialsPanel.getMaterialIDs().indexOf(results.getString(i + 1)));
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
                MeQuantityLabel.setText("Quantity/" + rawMaterialsPanel.getMaterialUnits().get(MeMaterialComboBox.getSelectedIndex()));
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
    }
}