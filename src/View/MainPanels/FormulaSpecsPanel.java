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

public class FormulaSpecsPanel extends JPanel implements MainPanel, ActionListener {

    private final HomeView homeView;
    private JPanel formulaSpecsPanel;
    private final Constants K;

    //  VIEW STORAGE

    private JTable specsTable;
    private JPanel tablePanel;

    private JComboBox<String> filterFormulasComboBox;

    private JRadioButton filterFormulasButton;

    private JButton viewSpecsButton;
    private JButton clearFiltersButton;


    public FormulaSpecsPanel(HomeView homeView) {
        this.homeView = homeView;
        K = new Constants();
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(formulaSpecsPanel, BorderLayout.CENTER);

        specsTable = new JTable();
        tablePanel.add(new JScrollPane(specsTable));
    }

    public void getFormulasSpecs(ResultSet items, SystemController controller) throws SQLException {
        int columnCount = items.getMetaData().getColumnCount();
        int rowCount = getRowCount(items);

        String[] columnNames = getColumnNames(items, columnCount);
        String [][] data = new String[rowCount][columnCount];

        while (items.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[items.getRow() - 1][i] = items.getString(i + 1);
            }
        }
        specsTable = new JTable(data, columnNames);
        setTableFont(specsTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(specsTable));
        specsTable.getModel().addTableModelListener(controller);

        this.validate();
    }


    //  VIEW STORAGE GETTERS


    public JTable getSpecsTable() {
        return specsTable;
    }

    public String getFilterFormula() {
        return filterFormulasComboBox.getSelectedItem().toString();
    }

    public boolean getFilterFormulasSelected() {
        return filterFormulasButton.isSelected();
    }

    public JButton getViewSpecsButton() {
        return viewSpecsButton;
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
        filterFormulasButton.addActionListener(this);
        viewSpecsButton.addActionListener(controller);
        clearFiltersButton.addActionListener(this);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == clearFiltersButton) {
            filterFormulasComboBox.setEnabled(false);
            filterFormulasButton.setSelected(false);
        } else {
            filterFormulasComboBox.setEnabled(true);
        }
    }
}
