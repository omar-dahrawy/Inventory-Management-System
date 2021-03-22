package View.MainPanels;

import javax.swing.*;
import java.sql.ResultSet;

public interface MainPanel {
    public int getRowCount(ResultSet set);
    public String[] getColumnNames(ResultSet set, int columnCount);
    public void setTableFont(JTable table);
}
