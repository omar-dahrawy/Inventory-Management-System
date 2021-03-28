package View.MainPanels;

import Controller.SystemController;
import View.CreateFormulaView;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FormulasPanel extends JPanel implements MainPanel {

    private JPanel formulasPanel;
    private HomeView homeView;

    //  ADD FORMULA

    private JButton createNewFormulaButton;
    private CreateFormulaView createFormulaView;

    //  VIEW FORMULAS

    private JTable formulasTable;
    private JPanel tablePanel;

    private JButton refreshFormulasButton;
    private JButton deleteFormulaButton;

    public FormulasPanel(HomeView homeView) {
        this.homeView = homeView;
        initializePanel();
    }

    private void initializePanel() {
        this.setLayout(new BorderLayout());
        this.add(formulasPanel, BorderLayout.CENTER);

        //  VIEW FORMULAS

        formulasTable = new JTable();
        tablePanel.add(new JScrollPane(formulasTable));
    }

    public void showFormulas(ResultSet formulas, SystemController controller) throws SQLException {
        int columnCount = formulas.getMetaData().getColumnCount();
        int rowCount = getRowCount(formulas);

        String[] columnNames = getColumnNames(formulas, columnCount);
        String [][] data = new String[rowCount][columnCount];


        while (formulas.next()) {
            for (int i = 0 ; i < columnCount ; i++) {
                data[formulas.getRow() - 1][i] = formulas.getString(i + 1);
            }
        }
        formulasTable = new JTable(data, columnNames);
        formulasTable.setAutoCreateRowSorter(true);
        setTableFont(formulasTable);
        tablePanel.remove(0);
        tablePanel.add(new JScrollPane(formulasTable));
        formulasTable.getModel().addTableModelListener(controller);

        this.validate();
        homeView.getProductionPanel().getFormulas();
        homeView.getStoragePanel().getFormulas();
    }

    public void showCreateFormulaView(SystemController controller) {
        createFormulaView = new CreateFormulaView(controller, homeView);
    }


    //  ADD FORMULA GETTER


    public JButton getCreateNewFormulaButton() {
        return createNewFormulaButton;
    }

    public CreateFormulaView getCreateFormulaView() {
        return createFormulaView;
    }


    //  ADD FORMULA GETTER


    public JTable getFormulasTable() {
        return formulasTable;
    }

    public JButton getRefreshFormulasButton() {
        return refreshFormulasButton;
    }

    public JButton getDeleteFormulaButton() {
        return deleteFormulaButton;
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
        createNewFormulaButton.addActionListener(controller);
        refreshFormulasButton.addActionListener(controller);
        deleteFormulaButton.addActionListener(controller);
    }

}
