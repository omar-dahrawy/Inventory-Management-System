package Controller;

import Model.Constants;
import View.AddProductionView;
import View.CreateFormulaView;
import View.HomeView;
import View.MainPanels.FormulasPanel;
import View.MainPanels.OrdersPanel;
import View.MainPanels.ProductionPanel;
import View.MainPanels.StoragePanel;
import View.SystemView;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class SystemController implements ActionListener, TableModelListener {

    private final SystemView view;
    private final HomeView homeView;
    private final OrdersPanel ordersPanel;
    private final StoragePanel storagePanel;
    private final FormulasPanel formulasPanel;
    private final ProductionPanel productionPanel;

    private final Constants K = new Constants();

    private int currentUserID;

    private Connection databaseConnection;
    private Statement sqlStatement;

    public SystemController(SystemView view) {
        this.view = view;
        this.homeView = view.getHomeView();
        this.ordersPanel = homeView.getOrdersPanel();
        this.storagePanel = homeView.getStoragePanel();
        this.formulasPanel = homeView.getFormulasPanel();
        this.productionPanel = homeView.getProductionPanel();
        this.view.addActionListeners(this);
    }

    void connectToServer() {

        //CONNECT TO DATABASE
        if (databaseConnection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                databaseConnection = DriverManager.getConnection("jdbc:postgresql://localhost/Eagles", "postgres", "admin");
                System.out.println("Database server connection successful");
                sqlStatement = databaseConnection.createStatement();
                login();
            } catch (Exception e) {
                showErrorMessage("Could not connect to database server", "Connection to database server refused.\nCheck that the server is running and accepting connections. ", e.getLocalizedMessage());
            }
        } else {
            login();
        }
    }

    void login() {
        String username = view.getLoginView().getUsername();
        String password = view.getLoginView().getPassword();

        try {
            ResultSet users = sqlStatement.executeQuery("SELECT * FROM public.\"Users\" where \"Username\" = '" + username + "'");
            if (users.next()) {
                if (users.getString(4).equals(password)) {
                    showMessage("Login Message", "Login successful.");
                    currentUserID = Integer.parseInt(users.getString(1));
                    users.close();
                    getMaterials();
                    viewVendors();
                    viewGeneralExpenses();
                    viewMaterialExpenses();
                    viewProductions();
                    viewOrders();
                    getFormulas();
                    viewStorage();
                    view.goToHome();
                } else {
                    showMessage("Login Error", "Password is incorrect.");
                }
            } else {
                showMessage("Login Error", "Username does not exist.");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /*
     *
     *      ADD GENERAL EXPENSE
     *
     */

    void addGeneralExpense() {
        if (checkGeneralExpenses()) {
            String itemName = "'" + homeView.getGeItemName() + "',";
            String itemQuantity = homeView.getGeQuantity() + ",";
            String dateOfPurchase = "'" + homeView.getGeDate() + "'";
            String dateOfEntry = "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "',";

            try {
                String sqlQuery = "insert into public.\"General_Expenses\" values (DEFAULT," + itemName + itemQuantity + "null, " + currentUserID + "," + dateOfEntry + dateOfPurchase + ")";
                System.out.println(sqlQuery);
                sqlStatement.executeUpdate(sqlQuery);
                showMessage("Operation successful", "General Expense added.");
                homeView.clearGeneralExpensesFields();
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "General Expense could not be added.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkGeneralExpenses() {
        boolean flag = true;

        if (homeView.getGeItemName().equals("")) {
            flag = false;
            showMessage("Error adding expense", "Item name cannot be empty.");
        } else if (homeView.getGeQuantity() == -13.11) {
            flag = false;
            showMessage("Error adding expense", "Item quantity must be a number.");
        } else if (homeView.getGeQuantity() == 0) {
            flag = false;
            showMessage("Error adding expense", "Item quantity cannot be 0 or empty.");
        } else if (homeView.getGeDate() == null) {
            flag = false;
            showMessage("Error adding expense", "Date of purchase cannot be empty.");
        }
        return  flag;
    }

    /*
     *
     *      ADD MATERIAL EXPENSE
     *
     */

    void addMaterialExpense() {
        if (checkMaterialExpenses()) {
            String materialID = homeView.getMaterialID();
            String materialQuantity = homeView.getMeQuantity() + ",";
            String materialInvoice = "'" + homeView.getMeInvoice() + "'";
            String vendorID = homeView.getVendorID();
            String materialUnit = "'" + homeView.getMaterialUnit() + "',";
            String dateOfEntry = "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "',";
            String dateOfPurchase = "'" + homeView.getMeDate() + "',";

            try {
                String sqlQuery = "insert into public.\"Material_Expenses\" values (DEFAULT," + materialID + "," + materialQuantity + materialUnit + currentUserID + "," + dateOfEntry + dateOfPurchase + vendorID + "," + materialInvoice + ")";
                sqlStatement.executeUpdate(sqlQuery);
                showMessage("Operation successful", "General Expense added.");
                String updateQuery = "UPDATE \"Materials\" SET \"Available_quantity\" = \"Available_quantity\" + " + homeView.getMeQuantity() + " WHERE \"Material_ID\" = " + homeView.getMaterialID();
                sqlStatement.executeUpdate(updateQuery);
                getMaterials();
                homeView.clearMaterialExpensesFields();
                //updateFormulasPrice(materialID,materialName, materialPrice, materialQuantity);
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Material Expense could not be added.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkMaterialExpenses() {
        boolean flag = true;

        if (homeView.getMaterialID().equals("")) {
            flag = false;
            showMessage("Error adding expense", "Please select a Material.");
        } else if (homeView.getMeQuantity() == -13.11) {
            flag = false;
            showMessage("Error adding expense", "Material quantity must be in numbers only.");
        } else if (homeView.getMeQuantity() == 0) {
            flag = false;
            showMessage("Error adding expense", "Material quantity cannot be 0 or empty.");
        } else if (homeView.getMeDate() == null) {
            flag = false;
            showMessage("Error adding expense.", "Date of purchase cannot be empty.");
        }
        return  flag;
    }

    /*
     *
     *      VIEW GENERAL EXPENSES
     *
     */

    void viewGeneralExpenses() {

        LocalDate fromDate = homeView.getVgeFromDate();
        LocalDate toDate = homeView.getVgeToDate();

        String VeDoeSelected = (homeView.getVgeDoeRadioButton().isSelected()) ? ((" WHERE \"Date_of_entry\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_entry\" <= '" + toDate + "'" : "")) : "";
        String VeDopSelected = (homeView.getVgeDopRadioButton().isSelected()) ? ((" WHERE \"Date_of_purchase\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_purchase\" <= '" + toDate + "'" : "")) : "";

        String query = "SELECT * FROM public.\"General_Expenses\"" + VeDoeSelected + VeDopSelected;

        if (checkViewGeneralExpenses()) {
            try {
                PreparedStatement expensesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet results = expensesQuery.executeQuery();
                homeView.showGeneralExpenses(results, this);

            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing expenses.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewGeneralExpenses() {
        boolean flag = true;

        if (homeView.getVgeDoeRadioButton().isSelected() || homeView.getVgeDopRadioButton().isSelected()) {
            if (homeView.getVgeFromDate() == null && homeView.getVgeToDate() == null) {
                flag = false;
                showMessage("Error viewing expenses","Please enter viewing dates.");
            } else if (homeView.getVgeFromDate() == null && homeView.getVgeToDate() != null) {
                flag = false;
                showMessage("Error viewing expenses","Please enter a From viewing date.");
            }
        }
        return flag;
    }

    void updateGeneralExpense(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = homeView.getVgeTable().getValueAt(row, column).toString();
            String columnName = homeView.getVgeTable().getColumnName(column);
            String id = homeView.getVgeTable().getValueAt(row, 0).toString();

            String query = "UPDATE \"General_Expenses\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Purchase_ID\" = " + id;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Item updated successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not update item. Please review the new values.");
            }
        }
        viewGeneralExpenses();
    }

    void deleteGeneralExpense() {

        int[] rows = homeView.getVgeTable().getSelectedRows();

        for (int i: rows) {
            String id = homeView.getVgeTable().getValueAt(i, 0).toString();
            String query = "DELETE FROM \"General_Expenses\" WHERE \"Purchase_ID\" = " + id;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Delete successful","Item deleted successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not delete item.");
            }
        }
        viewGeneralExpenses();
    }

    /*
     *
     *      VIEW MATERIAL EXPENSES
     *
     */

    void viewMaterialExpenses() {

        LocalDate fromDate = homeView.getVmeFromDate();
        LocalDate toDate = homeView.getVmeToDate();

        String VeDoeSelected = (homeView.getVmeDoeRadioButton().isSelected()) ? ((" WHERE \"Date_of_entry\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_entry\" <= '" + toDate + "'" : "")) : "";
        String VeDopSelected = (homeView.getVmeDopRadioButton().isSelected()) ? ((" WHERE \"Date_of_purchase\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_purchase\" <= '" + toDate + "'" : "")) : "";

        String query = "SELECT \"Purchase_ID\", \"Material_ID\", \"Quantity\", \"Unit\", \"Vendor\", \"Invoice_number\", \"User_ID\", \"Date_of_entry\", \"Date_of_purchase\" FROM public.\"Material_Expenses\"" + VeDoeSelected + VeDopSelected;

        if (checkViewMaterialExpenses()) {
            try {
                PreparedStatement expensesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet results = expensesQuery.executeQuery();
                homeView.showMaterialExpenses(results, this);

            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing expenses.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewMaterialExpenses() {
        boolean flag = true;

        if (homeView.getVgeDoeRadioButton().isSelected() || homeView.getVgeDopRadioButton().isSelected()) {
            if (homeView.getVgeFromDate() == null && homeView.getVgeToDate() == null) {
                flag = false;
                showMessage("Error viewing expenses","Please enter viewing dates.");
            } else if (homeView.getVgeFromDate() == null && homeView.getVgeToDate() != null) {
                flag = false;
                showMessage("Error viewing expenses","Please enter a From viewing date.");
            }
        }
        return flag;
    }

    void updateMaterialExpense(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = homeView.getVmeTable().getValueAt(row, column).toString();
            String columnName = homeView.getVmeTable().getColumnName(column);
            String id = homeView.getVmeTable().getValueAt(row, 0).toString();

            String query = "UPDATE \"Material_Expenses\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Material_ID\" = " + id;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Item updated successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not update item. Please review the new values.");
            }
        }
        viewMaterialExpenses();
    }

    void deleteMaterialExpense() {
        int[] rows = homeView.getVmeTable().getSelectedRows();

        for (int i: rows) {
            String id = homeView.getVmeTable().getValueAt(i, 0).toString();
            String query = "DELETE FROM \"Material_Expenses\" WHERE \"Purchase_ID\" = " + id;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Delete successful","Item deleted successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not delete item.");
            }
        }
        viewGeneralExpenses();
    }

    /*
     *
     *      ADD ORDER
     *
     */

    void addOrder() {
        if (checkAddOrder()) {
            String customer = ordersPanel.getAddCustomer();
            String orderDetails = ordersPanel.getAddDetails();
            java.sql.Date DOP = java.sql.Date.valueOf(ordersPanel.getAddDop());
            java.sql.Date DOD = java.sql.Date.valueOf(ordersPanel.getAddDod());
            java.sql.Date DOE = java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            String sql = "INSERT INTO \"Orders\" values (DEFAULT, ?, ?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, customer);
                query.setString(2, orderDetails);
                query.setString(3, K.status_1);
                query.setInt(4, currentUserID);
                query.setDate(5, DOP);
                query.setDate(6, DOD);
                query.setDate(7, DOE);
                query.executeUpdate();
                query.close();
                ordersPanel.clearAddFields();
                showMessage("Operation successful", "New order added.");
                viewOrders();
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Order could not be added.");
                throwables.printStackTrace();
            }
        }

    }

    boolean checkAddOrder() {
        boolean flag = true;

        if (ordersPanel.getAddCustomer().equals("")) {
            flag = false;
            showMessage("Error adding order", "Order customer cannot be empty.");
        } else if (ordersPanel.getAddBatchSerial().equals("")) {
            flag = false;
            showMessage("Error adding order", "Batch serial cannot be empty.");
        } else if (ordersPanel.getAddDetails().equals("")) {
            flag = false;
            showMessage("Error adding order", "Order details cannot be empty.");
        } else if (ordersPanel.getAddDod() == null) {
            flag = false;
            showMessage("Error adding order", "Date of delivery cannot be empty.");
        } else if (ordersPanel.getAddDop() == null) {
            flag = false;
            showMessage("Error adding order", "Date of production cannot be empty.");
        }
        return flag;
    }

    /*
     *
     *      VIEW ORDERS
     *
     */

    void viewOrders() {
        if (checkViewOrders()) {
            boolean customerSelected = ordersPanel.getFilterCustomerButton().isSelected();
            boolean DateSelected = ordersPanel.getFilterDateButton().isSelected();
            boolean statusSelected = ordersPanel.getFilterStatusButton().isSelected();
            boolean batchSerialSelected = ordersPanel.getFilterBatchSerialButton().isSelected();

            String query;

            if (customerSelected) {
                query = "SELECT * FROM public.\"Orders\" WHERE \"Customer\" = '" + ordersPanel.getFilterCustomer() + "'";
            } else if (DateSelected) {
                String selectedDateType = ordersPanel.getFilterDateType();
                String dateType = (selectedDateType.equals("Date of entry")) ? "Date_of_entry" : (selectedDateType.equals("Date of production") ? "Date_of_production" : "Date_of_delivery");
                if (ordersPanel.getFilterToDate() != null) {
                    query = "SELECT * FROM public.\"Orders\" WHERE \"" + dateType + "\" >= '" + ordersPanel.getFilterFromDate() + "' AND \"" + dateType + "\" <= '" + ordersPanel.getFilterToDate() + "'";
                } else {
                    query = "SELECT * FROM public.\"Orders\" WHERE \"" + dateType + "\" >= '" + ordersPanel.getFilterFromDate() + "'";
                }
            } else if (statusSelected) {
                query = "SELECT * FROM public.\"Orders\" WHERE \"Status\" = '" + ordersPanel.getFilterStatus() + "'";
            } else if (batchSerialSelected) {
                query = "SELECT * FROM public.\"Orders\" WHERE \"Batch_serial\" = '" + ordersPanel.getFilterSerial() + "'";
            } else {
                query = "SELECT * FROM public.\"Orders\"";
            }

            try {
                PreparedStatement ordersQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet orders = ordersQuery.executeQuery();
                homeView.getOrdersPanel().showOrders(orders, this);
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing orders.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewOrders() {
        boolean flag = true;
        if (ordersPanel.getFilterCustomerButton().isSelected()) {
            if (ordersPanel.getFilterCustomer().equals("")) {
                flag = false;
                showMessage("Error viewing orders", "Please enter a customer to filter by.");
            }
        } else if (ordersPanel.getFilterStatusButton().isSelected()) {
            if (ordersPanel.getFilterStatus().equals("Select Status")) {
                flag = false;
                showMessage("Error viewing orders", "Please select a status to filter by.");
            }
        } else if (ordersPanel.getFilterBatchSerialButton().isSelected()) {
            if (ordersPanel.getFilterSerial().equals("")) {
                flag = false;
                showMessage("Error viewing orders", "Please enter a batch serial to filter by.");
            }
        } else if (ordersPanel.getFilterDateButton().isSelected()) {
            if (ordersPanel.getFilterDateType().equals("Select date type")) {
                flag = false;
                showMessage("Error viewing orders", "Please select a date type to filter by.");
            } else if (ordersPanel.getFilterFromDate() == null) {
                flag = false;
                showMessage("Error viewing orders", "Please enter a From date to filter by.");
            }
        }

        return flag;
    }

    void updateOrder(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = ordersPanel.getOrdersTable().getValueAt(row, column).toString();
            String columnName = ordersPanel.getOrdersTable().getColumnName(column);
            String id = ordersPanel.getOrdersTable().getValueAt(row, 0).toString();

            String query = "UPDATE \"Orders\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Order_ID\" = " + id;
            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Item updated successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not update item. Please review the new values.");
            }
        }
        viewOrders();
    }

    void deleteOrder() {
        int row = ordersPanel.getOrdersTable().getSelectedRow();
        String id = ordersPanel.getOrdersTable().getValueAt(row, 0).toString();

        String query = "DELETE FROM \"Orders\" WHERE \"Order_ID\" = " + id;

        try {
            sqlStatement.executeUpdate(query);
            showMessage("Delete successful","Item deleted successfully.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            showMessage("Error performing operation", "Could not delete item.");
        }
        viewOrders();
    }

    /*
     *
     *      VIEW MATERIALS
     *
     */

    void getMaterials() {
        String query = "SELECT * FROM public.\"Materials\"";
        try {
            PreparedStatement materialsQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet materials = materialsQuery.executeQuery();
            homeView.getMaterials(materials, this);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void updateMaterial(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String columnName = homeView.getVmTable().getColumnName(column);
            String newValue = homeView.getVmTable().getValueAt(row, column).toString();
            String id = homeView.getVmTable().getValueAt(row, 0).toString();

            String query = "UPDATE \"Materials\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Material_ID\" = " + id;
            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Item updated successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not update item. Please review the new values.");
            }
        }
        getMaterials();
    }

    /*
     *
     *      ADD MATERIAL
     *
     */

    void addMaterial() {
        if (checkAddMaterial()) {
            String sql = "INSERT INTO \"Materials\" values (DEFAULT, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString( 1, homeView.getAmNameField());
                query.setDouble( 2, 0.0);
                query.setDate( 3, java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
                query.setString( 4, homeView.getAmUnitField());
                query.executeUpdate();
                query.close();
                showMessage("Operation successful","New material added.");
                homeView.clearAddMaterialFields();
                getMaterials();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation","Material could not be added.");
            }
        }
    }

    boolean checkAddMaterial() {
        boolean flag = true;

        if (homeView.getAmNameField().equals("")) {
            flag = false;
            showMessage("Error adding material","Material name cannot be empty.");
        } else if (homeView.getAmUnitField().equals("")) {
            flag = false;
            showMessage("Error adding material","Material unit cannot be empty.");
        }
        return flag;
    }

    /*
     *
     *      ADD PRODUCTION
     *
     */

    void addProduction() {
        AddProductionView ApView = productionPanel.getAddProductionView();

        if (checkAddProduction()) {
            String formula = ApView.getApFormula();
            double quantity = ApView.getApQuantity();

            String sql = "INSERT INTO \"Production\" values (DEFAULT, ?, ?, ?, ?)";

            try {
                Array array = databaseConnection.createArrayOf("VARCHAR", ApView.getApOrders());
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, formula);
                query.setDouble(2,quantity);
                query.setArray(3, array);
                query.setString(4, K.status_1);
                query.executeUpdate();
                query.close();
                showMessage("Operation successful", "New production added.");
                viewProductions();
                addItemsToStorage();
                ApView.dispatchEvent(new WindowEvent(ApView, WindowEvent.WINDOW_CLOSING));
            } catch (SQLException throwables) {
                showErrorMessage("Error performing operation", "New production could not be added.", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }
    }

    boolean checkAddProduction() {
        AddProductionView ApView = productionPanel.getAddProductionView();

        if (ApView.getApQuantity() == -13.11) {
            showMessage("Error adding production", "Production quantity must be numbers.");
            return false;
        } else if (ApView.getApQuantity() == 0) {
            showMessage("Error adding production", "Production quantity cannot be 0 or empty.");
            return false;
        } else if (ApView.getApOrders().length == 0) {
            showMessage("Error adding production", "Order IDs cannot be empty.");
            return false;
        } else if (ApView.getApFormula().equals("Select Formula")) {
            showMessage("Error adding production","Please choose a batch formula.");
            return false;
        } else {
            return checkAddProductionFields();
        }
    }

    boolean checkAddProductionFields() {
        AddProductionView ApView = productionPanel.getAddProductionView();
        int count = 0;

        for (JPanel panel: ApView.getTanksPanels()) {
            count++;
            double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All tanks fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All tanks fields must contain numbers");
                return false;
            }
        }
        for (JPanel panel: ApView.getDrumsPanels()) {
            count++;
            double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All drums fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All drums fields must contain numbers");
                return false;
            }
        }
        for (JPanel panel: ApView.getPailsPanels()) {
            count++;
            double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All pails fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All pails fields must contain numbers");
                return false;
            }
        }
        for (JPanel panel: ApView.getCartonsPanels()) {
            count++;
            double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All cartons fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All cartons fields must contain numbers");
                return false;
            }
        }
        for (JPanel panel: ApView.getGallonsPanels()) {
            count++;
            double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All gallons fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All gallons fields must contain numbers");
                return false;
            }
        }

        if (count > 0) {
            return true;
        } else {
            showMessage("Error adding production","You must add at least one container");
            return false;
        }
    }

    double parseDouble(String string) {
        if (!string.equals("")) {
            try {
                return Double.parseDouble(string);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            return 0.0;
        }
        return -13.11;
    }

    /*
     *
     *      VIEW PRODUCTIONS
     *
     */

    void viewProductions() {
        if (checkViewProductions()) {
            boolean batchSerialSelected = productionPanel.getFilterSerialSelected();
            boolean orderIdSelected = productionPanel.getFilterOrderIdSelected();
            boolean formulaSelected = productionPanel.getFilterFormulaSelected();
            boolean statusSelected = productionPanel.getFilterStatusSelected();

            String query;

            if (batchSerialSelected) {
                String serial = "'" + productionPanel.getFilterSerial() + "'";
                query = "SELECT * FROM \"Production\" WHERE \"Batch_serial\" = " + serial;
            } else if (orderIdSelected) {
                String orderID = productionPanel.getFilterOrderId();
                query = "SELECT * FROM \"Production\" WHERE \"Orders_IDs\" @> ARRAY['" + orderID + "']::varchar[]";
            } else if (formulaSelected) {
                String formula = productionPanel.getFilterFormula();
                query = "SELECT * FROM \"Production\" WHERE \"Batch_formula\" = '" + formula + "'";
            } else if (statusSelected) {
                String status = productionPanel.getFilterStatus();
                query = "SELECT * FROM \"Production\" WHERE \"Production_status\" = '" + status + "'";
            } else {
                query = "SELECT * FROM \"Production\"";
            }

            try {
                PreparedStatement samplesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet productions = samplesQuery.executeQuery();
                productionPanel.showProductions(productions, this);
            } catch (SQLException throwables) {
                showErrorMessage("Error performing operation", "Error viewing productions.", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }

    }

    boolean checkViewProductions() {
        boolean flag = true;

        if (productionPanel.getFilterSerialSelected()) {
            if (productionPanel.getFilterSerial().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter a batch serial to filter by.");
            }
        } else if (productionPanel.getFilterOrderIdSelected()) {
            if (productionPanel.getFilterOrderId().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter an Order ID to filter by.");
            }
        } else if (productionPanel.getFilterFormulaSelected()) {
            if (productionPanel.getFilterFormula().equals("Select Formula")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter a Formula to filter by.");
            }
        } else if (productionPanel.getFilterStatusSelected()) {
            if (productionPanel.getFilterStatus().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter a Status to filter by.");
            }
        }
        return flag;
    }

    void updateProduction(TableModelEvent e) {
        JTable productionTable = productionPanel.getProductionTable();
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = productionTable.getValueAt(row, column).toString();
            String columnName = productionTable.getColumnName(column);
            String serial = "'" + productionTable.getValueAt(row, 0).toString() + "'";

            String query = "UPDATE \"Production\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Batch_serial\" = " + serial;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Batch updated successfully.");
                if (columnName.equals("Production_status") && newValue.equals(K.status_3)) {
                    String formulaName = productionTable.getValueAt(row,1).toString();
                    double quantity = Double.parseDouble(productionTable.getValueAt(row,2).toString());
                    deductMaterialQuantity(formulaName, quantity);
                }
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Could not update batch. Please review the new values.");
                throwables.printStackTrace();
            }
        }
        viewProductions();
    }

    private void deductMaterialQuantity(String formulaName, double formulaQuantity) {
        int row = 0;
        for (int i = 0 ; i < productionPanel.getProductionTable().getRowCount() ; i++) {
            if (productionPanel.getProductionTable().getValueAt(i, 0).toString().equals(formulaName)) {
                row = i;
                break;
            }
        }

        String formulaDescription = formulasPanel.getFormulasTable().getValueAt(row, 1).toString();
        String [] materials = formulaDescription.split(" - ");

        for (String material : materials) {
            String materialName = material.split(":")[0];
            double materialQuantity = Double.parseDouble(material.split(":")[1].substring(1, material.split(":")[1].lastIndexOf(" ")));
            double availableQuantity = 0.0;

            for (int i = 0 ; i < homeView.getVmTable().getRowCount() ; i++) {
                if (homeView.getVmTable().getValueAt(i, 1).toString().equals(materialName)) {
                    availableQuantity = Double.parseDouble(homeView.getVmTable().getValueAt(i, 2).toString());
                }
            }

            double newQuantity = availableQuantity - (materialQuantity * formulaQuantity);

            if (newQuantity >= 0) {
                String sql = "UPDATE \"Materials\" SET \"Available_quantity\" = ? WHERE \"Material_name\" = ?";
                try {
                    PreparedStatement query = databaseConnection.prepareStatement(sql);
                    query.setDouble(1, newQuantity);
                    query.setString(2, materialName);
                    query.executeUpdate();
                    query.close();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            } else {
                showMessage("Error updating material quantities","Not enough material quantities to deduct production materials");
                break;
            }
        }
        getMaterials();
    }

    void deleteProduction() {
        JTable productionTable = productionPanel.getProductionTable();

        if (productionTable != null) {
            if (productionTable.getModel() != null) {
                int row = productionTable.getSelectedRow();
                String serial = "'" + productionTable.getValueAt(row, 0).toString() + "'";

                String query = "DELETE FROM \"Production\" WHERE \"Batch_serial\" = " + serial;

                try {
                    sqlStatement.executeUpdate(query);
                    showMessage("Delete successful","Batch deleted successfully.");
                } catch (SQLException throwables) {
                    showMessage("Error performing operation", "Could not delete batch.");
                    throwables.printStackTrace();
                }
                viewProductions();
            }
        }
    }

    /*
     *
     *      CREATE NEW FORMULA
     *
     */

    void createNewFormula(String formulaName, String formulaDescription, String formulaQuantity) {
        String sql = "INSERT INTO \"Formulas\" values (?, ?, ?, ?)";
        try {
            PreparedStatement query = databaseConnection.prepareStatement(sql);
            query.setString(1, formulaName);
            query.setString(2, formulaDescription);
            query.setString(3, formulaQuantity);
            query.setDouble(4, 0.0);
            query.executeUpdate();
            query.close();
            showMessage("Operation successful","New formula created.");
            formulasPanel.getCreateFormulaView().dispatchEvent(new WindowEvent(formulasPanel.getCreateFormulaView(), WindowEvent.WINDOW_CLOSING));
            getFormulas();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            showErrorMessage("Operation unsuccessful","Could not create new formula.", throwables.getLocalizedMessage());
        }
    }

    void checkCreateFormula() {
        CreateFormulaView CfView = formulasPanel.getCreateFormulaView();
        String formulaName = CfView.getFormulaName();
        String formulaDescription = "";

        ArrayList<JCheckBox> checkBoxes = CfView.getCheckBoxes();
        ArrayList<JTextField> textFields = CfView.getTextFields();

        if (formulaName.equals("")) {
            showMessage("Error creating new formula","Formula name cannot be empty.");
        } else {
            int count = 0;
            Double formulaQuantity = 0.0;
            for (int i = 0 ; i < checkBoxes.size(); i++) {
                JCheckBox checkBox = checkBoxes.get(i);
                if (checkBox.isSelected()) {
                    count++;
                    if (textFields.get(i).getText().equals("")) {
                        showMessage("Error creating new formula", "Please add a quantity for the material " + checkBox.getText());
                        count = -1;
                        break;
                    } else {
                        formulaDescription += checkBox.getText();
                        formulaDescription += ": ";
                        formulaDescription += textFields.get(i).getText();
                        formulaQuantity += Double.parseDouble(textFields.get(i).getText());
                        formulaDescription += " " + homeView.getVmTable().getValueAt(i, 4).toString();
                        formulaDescription += " - ";
                    }
                }
            }
            if (count >= 0) {
                if (count == 0) {
                    showMessage("Error creating new formula", "Please select at least one material for the new Formula.");
                } else {
                    formulaDescription = formulaDescription.substring(0, formulaDescription.length() - 3);
                    createNewFormula(formulaName, formulaDescription, formulaQuantity + " Kg");
                }
            }
        }
    }

    /*
     *
     *      VIEW FORMULAS
     *
     */

    void getFormulas() {
        String query = "SELECT * FROM public.\"Formulas\"";
        try {
            PreparedStatement formulasQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet formulas = formulasQuery.executeQuery();
            formulasPanel.showFormulas(formulas, this);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            showMessage("Error performing operation", "Error viewing formulas.");
        }
    }

    void updateFormula(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            JTable formulasTable = formulasPanel.getFormulasTable();

            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = formulasTable.getValueAt(row, column).toString();
            String columnName = formulasTable.getColumnName(column);
            String id = formulasTable.getValueAt(row, 0).toString();

            String query = "UPDATE \"Formulas\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Formula_ID\" = '" + id + "'";

            try {
                sqlStatement.executeUpdate(query);
                recalculateFormulaQuantity(id, row);
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Could not update batch. Please review the new values.");
                throwables.printStackTrace();
            }
        }
        getFormulas();
    }

    void recalculateFormulaQuantity(String formulaID, int row) {
        Double newQuantity = 0.0;
        String formulaDescription = formulasPanel.getFormulasTable().getValueAt(row, 1).toString();
        String [] materials = formulaDescription.split(" - ");

        for (int i = 0 ; i < materials.length ; i++) {
            materials[i] = materials[i].split(": ")[1];
        }
        for (int i = 0 ; i < materials.length ; i++) {
            materials[i] = materials[i].split(" ")[0];
        }
        for (String quantity : materials) {
            newQuantity += Double.parseDouble(quantity);
        }

        String sql = "UPDATE \"Formulas\" SET \"Formula_quantity\" = ? WHERE \"Formula_ID\" = ?";
        try {
            PreparedStatement query = databaseConnection.prepareStatement(sql);
            query.setString(1, newQuantity + " Kg");
            query.setString(2, formulaID);
            query.executeUpdate();
            query.close();
            showMessage("Update successful", "Formula updated successfully.");
        } catch (SQLException throwables) {
            showErrorMessage("Error updating formula price", "Formula price could not be updated", throwables.getLocalizedMessage());
            throwables.printStackTrace();
        }

    }

    void deleteFormula() {
        JTable formulasTable = formulasPanel.getFormulasTable();
        if (formulasTable != null) {
            if (formulasTable.getModel() != null) {
                int row = formulasTable.getSelectedRow();
                String id = "'" + formulasTable.getValueAt(row, 0).toString() + "'";

                String query = "DELETE FROM \"Formulas\" WHERE \"Formula_ID\" = " + id;

                try {
                    sqlStatement.executeUpdate(query);
                    showMessage("Delete successful","Formula deleted successfully.");
                } catch (SQLException throwables) {
                    showErrorMessage("Error performing operation", "Could not delete formula.", throwables.getLocalizedMessage());
                    throwables.printStackTrace();
                }
                getFormulas();
            }
        }
    }

    /*
     *
     *      ADD VENDOR
     *
     */

    void addVendor() {
        if (checkAddVendor()) {
            String vendorName = homeView.getAvVendorName();
            String contactName = homeView.getAvContactName();
            String contactNumber = homeView.getAvContactNumber();
            String contactEmail = homeView.getAvContactEmail();

            String sql = "INSERT INTO \"Vendors\" values (DEFAULT, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, vendorName);
                query.setString(2, contactName);
                query.setString(3, contactNumber);
                query.setString(4, contactEmail);
                query.executeUpdate();
                query.close();
                showMessage("Operation successful", "New vendor added.");
                viewVendors();
                homeView.clearAddVendorFields();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Operation unsuccessful", "Could not add new vendor.");
            }
        }
    }

    boolean checkAddVendor() {
        boolean flag = true;

        if (homeView.getAvVendorName().equals("")) {
            flag = false;
            showMessage("Error adding vendor", "Vendor name cannot be empty.");
        } else if (homeView.getAvContactName().equals("")) {
            flag = false;
            showMessage("Error adding vendor","Contact name cannot be empty.");
        } else if (homeView.getAvContactNumber().equals("")) {
            flag = false;
            showMessage("Error adding vendor","Contact number cannot be empty.");
        } else if (homeView.getAvContactEmail().equals("")) {
            flag = false;
            showMessage("Error adding vendor","Contact email cannot be empty.");
        }

        return flag;
    }

    /*
     *
     *      VIEW VENDORS
     *
     */

    void viewVendors() {
        if (checkViewVendors()) {
            boolean vendorIsSelected = homeView.getVvVendorRadioButton().isSelected();
            boolean contactIsSelected = homeView.getVvContactRadioButton().isSelected();

            String query;

            if (vendorIsSelected) {
                String vendorName = "'" + homeView.getVvVendor() + "'";
                query = "SELECT * FROM public.\"Vendors\" WHERE \"Vendor_name\" = " + vendorName;
            } else if (contactIsSelected) {
                String contactName = "'" + homeView.getVvContact() + "'";
                query = "SELECT * FROM public.\"Vendors\" WHERE \"Contact_name\" = " + contactName;
            } else {
                query = "SELECT * FROM public.\"Vendors\"";
            }

            try {
                PreparedStatement vendorsQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet vendors = vendorsQuery.executeQuery();
                homeView.showVendors(vendors, this);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Error viewing vendors.");
            }
        }
    }

    boolean checkViewVendors() {
        boolean flag = true;

        if (homeView.getVvVendorRadioButton().isSelected()) {
            if (homeView.getVvVendor().equals("")) {
                System.out.println("VvV");
                flag = false;
                showMessage("Error viewing vendors", "Please enter a vendor name to filter by.");
            }
        } else if (homeView.getVvContactRadioButton().isSelected()) {
            if (homeView.getVvContact().equals("")) {
                flag = false;
                showMessage("Error viewing vendors", "Please enter a contact name to filter by.");
            }
        }
        return flag;
    }

    void updateVendor(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = homeView.getVvTable().getValueAt(row, column).toString();
            String columnName = homeView.getVvTable().getColumnName(column);
            String id = homeView.getVvTable().getValueAt(row, 0).toString();

            String query = "UPDATE \"Vendors\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Vendor_ID\" = " + id;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Vendor updated successfully.");
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Could not update vendor. Please review the new values.");
                throwables.printStackTrace();
            }
        }
        viewVendors();
    }

    void deleteVendor() {
        if (homeView.getVvTable() != null) {
            if (homeView.getVvTable().getModel() != null) {
                int row = homeView.getVvTable().getSelectedRow();
                String id = homeView.getVvTable().getValueAt(row, 0).toString();

                String query = "DELETE FROM \"Vendors\" WHERE \"Vendor_ID\" = " + id;

                try {
                    sqlStatement.executeUpdate(query);
                    showMessage("Delete successful","Vendor deleted successfully.");
                } catch (SQLException throwables) {
                    showErrorMessage("Error performing operation", "Could not delete vendor.", throwables.getLocalizedMessage());
                    throwables.printStackTrace();
                }
                viewVendors();
            }
        }
    }

    /*
     *
     *      STORAGE
     *
     */

    void addItemsToStorage() {
        AddProductionView ApView = productionPanel.getAddProductionView();

        String formulaName = ApView.getApFormula();
        ArrayList<JPanel> tanksPanels = ApView.getTanksPanels();
        ArrayList<JPanel> pailsPanels = ApView.getPailsPanels();
        ArrayList<JPanel> drumsPanels = ApView.getDrumsPanels();
        ArrayList<JPanel> cartonsPanels = ApView.getCartonsPanels();
        ArrayList<JPanel> gallonsPanels = ApView.getGallonsPanels();

        for (JPanel panel : tanksPanels) {
            String sql = "INSERT INTO \"Storage\" values(DEFAULT, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, formulaName);
                query.setString(2, "Tank");
                query.setDouble(3, Double.parseDouble(((JTextField)panel.getComponent(1)).getText()));
                query.setDouble(4, Double.parseDouble(((JTextField)panel.getComponent(2)).getText()));
                query.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        for (JPanel panel : pailsPanels) {
            String sql = "INSERT INTO \"Storage\" values(DEFAULT, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, formulaName);
                query.setString(2, "Pail");
                query.setDouble(3, Double.parseDouble(((JTextField)panel.getComponent(1)).getText()));
                query.setDouble(4, Double.parseDouble(((JTextField)panel.getComponent(2)).getText()));
                query.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        for (JPanel panel : drumsPanels) {
            String sql = "INSERT INTO \"Storage\" values(DEFAULT, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, formulaName);
                query.setString(2, "Drum");
                query.setDouble(3, Double.parseDouble(((JTextField)panel.getComponent(1)).getText()));
                query.setDouble(4, Double.parseDouble(((JTextField)panel.getComponent(2)).getText()));
                query.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        for (JPanel panel : cartonsPanels) {
            String sql = "INSERT INTO \"Storage\" values(DEFAULT, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, formulaName);
                query.setString(2, "Carton");
                query.setDouble(3, Double.parseDouble(((JTextField)panel.getComponent(1)).getText()));
                query.setDouble(4, Double.parseDouble(((JTextField)panel.getComponent(2)).getText()));
                query.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        for (JPanel panel : gallonsPanels) {
            String sql = "INSERT INTO \"Storage\" values(DEFAULT, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, formulaName);
                query.setString(2, "Gallon");
                query.setDouble(3, Double.parseDouble(((JTextField)panel.getComponent(1)).getText()));
                query.setDouble(4, Double.parseDouble(((JTextField)panel.getComponent(2)).getText()));
                query.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
        viewStorage();
    }

    void viewStorage() {
        if (checkViewStorage()) {
            boolean formulaFilterSelected = storagePanel.getFilterProductSelected();
            boolean containerFilterSelected = storagePanel.getFilterContainerSelected();

            String query;

            if (formulaFilterSelected) {
                query = "SELECT * FROM \"Storage\" WHERE \"Product_name\" = '" + storagePanel.getFilterProduct() + "'";
            } else if (containerFilterSelected) {
                query = "SELECT * FROM \"Storage\" WHERE \"Container_type\" = '" + storagePanel.getFilterContainerType() + "'";
            } else {
                query = "SELECT * FROM \"Storage\"";
            }

            try {
                PreparedStatement materialsQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet items = materialsQuery.executeQuery();
                homeView.getStoragePanel().getStorageItems(items, this);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewStorage() {
        if (storagePanel.getFilterContainerSelected()) {
            if (storagePanel.getFilterContainerType().equals("Select Container")) {
                showMessage("Error viewing storage", "Please select a container type to filter by");
                return false;
            }
        } else if (storagePanel.getFilterProductSelected()) {
            if (storagePanel.getFilterProduct().equals("Select Product")) {
                showMessage("Error viewing storage", "Please select a product to filter by");
                return false;
            }
        }
        return true;
    }

    void updateStorageItem(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            JTable storageTable = storagePanel.getStorageTable();
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = storageTable.getValueAt(row, column).toString();
            String columnName = storageTable.getColumnName(column);
            String id = storageTable.getValueAt(row, 0).toString();

            String query = "UPDATE \"Storage\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Item_ID\" = " + id;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Storage updated successfully.");
            } catch (SQLException throwables) {
                showErrorMessage("Error performing operation", "Could not update storage. Please review the new values.", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }
        viewStorage();
    }

    void deleteStorageItem() {
        JTable storageTable = storagePanel.getStorageTable();
        if (storageTable != null) {
            if (storageTable.getModel() != null) {
                int row = storageTable.getSelectedRow();
                String id = storageTable.getValueAt(row, 0).toString();

                String query = "DELETE FROM \"Storage\" WHERE \"Item_ID\" = " + id;

                try {
                    sqlStatement.executeUpdate(query);
                    showMessage("Delete successful","Storage item deleted successfully.");
                } catch (SQLException throwables) {
                    showErrorMessage("Error performing operation", "Could not delete storage item.", throwables.getLocalizedMessage());
                    throwables.printStackTrace();
                }
                viewStorage();
            }
        }
    }

    /*
    --
    --
    --
     */

    void addMaterials() {
        String delete = "DELETE FROM \"Materials\"";
        String reset = "ALTER SEQUENCE \"Materials_Material_ID_seq\" RESTART WITH 1";
        String insert = "INSERT INTO \"Materials\" values (DEFAULT, ?, ?, ?, ?)";

        try {
            sqlStatement.executeUpdate(delete);
            sqlStatement.executeUpdate(reset);
        } catch (SQLException exception) {
            exception.printStackTrace();
        }

        try (BufferedReader br = new BufferedReader(new FileReader("/Users/omar_aldahrawy/Desktop/Materials.txt"))) {
            PreparedStatement query = databaseConnection.prepareStatement(insert);
            query.setDouble( 2, 0.0);
            query.setDate( 3, java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
            query.setString( 4, "Kg");
            String line;
            while ((line = br.readLine()) != null) {
                query.setString( 1, line);
                query.executeUpdate();
            }
            query.close();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void showMessage(String title, String message) {
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.INFORMATION_MESSAGE);
    }

    void showErrorMessage(String title, String message, String exception) {
        String[] options = {"Ok", "More info"};
        int x = JOptionPane.showOptionDialog(null, message, title, JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
        if (x == 1) {
            showMessage(title, exception);
        }
    }

    boolean checkUpdatePrivilege() {
        boolean flag = false;

        String username = JOptionPane.showInputDialog(null, "Enter username:",
                "Confirm data update", JOptionPane.INFORMATION_MESSAGE);

        String password = JOptionPane.showInputDialog(null, "Enter password:",
                "Confirm data update", JOptionPane.INFORMATION_MESSAGE);

        try {
            ResultSet users = sqlStatement.executeQuery("SELECT * FROM public.\"Users\" where \"Username\" = '" + username + "'");
            if (users.next()) {
                if (users.getString(4).equals(password)) {
                    if (users.getString(5).equals("Admin")) {
                        flag = true;
                    } else {
                        showMessage("Confirm error", "Update unsuccessful\nAdmin privileges needed to confirm update");
                    }
                } else {
                    showMessage("Confirm update error", "Password is incorrect.");
                }
            } else {
                showMessage("Confirm update error", "Username does not exist.");
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return flag;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getLoginView().getContinueButton()) {
            connectToServer();
        }

        // GENERAL EXPENSES

        else if (e.getSource() == homeView.getMeAddButton()) {
            addGeneralExpense();
        } else if (e.getSource() == homeView.getVgeViewButton()) {
            viewGeneralExpenses();
        } else if (e.getSource() == homeView.getVgeDeleteButton()) {
            deleteGeneralExpense();
        }

        //  MATERIAL EXPENSES

        else if (e.getSource() == homeView.getGeAddButton()) {
            addMaterialExpense();
        } else if (e.getSource() == homeView.getVmeViewButton()) {
            viewMaterialExpenses();
        } else if (e.getSource() == homeView.getVmeDeleteButton()) {
            deleteMaterialExpense();
        }

        //  ORDERS

        else if (e.getSource() == ordersPanel.getAddOrderButton()) {
            addOrder();
        } else if (e.getSource() == ordersPanel.getViewOrdersButton()) {
            viewOrders();
        } else if (e.getSource() == ordersPanel.getDeleteOrderButton()) {
            deleteOrder();
        }

        //  MATERIALS

        else if (e.getSource() == homeView.getVmRefreshButton()) {
            getMaterials();
        } else if (e.getSource() == homeView.getAmAddButton()) {
            addMaterial();

        //  FORMULAS

        } else if (e.getSource() == formulasPanel.getCreateNewFormulaButton()) {
            formulasPanel.showCreateFormulaView(this);
        } else if (e.getActionCommand().equals("Create Formula")) {
            checkCreateFormula();
        } else if (e.getSource() == formulasPanel.getRefreshFormulasButton()) {
            getFormulas();
        } else if (e.getSource() == formulasPanel.getDeleteFormulaButton()) {
            deleteFormula();
        }

        //  VENDORS

        else if (e.getSource() == homeView.getAvAddButton()) {
            addVendor();
        } else if (e.getSource() == homeView.getVvViewButton()) {
            viewVendors();
        } else if (e.getSource() == homeView.getDeleteVendorButton()) {
            deleteVendor();
        }

        //  PRODUCTION

        else if (e.getActionCommand().equals("Add Production")) {
            addProduction();
        } else if (e.getSource() == productionPanel.getAddNewProductionButton()) {
            productionPanel.showAddProductionView(this);
        } else if (e.getSource() == productionPanel.getViewProductionsButton()) {
            viewProductions();
        } else if (e.getSource() == productionPanel.getDeleteProductionButton()) {
            deleteProduction();
        }

        //  STORAGE

        else if (e.getSource() == storagePanel.getViewStorageButton()) {
            viewStorage();
        } else if (e.getSource() == storagePanel.getDeleteStorageButton()) {
            deleteStorageItem();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (homeView.getVgeTable() != null) {
            if (homeView.getVgeTable().getModel() != null) {
                if (e.getSource() == homeView.getVgeTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateGeneralExpense(e);
                    }
                }
            }
        } if (homeView.getVmeTable() != null) {
            if (homeView.getVmeTable().getModel() != null) {
                if (e.getSource() == homeView.getVmeTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateMaterialExpense(e);
                    }
                }
            }
        } if (ordersPanel.getOrdersTable() != null) {
            if (ordersPanel.getOrdersTable().getModel() != null) {
                if (e.getSource() == ordersPanel.getOrdersTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateOrder(e);
                    }
                }
            }
        } if (homeView.getVmTable() != null) {
            if (homeView.getVmTable().getModel() != null) {
                if (e.getSource() == homeView.getVmTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateMaterial(e);
                    }
                }
            }
        } if (productionPanel.getProductionTable() != null) {
            if (productionPanel.getProductionTable().getModel() != null) {
                if (e.getSource() == productionPanel.getProductionTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateProduction(e);
                    }
                }
            }
        } if (formulasPanel.getFormulasTable() != null) {
            if (formulasPanel.getFormulasTable().getModel() != null) {
                if (e.getSource() == formulasPanel.getFormulasTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateFormula(e);
                    }
                }
            }
        } if (homeView.getVvTable() != null) {
            if (homeView.getVvTable().getModel() != null) {
                if (e.getSource() == homeView.getVvTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateVendor(e);
                    }
                }
            }
        } if (storagePanel.getStorageTable() != null) {
            if (storagePanel.getStorageTable().getModel() != null) {
                if (e.getSource() == storagePanel.getStorageTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateStorageItem(e);
                    }
                }
            }
        }
    }

    public static void main(String[] args) {
        SystemView view = new SystemView();
        new SystemController(view);
    }

}


