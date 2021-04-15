package View.MainPanels;

import Controller.SystemController;
import View.HelperPanels.ShowTextAreaView;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RawMaterialsPanel extends JPanel implements MainPanel, PropertyChangeListener, ActionListener {

    private HomeView homeView;
    private JPanel rawMaterialsPanel;

    private ArrayList<String> materialUnits = new ArrayList<>();

    //  ADD MATERIAL

    private JTextField addNameField;
    private JTextField addUnitField;
    private JButton addMaterialButton;

    //  VIEW MATERIALS

    private JTable materialsTable;
    private JPanel tablePanel;

    private JButton refreshMaterialsButton;
    private JButton deleteMaterialButton;

    private ShowTextAreaView showTextAreaView;

    public RawMaterialsPanel(HomeView homeView) {
        this.homeView = homeView;
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(rawMaterialsPanel, BorderLayout.CENTER);

        materialsTable = new JTable();
        tablePanel.add(new JScrollPane(materialsTable));

        materialUnits.add("");
    }

    public void getMaterials(ResultSet materials, SystemController controller) throws SQLException {
        int columnCount = materials.getMetaData().getColumnCount();
        int rowCount = getRowCount(materials);

        String[] columnNames = getColumnNames(materials, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (materials.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[materials.getRow()-1][i] = materials.getString(i+1);
            }
            materialUnits.add(materials.getString(4));
        }

        materialsTable = new JTable(data, columnNames);
        setTableFont(materialsTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(materialsTable));

        materialsTable.getModel().addTableModelListener(controller);
        materialsTable.addPropertyChangeListener(this);
        this.validate();
        homeView.getMaterialPurchasesPanel().getMaterials();
    }

    public void clearAddFields() {
        addNameField.setText("");
        addUnitField.setText("");
    }


    //  ADD MATERIAL GETTERS


    public String getAddName() {
        return addNameField.getText();
    }

    public String getAddUnit() {
        return addUnitField.getText();
    }

    public JButton getAddMaterialButton() {
        return addMaterialButton;
    }


    //  VIEW MATERIALS GETTERS


    public JTable getMaterialsTable() {
        return materialsTable;
    }

    public JButton getRefreshMaterialsButton() {
        return refreshMaterialsButton;
    }

    public JButton getDeleteMaterialButton() {
        return deleteMaterialButton;
    }

    public ArrayList<String> getMaterialUnits() {
        return materialUnits;
    }


    //  OTHER METHODS

    void showBookedQuantities() {
        int row = materialsTable.getSelectedRow();
        int column = materialsTable.getSelectedColumn();

        String bookedQuantities = materialsTable.getValueAt(row, column).toString();
        showTextAreaView = new ShowTextAreaView(bookedQuantities, row);
        showTextAreaView.getUpdateButton().addActionListener(this);
    }

    void updateBookedQuantities() {
        showTextAreaView.dispatchEvent(new WindowEvent(showTextAreaView, WindowEvent.WINDOW_CLOSING));
        String[] lines = showTextAreaView.getTextArea().split("\n");
        String updated = "";
        for (int i = 0 ; i < lines.length ; i++) {
            if (i == lines.length - 1) {
                updated += lines[i];
            } else {
                updated += lines[i] + ",";
            }
        }
        materialsTable.setValueAt(updated, showTextAreaView.getRow(), 2);
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
        addMaterialButton.addActionListener(controller);
        refreshMaterialsButton.addActionListener(controller);
        deleteMaterialButton.addActionListener(controller);
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == materialsTable) {
            if (evt.getPropertyName().equals("tableCellEditor")) {
                if (materialsTable.getColumnName(materialsTable.getSelectedColumn()).equals("Booked_quantity")) {
                    materialsTable.getCellEditor().stopCellEditing();
                    materialsTable.getCellEditor().cancelCellEditing();
                    materialsTable.setFocusable(false);
                    showBookedQuantities();
                }
            }
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getActionCommand().equals("Update")) {
            updateBookedQuantities();
        }
    }
}