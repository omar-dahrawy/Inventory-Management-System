package Controller;

import Model.Constants;
import View.SystemView;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.sql.*;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;

public class SystemController implements ActionListener, TableModelListener, PropertyChangeListener {

    private SystemView view;
    private Constants K = new Constants();

    private int currentUserID;

    private Connection databaseConnection;
    private Statement sqlStatement;

    public SystemController(SystemView view) {
        this.view = view;
        this.view.addActionListeners(this);


        //CONNECT TO DATABASE
        while (databaseConnection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                databaseConnection = DriverManager.getConnection("jdbc:postgresql://localhost/Eagles", "postgres", "admin");
                System.out.println("Database server connection successful");
                sqlStatement = databaseConnection.createStatement();
            } catch (Exception e) {
                showMessage("Could not connect to database server", "Connection to database server refused.\nCheck that the server is running and accepting connections. ");
            }
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
                    getStatusDomain();
                    viewProductions();
                    viewOrders();
                    getFormulas();
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
            String itemName = "'" + view.getHomeView().getGeItemName() + "',";
            String itemQuantity = view.getHomeView().getGeQuantity() + ",";
            String dateOfPurchase = "'" + view.getHomeView().getGeDate() + "'";
            String dateOfEntry = "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "',";

            try {
                String sqlQuery = "insert into public.\"General_Expenses\" values (DEFAULT," + itemName + itemQuantity + "null, " + currentUserID + "," + dateOfEntry + dateOfPurchase + ")";
                System.out.println(sqlQuery);
                sqlStatement.executeUpdate(sqlQuery);
                showMessage("Operation successful", "General Expense added.");
                view.getHomeView().clearGeneralExpensesFields();
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "General Expense could not be added.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkGeneralExpenses() {
        boolean flag = true;

        if (view.getHomeView().getGeItemName().equals("")) {
            flag = false;
            showMessage("Error adding expense", "Item name cannot be empty.");
        } else if (view.getHomeView().getGeQuantity() == -13.11) {
            flag = false;
            showMessage("Error adding expense", "Item quantity must be a number.");
        } else if (view.getHomeView().getGeQuantity() == 0) {
            flag = false;
            showMessage("Error adding expense", "Item quantity cannot be 0 or empty.");
        } else if (view.getHomeView().getGeDate() == null) {
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
            String materialID = view.getHomeView().getMaterialID();
            String materialQuantity = view.getHomeView().getMeQuantity() + ",";
            String materialInvoice = "'" + view.getHomeView().getMeInvoice() + "'";
            String vendorID = view.getHomeView().getVendorID();
            String materialUnit = "'" + view.getHomeView().getMaterialUnit() + "',";
            String dateOfEntry = "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "',";
            String dateOfPurchase = "'" + view.getHomeView().getMeDate() + "',";

            try {
                String sqlQuery = "insert into public.\"Material_Expenses\" values (DEFAULT," + materialID + "," + materialQuantity + materialUnit + currentUserID + "," + dateOfEntry + dateOfPurchase + vendorID + "," + materialInvoice + ")";
                sqlStatement.executeUpdate(sqlQuery);
                showMessage("Operation successful", "General Expense added.");
                String updateQuery = "UPDATE \"Materials\" SET \"Available_quantity\" = \"Available_quantity\" + " + view.getHomeView().getMeQuantity() + " WHERE \"Material_ID\" = " + view.getHomeView().getMaterialID();
                sqlStatement.executeUpdate(updateQuery);
                getMaterials();
                view.getHomeView().clearMaterialExpensesFields();
                //updateFormulasPrice(materialID,materialName, materialPrice, materialQuantity);
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Material Expense could not be added.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkMaterialExpenses() {
        boolean flag = true;

        if (view.getHomeView().getMaterialID().equals("")) {
            flag = false;
            showMessage("Error adding expense", "Please select a Material.");
        } else if (view.getHomeView().getMeQuantity() == -13.11) {
            flag = false;
            showMessage("Error adding expense", "Material quantity must be in numbers only.");
        } else if (view.getHomeView().getMeQuantity() == 0) {
            flag = false;
            showMessage("Error adding expense", "Material quantity cannot be 0 or empty.");
        } else if (view.getHomeView().getMeDate() == null) {
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

        LocalDate fromDate = view.getHomeView().getVgeFromDate();
        LocalDate toDate = view.getHomeView().getVgeToDate();

        String VeDoeSelected = (view.getHomeView().getVgeDoeRadioButton().isSelected()) ? ((" WHERE \"Date_of_entry\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_entry\" <= '" + toDate + "'" : "")) : "";
        String VeDopSelected = (view.getHomeView().getVgeDopRadioButton().isSelected()) ? ((" WHERE \"Date_of_purchase\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_purchase\" <= '" + toDate + "'" : "")) : "";

        String query = "SELECT * FROM public.\"General_Expenses\"" + VeDoeSelected + VeDopSelected;

        if (checkViewGeneralExpenses()) {
            try {
                PreparedStatement expensesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet results = expensesQuery.executeQuery();
                view.getHomeView().showGeneralExpenses(results, this);

            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing expenses.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewGeneralExpenses() {
        boolean flag = true;

        if (view.getHomeView().getVgeDoeRadioButton().isSelected() || view.getHomeView().getVgeDopRadioButton().isSelected()) {
            if (view.getHomeView().getVgeFromDate() == null && view.getHomeView().getVgeToDate() == null) {
                flag = false;
                showMessage("Error viewing expenses","Please enter viewing dates.");
            } else if (view.getHomeView().getVgeFromDate() == null && view.getHomeView().getVgeToDate() != null) {
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
            String newValue = view.getHomeView().getVgeTable().getValueAt(row, column).toString();
            String columnName = view.getHomeView().getVgeTable().getColumnName(column);
            String id = view.getHomeView().getVgeTable().getValueAt(row, 0).toString();

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

        int[] rows = view.getHomeView().getVgeTable().getSelectedRows();

        for (int i: rows) {
            String id = view.getHomeView().getVgeTable().getValueAt(i, 0).toString();
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

        LocalDate fromDate = view.getHomeView().getVmeFromDate();
        LocalDate toDate = view.getHomeView().getVmeToDate();

        String VeDoeSelected = (view.getHomeView().getVmeDoeRadioButton().isSelected()) ? ((" WHERE \"Date_of_entry\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_entry\" <= '" + toDate + "'" : "")) : "";
        String VeDopSelected = (view.getHomeView().getVmeDopRadioButton().isSelected()) ? ((" WHERE \"Date_of_purchase\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_purchase\" <= '" + toDate + "'" : "")) : "";

        String query = "SELECT \"Purchase_ID\", \"Material_ID\", \"Quantity\", \"Unit\", \"Vendor\", \"Invoice_number\", \"User_ID\", \"Date_of_entry\", \"Date_of_purchase\" FROM public.\"Material_Expenses\"" + VeDoeSelected + VeDopSelected;

        if (checkViewMaterialExpenses()) {
            try {
                PreparedStatement expensesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet results = expensesQuery.executeQuery();
                view.getHomeView().showMaterialExpenses(results, this);

            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing expenses.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewMaterialExpenses() {
        boolean flag = true;

        if (view.getHomeView().getVgeDoeRadioButton().isSelected() || view.getHomeView().getVgeDopRadioButton().isSelected()) {
            if (view.getHomeView().getVgeFromDate() == null && view.getHomeView().getVgeToDate() == null) {
                flag = false;
                showMessage("Error viewing expenses","Please enter viewing dates.");
            } else if (view.getHomeView().getVgeFromDate() == null && view.getHomeView().getVgeToDate() != null) {
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
            String newValue = view.getHomeView().getVmeTable().getValueAt(row, column).toString();
            String columnName = view.getHomeView().getVmeTable().getColumnName(column);
            String id = view.getHomeView().getVmeTable().getValueAt(row, 0).toString();

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
        int[] rows = view.getHomeView().getVmeTable().getSelectedRows();

        for (int i: rows) {
            String id = view.getHomeView().getVmeTable().getValueAt(i, 0).toString();
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
            String customer = "'" + view.getHomeView().getAoCustomer() + "',";
            String orderDetails = "'" + view.getHomeView().getAoDetails() + "',";
            String price = view.getHomeView().getAoPrice() + ",";
            String status = 1 + ",";
            String DOP = "'" + view.getHomeView().getAoDop() + "',";
            String DOD = "'" + view.getHomeView().getAoDod() + "',";
            String DOE = "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "'";

            try {
                String sqlQuery = "insert into public.\"Orders\" values (DEFAULT," + customer + orderDetails + price +
                        status + currentUserID + "," + DOP + DOD + DOE + ")";
                sqlStatement.executeUpdate(sqlQuery);
                view.getHomeView().clearAddOrderFields();
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

        if (view.getHomeView().getAoCustomer().equals("")) {
            flag = false;
            showMessage("Error adding order", "Order customer cannot be empty.");
        } else if (view.getHomeView().getAoBatchSerial().equals("")) {
            flag = false;
            showMessage("Error adding order", "Batch serial cannot be empty.");
        } else if (view.getHomeView().getAoPrice() == 0) {
            flag = false;
            showMessage("Error adding order", "Order price cannot be 0 or empty.");
        } else if (view.getHomeView().getAoPrice() == -13.11) {
            flag = false;
            showMessage("Error adding order", "Order price must be a number.");
        } else if (view.getHomeView().getAoDetails().equals("")) {
            flag = false;
            showMessage("Error adding order", "Order details cannot be empty.");
        } else if (view.getHomeView().getAoDod() == null) {
            flag = false;
            showMessage("Error adding order", "Date of delivery cannot be empty.");
        } else if (view.getHomeView().getAoDop() == null) {
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
            Boolean customerSelected = view.getHomeView().getVoCustomerRadioButton().isSelected();
            Boolean DateSelected = view.getHomeView().getVoDateRadioButton().isSelected();
            Boolean statusSelected = view.getHomeView().getVoStatusRadioButton().isSelected();
            Boolean batchSerialSelected = view.getHomeView().getVoSerialRadioButton().isSelected();

            String query = "";

            if (customerSelected) {
                query = "SELECT * FROM public.\"Orders\" WHERE \"Customer\" = '" + view.getHomeView().getVoCustomer() + "'";
            } else if (DateSelected) {
                String dateType = (view.getHomeView().getVoDateType().equals("Date of entry")) ? "Date_of_entry" : (view.getHomeView().getVoDateType().equals("Date of production") ? "Date_of_production" : "Date_of_delivery");
                if (view.getHomeView().getVoToDate() != null) {
                    query = "SELECT * FROM public.\"Orders\" WHERE \"" + dateType + "\" >= '" + view.getHomeView().getVoFromDate() + "' AND \"" + dateType + "\" <= '" + view.getHomeView().getVoToDate() + "'";
                } else {
                    query = "SELECT * FROM public.\"Orders\" WHERE \"" + dateType + "\" >= '" + view.getHomeView().getVoFromDate() + "'";
                }
            } else if (statusSelected) {
                query = "SELECT * FROM public.\"Orders\" WHERE \"Status\" = " + view.getHomeView().getVoStatus();
            } else if (batchSerialSelected) {
                query = "SELECT * FROM public.\"Orders\" WHERE \"Batch_serial\" = '" + view.getHomeView().getVoSerial() + "'";
            } else {
                query = "SELECT * FROM public.\"Orders\"";
            }

            try {
                PreparedStatement ordersQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet orders = ordersQuery.executeQuery();
                view.getHomeView().showOrders(orders, this);
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing orders.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewOrders() {
        boolean flag = true;

        if (view.getHomeView().getVoCustomerRadioButton().isSelected()) {
            if (view.getHomeView().getVoCustomer().equals("")) {
                flag = false;
                showMessage("Error viewing orders", "Please enter a customer to filter by.");
            }
        } else if (view.getHomeView().getVoStatusRadioButton().isSelected()) {
            if (view.getHomeView().getVoStatus() == 0) {
                flag = false;
                showMessage("Error viewing orders", "Please select a status to filter by.");
            }
        } else if (view.getHomeView().getVoSerialRadioButton().isSelected()) {
            if (view.getHomeView().getVoSerial().equals("")) {
                flag = false;
                showMessage("Error viewing orders", "Please enter a batch serial to filter by.");
            }
        } else if (view.getHomeView().getVoDateRadioButton().isSelected()) {
            if (view.getHomeView().getVoDateType().equals("Select date type")) {
                flag = false;
                showMessage("Error viewing orders", "Please select a date type to filter by.");
            } else if (view.getHomeView().getVoFromDate() == null) {
                flag = false;
                showMessage("Error viewing orders", "Please enter a From date to filter by.");
            }
        }

        return flag;
    }

    void getStatusDomain() {
        String query = "SELECT \"Status\" FROM public.\"Status_domain\"";

        try {
            ResultSet status = sqlStatement.executeQuery(query);
            view.getHomeView().getStatus(status);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void updateOrder(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = view.getHomeView().getVoTable().getValueAt(row, column).toString();
            String columnName = view.getHomeView().getVoTable().getColumnName(column);
            String id = view.getHomeView().getVoTable().getValueAt(row, 0).toString();

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
        int row = view.getHomeView().getVoTable().getSelectedRow();
        String id = view.getHomeView().getVoTable().getValueAt(row, 0).toString();

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
            view.getHomeView().getMaterials(materials, this);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void updateMaterial(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String columnName = view.getHomeView().getVmTable().getColumnName(column);
            String newValue = view.getHomeView().getVmTable().getValueAt(row, column).toString();
            String id = view.getHomeView().getVmTable().getValueAt(row, 0).toString();

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
                query.setString( 1, view.getHomeView().getAmNameField());
                query.setDouble( 2, 0.0);
                query.setDate( 3, java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
                query.setString( 4, view.getHomeView().getAmUnitField());
                query.executeUpdate();
                query.close();
                showMessage("Operation successful","New material added.");
                view.getHomeView().clearAddMaterialFields();
                getMaterials();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation","Material could not be added.");
            }
        }
    }

    boolean checkAddMaterial() {
        boolean flag = true;

        if (view.getHomeView().getAmNameField().equals("")) {
            flag = false;
            showMessage("Error adding material","Material name cannot be empty.");
        } else if (view.getHomeView().getAmUnitField().equals("")) {
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
        if (checkAddProduction()) {
            String formula = view.getHomeView().getApView().getApFormula();
            Double quantity = view.getHomeView().getApView().getApQuantity();

            String sql = "INSERT INTO \"Production\" values (DEFAULT, ?, ?, ?, ?)";

            try {
                Array array = databaseConnection.createArrayOf("VARCHAR", view.getHomeView().getApView().getApOrders());
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, formula);
                query.setDouble(2,quantity);
                query.setArray(3, array);
                query.setString(4, K.status_1);
                query.executeUpdate();
                query.close();
                showMessage("Operation successful", "New production added.");
                view.getHomeView().getApView().dispatchEvent(new WindowEvent(view.getHomeView().getApView(), WindowEvent.WINDOW_CLOSING));
                viewProductions();
            } catch (SQLException throwables) {
                showErrorMessage("Error performing operation", "New production could not be added.", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }
    }

    boolean checkAddProduction() {

        if (view.getHomeView().getApView().getApQuantity() == -13.11) {
            showMessage("Error adding production", "Production quantity must be numbers.");
            return false;
        } else if (view.getHomeView().getApView().getApQuantity() == 0) {
            showMessage("Error adding production", "Production quantity cannot be 0 or empty.");
            return false;
        } else if (view.getHomeView().getApView().getApOrders().length == 0) {
            showMessage("Error adding production", "Order IDs cannot be empty.");
            return false;
        } else if (view.getHomeView().getApView().getApFormula().equals("Select Formula")) {
            showMessage("Error adding production","Please choose a batch formula.");
            return false;
        } else {
            return checkAddProductionFields();
        }
    }

    boolean checkAddProductionFields() {
        int count = 0;

        for (JPanel panel: view.getHomeView().getApView().getTanksPanels()) {
            count++;
            Double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            Double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All tanks fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All tanks fields must contain numbers");
                return false;
            } else {

            }
        }
        for (JPanel panel: view.getHomeView().getApView().getDrumsPanels()) {
            count++;
            Double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            Double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All drums fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All drums fields must contain numbers");
                return false;
            }
        }
        for (JPanel panel: view.getHomeView().getApView().getPailsPanels()) {
            count++;
            Double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            Double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All pails fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All pails fields must contain numbers");
                return false;
            }
        }
        for (JPanel panel: view.getHomeView().getApView().getCartonsPanels()) {
            count++;
            Double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            Double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
            if (quantity == 0 || weight == 0) {
                showMessage("Error adding production","All cartons fields must not be 0 or empty");
                return false;
            } else if (quantity == -13.11 || weight == -13.11) {
                showMessage("Error adding production","All cartons fields must contain numbers");
                return false;
            }
        }
        for (JPanel panel: view.getHomeView().getApView().getGallonsPanels()) {
            count++;
            Double quantity = parseDouble(((JTextField)panel.getComponent(1)).getText());
            Double weight = parseDouble(((JTextField)panel.getComponent(2)).getText());
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
            boolean batchSerialSelected = view.getHomeView().getVpSerialRadioButton().isSelected();
            boolean orderIdIsSelected = view.getHomeView().getVpOrderRadioButton().isSelected();
            boolean formulaIsSelected = view.getHomeView().getVpFormulaRadioButton().isSelected();
            boolean statusIsSelected = view.getHomeView().getVpStatusRadioButton().isSelected();

            String query = "";

            if (batchSerialSelected) {
                String serial = "'" + view.getHomeView().getVpSerial() + "'";
                query = "SELECT * FROM \"Production\" WHERE \"Batch_serial\" = " + serial;
            } else if (orderIdIsSelected) {
                String orderID = view.getHomeView().getVpOrderId();
                query = "SELECT * FROM \"Production\" WHERE \"Orders_IDs\" @> ARRAY['" + orderID + "']::varchar[]";
            } else if (formulaIsSelected) {
                String formula = view.getHomeView().getVpFormula();
                query = "SELECT * FROM \"Production\" WHERE \"Batch_formula\" = '" + formula + "'";
            } else if (statusIsSelected) {
                String status = view.getHomeView().getVpStatus();
                query = "SELECT * FROM \"Production\" WHERE \"Production_status\" = '" + status + "'";
            } else {
                query = "SELECT * FROM \"Production\"";
            }

            try {
                PreparedStatement samplesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet productions = samplesQuery.executeQuery();
                view.getHomeView().showBatches(productions, this);
            } catch (SQLException throwables) {
                showErrorMessage("Error performing operation", "Error viewing productions.", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }

    }

    boolean checkViewProductions() {
        boolean flag = true;

        if (view.getHomeView().getVpSerialRadioButton().isSelected()) {
            if (view.getHomeView().getVpSerial().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter a batch serial to filter by.");
            }
        } else if (view.getHomeView().getVpOrderRadioButton().isSelected()) {
            if (view.getHomeView().getVpOrderId().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter an Order ID to filter by.");
            }
        } else if (view.getHomeView().getVpFormulaRadioButton().isSelected()) {
            if (view.getHomeView().getVpFormula().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter a Formula to filter by.");
            }
        } else if (view.getHomeView().getVpStatusRadioButton().isSelected()) {
            if (view.getHomeView().getVpStatus().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter a Status to filter by.");
            }
        }
        return flag;
    }

    void updateProduction(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = view.getHomeView().getVpTable().getValueAt(row, column).toString();
            String columnName = view.getHomeView().getVpTable().getColumnName(column);
            String serial = "'" + view.getHomeView().getVpTable().getValueAt(row, 0).toString() + "'";

            String query = "UPDATE \"Production\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Batch_serial\" = " + serial;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Batch updated successfully.");
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Could not update batch. Please review the new values.");
                throwables.printStackTrace();
            }
        }
        viewProductions();
    }

    void deleteProduction() {
        if (view.getHomeView().getVpTable() != null) {
            if (view.getHomeView().getVpTable().getModel() != null) {
                int row = view.getHomeView().getVpTable().getSelectedRow();
                String serial = "'" + view.getHomeView().getVpTable().getValueAt(row, 0).toString() + "'";

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
            view.getHomeView().getCfView().dispatchEvent(new WindowEvent(view.getHomeView().getCfView(), WindowEvent.WINDOW_CLOSING));
            getFormulas();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            showErrorMessage("Operation unsuccessful","Could not create new formula.", throwables.getLocalizedMessage());
        }
    }

    void checkCreateFormula() {
        String formulaName = view.getHomeView().getCfView().getFormulaName();
        String formulaDescription = "";

        ArrayList<JCheckBox> checkBoxes = view.getHomeView().getCfView().getCheckBoxes();
        ArrayList<JTextField> textFields = view.getHomeView().getCfView().getTextFields();

        if (formulaName.equals("")) {
            showMessage("Error creating new formula","Formula name cannot be empty.");
        } else {
            int count = 0;
            int formulaQuantity = 0;
            for (int i = 0 ; i < checkBoxes.size(); i++) {
                JCheckBox checkBox = checkBoxes.get(i);
                if (checkBox.isSelected()) {
                    count++;
                    if (textFields.get(checkBoxes.indexOf(checkBox)).getText().equals("")) {
                        showMessage("Error creating new formula", "Please add a quantity for the material " + checkBox.getText());
                        count = -1;
                        break;
                    } else {
                        formulaDescription += checkBox.getText();
                        formulaDescription += ": ";
                        formulaDescription += textFields.get(checkBoxes.indexOf(checkBox)).getText();
                        formulaQuantity += Double.parseDouble(textFields.get(checkBoxes.indexOf(checkBox)).getText());
                        formulaDescription += " " + view.getHomeView().getVmTable().getValueAt(checkBoxes.indexOf(checkBox), 4).toString();
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
            view.getHomeView().showFormulas(formulas, this);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            showMessage("Error performing operation", "Error viewing formulas.");
        }
    }

    void updateFormula(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = view.getHomeView().getVfTable().getValueAt(row, column).toString();
            String columnName = view.getHomeView().getVfTable().getColumnName(column);
            String id = "'" + view.getHomeView().getVfTable().getValueAt(row, 0).toString() + "'";

            String query = "UPDATE \"Formulas\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Formula_ID\" = " + id;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Formula updated successfully.");
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Could not update batch. Please review the new values.");
                throwables.printStackTrace();
            }
        }
        getFormulas();
    }

    void deleteFormula() {
        if (view.getHomeView().getVfTable() != null) {
            if (view.getHomeView().getVfTable().getModel() != null) {
                int row = view.getHomeView().getVfTable().getSelectedRow();
                String id = "'" + view.getHomeView().getVfTable().getValueAt(row, 0).toString() + "'";

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
            String vendorName = view.getHomeView().getAvVendorName();
            String contactName = view.getHomeView().getAvContactName();
            String contactNumber = view.getHomeView().getAvContactNumber();
            String contactEmail = view.getHomeView().getAvContactEmail();

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
                view.getHomeView().clearAddVendorFields();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Operation unsuccessful", "Could not add new vendor.");
            }
        }
    }

    boolean checkAddVendor() {
        boolean flag = true;

        if (view.getHomeView().getAvVendorName().equals("")) {
            flag = false;
            showMessage("Error adding vendor", "Vendor name cannot be empty.");
        } else if (view.getHomeView().getAvContactName().equals("")) {
            flag = false;
            showMessage("Error adding vendor","Contact name cannot be empty.");
        } else if (view.getHomeView().getAvContactNumber().equals("")) {
            flag = false;
            showMessage("Error adding vendor","Contact number cannot be empty.");
        } else if (view.getHomeView().getAvContactEmail().equals("")) {
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
            boolean vendorIsSelected = view.getHomeView().getVvVendorRadioButton().isSelected();
            boolean contactIsSelected = view.getHomeView().getVvContactRadioButton().isSelected();

            String query = "";

            if (vendorIsSelected) {
                String vendorName = "'" + view.getHomeView().getVvVendor() + "'";
                query = "SELECT * FROM public.\"Vendors\" WHERE \"Vendor_name\" = " + vendorName;
            } else if (contactIsSelected) {
                String contactName = "'" + view.getHomeView().getVvContact() + "'";
                query = "SELECT * FROM public.\"Vendors\" WHERE \"Contact_name\" = " + contactName;
            } else {
                query = "SELECT * FROM public.\"Vendors\"";
            }

            try {
                PreparedStatement vendorsQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet vendors = vendorsQuery.executeQuery();
                view.getHomeView().showVendors(vendors, this);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Error viewing vendors.");
            }
        }
    }

    boolean checkViewVendors() {
        boolean flag = true;

        if (view.getHomeView().getVvVendorRadioButton().isSelected()) {
            if (view.getHomeView().getVvVendor().equals("")) {
                System.out.println("VvV");
                flag = false;
                showMessage("Error viewing vendors", "Please enter a vendor name to filter by.");
            }
        } else if (view.getHomeView().getVvContactRadioButton().isSelected()) {
            if (view.getHomeView().getVvContact().equals("")) {
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
            String newValue = view.getHomeView().getVvTable().getValueAt(row, column).toString();
            String columnName = view.getHomeView().getVvTable().getColumnName(column);
            String id = view.getHomeView().getVvTable().getValueAt(row, 0).toString();

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
        if (view.getHomeView().getVvTable() != null) {
            if (view.getHomeView().getVvTable().getModel() != null) {
                int row = view.getHomeView().getVvTable().getSelectedRow();
                String id = view.getHomeView().getVpTable().getValueAt(row, 0).toString();

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
            String line = "";
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
            login();
        } else if (e.getSource() == view.getHomeView().getMeAddButton()) {
            addGeneralExpense();
        } else if (e.getSource() == view.getHomeView().getGeAddButton()) {
            addMaterialExpense();
        } else if (e.getSource() == view.getHomeView().getVgeViewButton()) {
            viewGeneralExpenses();
        } else if (e.getSource() == view.getHomeView().getVmeViewButton()) {
            viewMaterialExpenses();
        } else if (e.getSource() == view.getHomeView().getAoAddButton()) {
            addOrder();
        } else if (e.getSource() == view.getHomeView().getVoViewButton()) {
            viewOrders();
        } else if (e.getSource() == view.getHomeView().getVgeDeleteButton()) {
            deleteGeneralExpense();
        } else if (e.getSource() == view.getHomeView().getVmeDeleteButton()) {
            deleteMaterialExpense();
        } else if (e.getSource() == view.getHomeView().getDeleteOrderButton()) {
            deleteOrder();
        } else if (e.getSource() == view.getHomeView().getVmRefreshButton()) {
            getMaterials();
        } else if (e.getSource() == view.getHomeView().getAddNewProductionButton()) {
            view.getHomeView().showAddProductionView(this);
        } else if (e.getSource() == view.getHomeView().getVpViewButton()) {
            viewProductions();
        } else if (e.getSource() == view.getHomeView().getDeleteProductionButton()) {
            deleteProduction();
        } else if (e.getSource() == view.getHomeView().getAmAddButton()) {
            addMaterial();
        } else if (e.getSource() == view.getHomeView().getCreateNewFormulaButton()) {
            view.getHomeView().showCreateFormulaView(this);
        } else if (e.getActionCommand().equals("Create Formula")) {
            checkCreateFormula();
        } else if (e.getSource() == view.getHomeView().getVfRefreshButton()) {
            getFormulas();
        } else if (e.getSource() == view.getHomeView().getDeleteFormulaButton()) {
            deleteFormula();
        } else if (e.getSource() == view.getHomeView().getAvAddButton()) {
            addVendor();
        } else if (e.getSource() == view.getHomeView().getVvViewButton()) {
            viewVendors();
        } else if (e.getSource() == view.getHomeView().getDeleteVendorButton()) {
            deleteVendor();
        } else if (e.getActionCommand().equals("Add Production")) {
            addProduction();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (view.getHomeView().getVgeTable() != null) {
            if (view.getHomeView().getVgeTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVgeTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateGeneralExpense(e);
                    }
                }
            }
        } if (view.getHomeView().getVmeTable() != null) {
            if (view.getHomeView().getVmeTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVmeTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateMaterialExpense(e);
                    }
                }
            }
        } if (view.getHomeView().getVoTable() != null) {
            if (view.getHomeView().getVoTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVoTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateOrder(e);
                    }
                }
            }
        } if (view.getHomeView().getVmTable() != null) {
            if (view.getHomeView().getVmTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVmTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateMaterial(e);
                    }
                }
            }
        } if (view.getHomeView().getVpTable() != null) {
            if (view.getHomeView().getVpTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVpTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateProduction(e);
                    }
                }
            }
        } if (view.getHomeView().getVfTable() != null) {
            if (view.getHomeView().getVfTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVfTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateFormula(e);
                    }
                }
            }
        } if (view.getHomeView().getVvTable() != null) {
            if (view.getHomeView().getVvTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVvTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateVendor(e);
                    }
                }
            }
        }
    }

    @Override
    public void propertyChange(PropertyChangeEvent evt) {
        if (evt.getSource() == view.getHomeView().getVoTable()) {
            if (evt.getPropertyName().equals("tableCellEditor")) {
                if (view.getHomeView().getVoTable().getColumnName(view.getHomeView().getVoTable().getSelectedColumn()).equals("Status")) {
                    showMessage("Editing order status","To edit an order's status, enter the number corresponding\nto the status you want to change to:\n\n" +
                            "1 : Received Order\n2 : In Production\n3 : Preparing for Delivery\n4 : Delivered");
                }
            }
        }
    }

    public static void main(String[] args) {
        SystemView view = new SystemView();
        new SystemController(view);
    }

}


