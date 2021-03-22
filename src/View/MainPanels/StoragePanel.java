package View.MainPanels;

import Controller.SystemController;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class StoragePanel extends JPanel implements MainPanel{

    private JPanel storagePanel;

    private JTable storageTable;
    private JPanel tablePanel;

    public StoragePanel() {
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(storagePanel, BorderLayout.CENTER);

        storageTable = new JTable();
        tablePanel.add(new JScrollPane(storageTable));
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
        storageTable.addPropertyChangeListener(controller);

        this.validate();
    }

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

    }

}
