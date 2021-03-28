package View.MainPanels;

import Controller.SystemController;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class RawMaterialsPanel extends JPanel implements MainPanel {

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
}