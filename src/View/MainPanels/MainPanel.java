package View.MainPanels;

import Controller.SystemController;

import javax.swing.*;
import java.sql.ResultSet;

public interface MainPanel {
    public int getRowCount(ResultSet set);
    public String[] getColumnNames(ResultSet set, int columnCount);
    public void setTableFont(JTable table);
    public void addActionListeners(SystemController controller);
}
