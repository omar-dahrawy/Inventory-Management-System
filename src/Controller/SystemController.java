package Controller;

import View.SystemView;

import javax.management.InstanceNotFoundException;
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
                    getStatusDomain();
                    viewBatches();
                    viewOrders();
                    getFormulas();
                    viewVendors();
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
            String itemPrice = view.getHomeView().getGePrice() + ",";
            String itemQuantity = view.getHomeView().getGeQuantity() + ",";
            String dateOfPurchase = "'" + view.getHomeView().getGeDate() + "'";
            String dateOfEntry = "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "',";

            try {
                String sqlQuery = "insert into public.\"General_Expenses\" values (DEFAULT," + itemName + itemPrice + itemQuantity + "null, " + currentUserID + "," + dateOfEntry + dateOfPurchase + ")";
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
        } else if (view.getHomeView().getGePrice() == -13.11) {
            flag = false;
            showMessage("Error adding expense", "Item price must be a number.");
        } else if (view.getHomeView().getGeQuantity() == -13.11) {
            flag = false;
            showMessage("Error adding expense", "Item quantity must be a number.");
        } else if (view.getHomeView().getGePrice() == 0) {
            flag = false;
            showMessage("Error adding expense", "Item price cannot be 0 or empty.");
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
            String materialName = view.getHomeView().getMeComboBox().getSelectedItem().toString();
            String materialPrice = view.getHomeView().getMePrice() + ",";
            String materialQuantity = view.getHomeView().getMeQuantity() + ",";
            String materialUnit = "'" + view.getHomeView().getMaterialUnit() + "',";
            String dateOfEntry = "'" + new SimpleDateFormat("yyyy-MM-dd").format(new Date()) + "',";
            String dateOfPurchase = "'" + view.getHomeView().getMeDate() + "'";

            try {
                String sqlQuery = "insert into public.\"Material_Expenses\" values (DEFAULT," + materialID + "," + materialPrice + materialQuantity + materialUnit + currentUserID + "," + dateOfEntry + dateOfPurchase + ")";
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
        } else if (view.getHomeView().getMePrice() == -13.11) {
            flag = false;
            showMessage("Error adding expense", "Material price must be in numbers only.");
        } else if (view.getHomeView().getMePrice() == 0) {
            flag = false;
            showMessage("Error adding expense", "Material price cannot be 0 or empty.");
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
     *      VIEW EXPENSES
     *
     */

    void viewExpenses() {

        if (checkViewExpenses()) {
            ArrayList<String> queries = generateViewExpensesQuery();
            ArrayList<ResultSet> results = new ArrayList<>();

            try {
                for (String query : queries) {
                    if (!queries.equals("")) {
                        PreparedStatement expensesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                        results.add(expensesQuery.executeQuery());
                    }
                }
                view.getHomeView().showExpenses(results, this);

            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing expenses.");
                throwables.printStackTrace();
            }
        }
    }

    ArrayList<String> generateViewExpensesQuery() {
        LocalDate fromDate = view.getHomeView().getVeFromDate();
        LocalDate toDate = view.getHomeView().getVeToDate();

        boolean generalExpensesSelected = view.getHomeView().getVeGeCheckBox().isSelected();
        boolean materialExpensesSelected = view.getHomeView().getVeMeCheckBox().isSelected();

        String VeDoeSelected = (view.getHomeView().getVeDoeRadioButton().isSelected()) ? ((" WHERE \"Date_of_entry\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_entry\" <= '" + toDate + "'" : "")) : "";
        String VeDopSelected = (view.getHomeView().getVeDopRadioButton().isSelected()) ? ((" WHERE \"Date_of_purchase\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_purchase\" <= '" + toDate + "'" : "")) : "";

        String query1 = "";
        String query2 = "";

        ArrayList<String> queries = new ArrayList<>();

        if (generalExpensesSelected) {
            if (materialExpensesSelected) {
                query1 = "SELECT \"Purchase_ID\", \"Purchased_item\", \"Total_price\", \"Quantity\", \"Unit\", \"Date_of_entry\", \"Date_of_purchase\" FROM public.\"General_Expenses\"" + VeDoeSelected + VeDopSelected;
            } else {
                query1 = "SELECT \"Purchase_ID\", \"Purchased_item\", \"Total_price\", \"Quantity\", \"Date_of_entry\", \"Date_of_purchase\" FROM public.\"General_Expenses\"" + VeDoeSelected + VeDopSelected;
            }
        }
        if (materialExpensesSelected) {
            query2 = "SELECT \"Purchase_ID\", \"Material_ID\", \"Total_price\", \"Quantity\", \"Unit\", \"Date_of_entry\", \"Date_of_purchase\" FROM public.\"Material_Expenses\"" + VeDoeSelected + VeDopSelected;
        }

        if (!query1.equals("")) {
            queries.add(query1);
        }
        if (!query2.equals("")) {
            queries.add(query2);
        }
        return queries;
    }

    boolean checkViewExpenses() {
        boolean flag = true;

        if (!view.getHomeView().getVeGeCheckBox().isSelected() && !view.getHomeView().getVeMeCheckBox().isSelected()) {
            flag = false;
            showMessage("Error viewing expenses","Please select a category to view from.");
        } else if (view.getHomeView().getVeDoeRadioButton().isSelected() || view.getHomeView().getVeDopRadioButton().isSelected()) {
            if (view.getHomeView().getVeFromDate() == null && view.getHomeView().getVeToDate() == null) {
                flag = false;
                showMessage("Error viewing expenses","Please enter viewing dates.");
            } else if (view.getHomeView().getVeFromDate() == null && view.getHomeView().getVeToDate() != null) {
                flag = false;
                showMessage("Error viewing expenses","Please enter a From viewing date.");
            }
        }
        return flag;
    }

    void updateExpense(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = view.getHomeView().getVeTable().getValueAt(row, column).toString();
            String columnName = view.getHomeView().getVeTable().getColumnName(column);
            String id = view.getHomeView().getVeTable().getValueAt(row, 0).toString();
            Object unit = view.getHomeView().getVeTable().getValueAt(row, 4);

            String query = "";

            if (view.getHomeView().getVeTable().getColumnCount() == 6) {
                query = "UPDATE \"General_Expenses\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Purchase_ID\" = " + id;
            } else {
                if (unit == null) {
                    query = "UPDATE \"General_Expenses\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Purchase_ID\" = " + id;
                } else {
                    query = "UPDATE \"Material_Expenses\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Purchase_ID\" = " + id;
                }
            }

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Item updated successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not update item. Please review the new values.");
            }
        }
        viewExpenses();
    }

    void deleteExpense() {

        int[] rows = view.getHomeView().getVeTable().getSelectedRows();

        for (int i: rows) {
            String id = view.getHomeView().getVeTable().getValueAt(i, 0).toString();
            Object unit = view.getHomeView().getVeTable().getValueAt(i, 4);
            int columnCount = view.getHomeView().getVeTable().getColumnCount();

            String query = "";

            if (columnCount == 6) {
                query = "DELETE FROM \"General_Expenses\" WHERE \"Purchase_ID\" = " + id;
            } else {
                if (unit == null) {
                    query = "DELETE FROM \"General_Expenses\" WHERE \"Purchase_ID\" = " + id;
                } else {
                    query = "DELETE FROM \"Material_Expenses\" WHERE \"Purchase_ID\" = " + id;
                }
            }

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Delete successful","Item deleted successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not delete item.");
            }
        }
        viewExpenses();
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
        if (view.getHomeView().getVmTable().getColumnName(e.getColumn()).equals("Available_quantity")) {
            if (checkUpdatePrivilege()) {
                int row = e.getFirstRow();
                int column = e.getColumn();
                String newValue = view.getHomeView().getVmTable().getValueAt(row, column).toString();
                String id = view.getHomeView().getVmTable().getValueAt(row, 0).toString();

                String query = "UPDATE \"Materials\" SET \"Available_quantity\" = '" + newValue + "' WHERE \"Material_ID\" = " + id;
                try {
                    sqlStatement.executeUpdate(query);
                    showMessage("Update successful", "Item updated successfully.");
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                    showMessage("Error performing operation", "Could not update item. Please review the new values.");
                }
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
            String sql = "INSERT INTO \"Materials\" values (DEFAULT, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString( 1, view.getHomeView().getAmNameField());
                query.setDouble( 2, 0.0);
                query.setDate( 3, java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
                query.setBoolean( 4, false);
                query.setString( 5, view.getHomeView().getAmUnitField());
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
     *      CREATE BATCH
     *
     */

    void createBatch() {
        if (checkCreateBatch()) {
            String batchSerial = "'" + view.getHomeView().getCbSerial() + "', '{";
            String orderIDs = view.getHomeView().getCbOrderIDs();
            String formula = view.getHomeView().getCbFormula();

            String query = "INSERT INTO \"Batches\" values (" + batchSerial;
            query += orderIDs;
            query += "'" + formula + "')";

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Operation successful", "New batch created.");
                viewBatches();
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "New batch could not be created.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkCreateBatch() {
        boolean flag = true;

        if (view.getHomeView().getCbSerial().equals("")) {
            flag = false;
            showMessage("Error creating batch", "Batch serial cannot be empty.");
        } else if (view.getHomeView().getCbOrderIDs().equals("")) {
            flag = false;
            showMessage("Error creating batch", "Order IDs cannot be empty.");
        } else if (view.getHomeView().getCbFormula().equals("Select Formula")) {
            showMessage("Error creating batch","Please select a batch formula.");
        }

        return flag;
    }

    /*
     *
     *      VIEW BATCHES
     *
     */

    void viewBatches() {

        if (checkViewBatches()) {
            boolean batchSerialSelected = view.getHomeView().getVbSerialRadioButton().isSelected();
            boolean orderIdIsSelected = view.getHomeView().getVbOrderRadioButton().isSelected();
            boolean formulaIsSelected = view.getHomeView().getVbFormulaRadioButton().isSelected();

            String query = "";

            if (batchSerialSelected) {
                String serial = "'" + view.getHomeView().getVbSerial() + "'";
                query = "SELECT * FROM \"Batches\" WHERE \"Batch_serial\" = " + serial;
            } else if (orderIdIsSelected) {
                String orderID = view.getHomeView().getVbOrderId();
                query = "SELECT * FROM \"Batches\" WHERE \"Order_IDs\" @> ARRAY['" + orderID + "']::varchar[]";
            } else if (formulaIsSelected) {
                String formula = view.getHomeView().getVbFormula();
                query = "SELECT * FROM \"Batches\" WHERE \"Formula_ID\" = '" + formula + "'";
            } else {
                query = "SELECT * FROM \"Batches\"";
            }


            try {
                PreparedStatement samplesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet samples = samplesQuery.executeQuery();
                view.getHomeView().showBatches(samples, this);
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing batches.");
                throwables.printStackTrace();
            }
        }

    }

    boolean checkViewBatches() {
        boolean flag = true;

        if (view.getHomeView().getVbSerialRadioButton().isSelected()) {
            if (view.getHomeView().getVbSerial().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter a batch serial to filter by.");
            }
        } else if (view.getHomeView().getVbOrderRadioButton().isSelected()) {
            if (view.getHomeView().getVbOrderId().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter an Order ID to filter by.");
            }
        } else if (view.getHomeView().getVbFormulaRadioButton().isSelected()) {
            if (view.getHomeView().getVbFormula().equals("")) {
                flag = false;
                showMessage("Error viewing batches", "Please enter a Formula to filter by.");
            }
        }

        return flag;
    }

    void updateBatch(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = view.getHomeView().getVbTable().getValueAt(row, column).toString();
            String columnName = view.getHomeView().getVbTable().getColumnName(column);
            String serial = "'" + view.getHomeView().getVbTable().getValueAt(row, 0).toString() + "'";

            String query = "UPDATE \"Batches\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Batch_serial\" = " + serial;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Batch updated successfully.");
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Could not update batch. Please review the new values.");
                throwables.printStackTrace();
            }
        }
        viewBatches();
    }

    void deleteBatch() {
        if (view.getHomeView().getVbTable() != null) {
            if (view.getHomeView().getVbTable().getModel() != null) {
                int row = view.getHomeView().getVbTable().getSelectedRow();
                String serial = "'" + view.getHomeView().getVbTable().getValueAt(row, 0).toString() + "'";

                String query = "DELETE FROM \"Batches\" WHERE \"Batch_serial\" = " + serial;

                try {
                    sqlStatement.executeUpdate(query);
                    showMessage("Delete successful","Batch deleted successfully.");
                } catch (SQLException throwables) {
                    showMessage("Error performing operation", "Could not delete batch.");
                    throwables.printStackTrace();
                }
                viewBatches();
            }
        }
    }

    /*
     *
     *      CREATE NEW FORMULA
     *
     */

    void createNewFormula(String formulaName, String formulaDescription) {
        String sql = "INSERT INTO \"Formulas\" values (?, ?, ?)";
        try {
            PreparedStatement query = databaseConnection.prepareStatement(sql);
            query.setString(1, formulaName);
            query.setString(2, formulaDescription);
            query.setDouble(3, 0.0);
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
                        formulaDescription += " " + view.getHomeView().getVmTable().getValueAt(checkBoxes.indexOf(checkBox), 5).toString();
                        formulaDescription += " - ";
                    }
                }
            }
            if (count >= 0) {
                if (count == 0) {
                    showMessage("Error creating new formula", "Please select at least one material for the new Formula.");
                } else {
                    formulaDescription = formulaDescription.substring(0, formulaDescription.length() - 3);
                    createNewFormula(formulaName, formulaDescription);
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

    void updateFormulasPrice(String materialID, String materialName, String materialPrice, String materialQuantity) {
        for(int i = 0 ; i < view.getHomeView().getVfTable().getRowCount() ; i++) {
            String formulaDescription = view.getHomeView().getVfTable().getValueAt(i, 1).toString();
            if(formulaDescription.contains(materialName)) {
                String select = "SELECT \"Total_price\" FROM \"Material_Expenses\" WHERE \"Material_ID\" = ? ORDER BY \"Date_of_purchase\" DESC LIMIT 2";
                PreparedStatement query = null;
                try {
                    query = databaseConnection.prepareStatement(select);
                    query.setInt( 1, Integer.parseInt(materialID));
                    ResultSet materialPrices = query.executeQuery();
                    materialPrices.next();
                    double price1 = Double.parseDouble(materialPrices.getString(1));
                    materialPrices.next();
                    double price2 = Double.parseDouble(materialPrices.getString(1));
                    double averagePrice = (price1 + price2)/2;
                    String update = "UPDATE \"Formulas\" SET \"Formula_price\" = " + averagePrice + " WHERE \"Formula_ID\" = '" + view.getHomeView().getVfTable().getValueAt(i, 0).toString() + "'";
                    System.out.println(update);
                    query = databaseConnection.prepareStatement(update);
                    query.executeUpdate();
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        getFormulas();
    }

    void updateFormulasPrice() {
        for(int i = 0 ; i < view.getHomeView().getVfTable().getRowCount() ; i++) {
            String formulaDescription = view.getHomeView().getVfTable().getValueAt(i, 1).toString();
            String [] materials = getFormulaMaterials(formulaDescription);
            int [] materialQuantity = getFormulaQuantities(formulaDescription);
            double formulaAvgPrice = 0;
            PreparedStatement query = null;
            for (int j = 0 ; j < materials.length ; j++) {
                String material = materials[j];
                String selectQuery = "SELECT \"Total_price\", \"Quantity\" FROM \"Material_Expenses\" WHERE \"Material_ID\" = ? ORDER BY \"Date_of_purchase\" DESC LIMIT 2";
                try {
                    query = databaseConnection.prepareStatement(selectQuery);
                    query.setInt( 1, getMaterialID(material));
                    ResultSet materialPrices = query.executeQuery();
                    double price1 = 0;
                    double quantity1 = 0;
                    double price2 = 0;
                    double quantity2 = 0;
                    if (materialPrices.next()) {
                        price1 = Double.parseDouble(materialPrices.getString(1));
                        quantity1 = Double.parseDouble(materialPrices.getString(2));
                    }
                    if (materialPrices.next()) {
                        price2 = Double.parseDouble(materialPrices.getString(1));
                        quantity2 = Double.parseDouble(materialPrices.getString(2));
                    }
                    double averageQuantity = (quantity1 + quantity2)/2;
                    double averagePrice = (price1 + price2)/2;
                    if (averageQuantity == 0 && averagePrice == 0) {
                        formulaAvgPrice += 0;
                    } else {
                        double pricePerKg = averagePrice / averageQuantity;
                        formulaAvgPrice += (pricePerKg * materialQuantity[j]);
                    }
                    showMessage("Operation successful","Formulas prices updated");
                } catch (SQLException throwables) {
                    showErrorMessage("Error performing operation", "Could not update formulas prices", throwables.getLocalizedMessage());
                    throwables.printStackTrace();
                }
            }
            String update = "UPDATE \"Formulas\" SET \"Formula_price\" = " + formulaAvgPrice + " WHERE \"Formula_ID\" = '" + view.getHomeView().getVfTable().getValueAt(i, 0).toString() + "'";
            try {
                query = databaseConnection.prepareStatement(update);
                query.executeUpdate();
            } catch (SQLException exception) {
                exception.printStackTrace();
            }
        }
        getFormulas();
    }

    String [] getFormulaMaterials(String formulaDescription) {
        String [] materials = formulaDescription.split("-");
        materials[0] = materials[0].substring(0, materials[0].indexOf(':'));
        for (int i = 1 ; i < materials.length ; i++) {
            String material = materials[i];
            materials[i] = material.substring(1, material.indexOf(':'));
        }
        return materials;
    }

    int [] getFormulaQuantities(String formulaDescription) {
        String [] materials = formulaDescription.split("-");
        int [] quantities = new int[materials.length];
        for (int i = 0 ; i < materials.length ; i++) {
            String material = materials[i].substring(materials[i].indexOf(':') + 2);;
            for (int j = 1 ; j < material.length() ; j++) {
                if (material.substring(j, j+1).equals(" ")) {
                    material = material.substring(0, j);
                    quantities[i] = Integer.parseInt(material);
                    break;
                }
            }
        }
        return quantities;
    }

    int getMaterialID(String materialName) {
        int row = 0;
        for (; row < view.getHomeView().getVmTable().getRowCount() ; row++) {
            if (view.getHomeView().getVmTable().getValueAt(row, 1).toString().equals(materialName)) {
                break;
            }
        }
        return Integer.parseInt(view.getHomeView().getVmTable().getValueAt(row, 0).toString());
    }

    /*
     *
     *      ADD VENDOR
     *
     */

    void addVendor() {
        if (checkAddVendor()) {
            String vendorName = view.getHomeView().getAvVendorName();
            String products = view.getHomeView().getAvProducts();
            String contactName = view.getHomeView().getAvContactName();
            String contactNumber = view.getHomeView().getAvContactNumber();
            String contactEmail = view.getHomeView().getAvContactEmail();

            String sql = "INSERT INTO \"Vendors\" values (DEFAULT, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, vendorName);
                query.setString(2, products);
                query.setString(3, contactName);
                query.setString(4, contactNumber);
                query.setString(5, contactEmail);
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
        } else if (view.getHomeView().getAvProducts().equals("")) {
            flag = false;
            showMessage("Error adding vendor", "Vendor products cannot be empty.");
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
            boolean productIsSelected = view.getHomeView().getVvProductRadioButton().isSelected();
            boolean contactIsSelected = view.getHomeView().getVvContactRadioButton().isSelected();

            String query = "";

            if (vendorIsSelected) {
                String vendorName = "'" + view.getHomeView().getVvVendor() + "'";
                query = "SELECT * FROM public.\"Vendors\" WHERE \"Vendor_name\" = " + vendorName;
            } else if (productIsSelected) {
                String product = "'" + view.getHomeView().getVvProduct() + "'";
                query = "SELECT * FROM \"Vendors\" WHERE \"Vendor_products\" LIKE '%' || " + product + " || '%'";
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
        } else if (view.getHomeView().getVvProductRadioButton().isSelected()) {
            if (view.getHomeView().getVvProduct().equals("")) {
                flag = false;
                showMessage("Error viewing vendors", "Please enter a product to filter by.");
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
                String id = view.getHomeView().getVbTable().getValueAt(row, 0).toString();

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
        String sql = "INSERT INTO \"Materials\" values (DEFAULT, ?, ?, ?, ?, ?)";

        try (BufferedReader br = new BufferedReader(new FileReader("/Users/omar_aldahrawy/Desktop/Materials.txt"))) {
            PreparedStatement query = databaseConnection.prepareStatement(sql);
            query.setDouble( 2, 0.0);
            query.setDate( 3, java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
            query.setBoolean( 4, false);
            query.setString( 5, "Kg");
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
        } else if (e.getSource() == view.getHomeView().getVeViewButton()) {
            viewExpenses();
        } else if (e.getSource() == view.getHomeView().getAoAddButton()) {
            addOrder();
        } else if (e.getSource() == view.getHomeView().getVoViewButton()) {
            viewOrders();
        } else if (e.getSource() == view.getHomeView().getVeDeleteButton()) {
            deleteExpense();
        } else if (e.getSource() == view.getHomeView().getDeleteOrderButton()) {
            deleteOrder();
        } else if (e.getSource() == view.getHomeView().getVmRefreshButton()) {
            getMaterials();
        } else if (e.getSource() == view.getHomeView().getCbCreateButton()) {
            createBatch();
        } else if (e.getSource() == view.getHomeView().getVbViewButton()) {
            viewBatches();
        } else if (e.getSource() == view.getHomeView().getDeleteBatchButton()) {
            deleteBatch();
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
        } else if (e.getSource() == view.getHomeView().getUpdatePricesButton()) {
            updateFormulasPrice();
        } else if (e.getSource() == view.getHomeView().getAvAddButton()) {
            addVendor();
        } else if (e.getSource() == view.getHomeView().getVvViewButton()) {
            viewVendors();
        } else if (e.getSource() == view.getHomeView().getDeleteVendorButton()) {
            deleteVendor();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (view.getHomeView().getVeTable() != null) {
            if (view.getHomeView().getVeTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVeTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateExpense(e);
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
        } if (view.getHomeView().getVbTable() != null) {
            if (view.getHomeView().getVbTable().getModel() != null) {
                if (e.getSource() == view.getHomeView().getVbTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateBatch(e);
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
        } else if (evt.getSource() == view.getHomeView().getVmTable()) {
            if (evt.getPropertyName().equals("tableCellEditor")) {
                if (!view.getHomeView().getVmTable().getColumnName(view.getHomeView().getVmTable().getSelectedColumn()).equals("Available_quantity")) {
                    showMessage("Selection cannot be edited","Selected column cannot be updated.\nYou can only update a material's available quantity.");
                }
            }
        }
    }

    public static void main(String[] args) {
        SystemView view = new SystemView();
        new SystemController(view);
    }

}


