package Controller;

import Model.Constants;
import View.AddProductionView;
import View.CreateFormulaView;
import View.HomeView;
import View.MainPanels.*;
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
    private final BatchesPanel batchesPanel;
    private final StoragePanel storagePanel;
    private final VendorsPanel vendorsPanel;
    private final FormulasPanel formulasPanel;
    private final ProductionPanel productionPanel;
    private final RawMaterialsPanel rawMaterialsPanel;
    private final GeneralPurchasesPanel generalPurchasesPanel;
    private final MaterialPurchasesPanel materialPurchasesPanel;

    private final Constants K = new Constants();

    private int currentUserID;
    private String currentUserType;

    private Connection databaseConnection;
    private Statement sqlStatement;

    public SystemController(SystemView view) {
        this.view = view;
        this.homeView = view.getHomeView();
        this.ordersPanel = homeView.getOrdersPanel();
        this.batchesPanel = homeView.getBatchesPanel();
        this.storagePanel = homeView.getStoragePanel();
        this.vendorsPanel = homeView.getVendorsPanel();
        this.formulasPanel = homeView.getFormulasPanel();
        this.productionPanel = homeView.getProductionPanel();
        this.rawMaterialsPanel = homeView.getRawMaterialsPanel();
        this.generalPurchasesPanel = homeView.getGeneralPurchasesPanel();
        this.materialPurchasesPanel = homeView.getMaterialPurchasesPanel();
        this.view.addActionListeners(this);
    }

    void connectToServer() {

        //CONNECT TO DATABASE
        if (databaseConnection == null) {
            try {
                Class.forName("org.postgresql.Driver");
                databaseConnection = DriverManager.getConnection("jdbc:postgresql://localhost/Eagles", "postgres", "admin");
                //databaseConnection = DriverManager.getConnection("jdbc:postgresql://192.168.1.35/Eagles", "postgres", "admin");
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
                    currentUserType = users.getString(5);
                    users.close();
                    getMaterials();
                    viewVendors();
                    viewGeneralExpenses();
                    viewMaterialExpenses();
                    viewProductions();
                    viewOrders();
                    viewStorage();
                    getFormulas();
                    calculateFormulaPrices();
                    viewBatches();
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


    // ADD GENERAL EXPENSE


    void addGeneralExpense() {
        if (checkGeneralExpenses()) {
            String itemName = generalPurchasesPanel.getAddItem();
            double itemQuantity = generalPurchasesPanel.getAddQuantity();
            java.sql.Date DOP = java.sql.Date.valueOf(generalPurchasesPanel.getAddDop());
            java.sql.Date DOE = java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            String sql = "INSERT INTO \"General_Expenses\" VALUES (DEFAULT, ?, ?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, itemName);
                query.setDouble(2, itemQuantity);
                query.setInt(3, currentUserID);
                query.setDate(4, DOP);
                query.setDate(5, DOE);
                query.execute();
                query.close();
                showMessage("Operation successful", "General Expense added.");
                generalPurchasesPanel.clearAddFields();
                viewGeneralExpenses();
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "General Expense could not be added.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkGeneralExpenses() {
        if (generalPurchasesPanel.getAddItem().equals("")) {
            showMessage("Error adding expense", "Item name cannot be empty.");
            return false;
        } else if (generalPurchasesPanel.getAddQuantity() == -13.11) {
            showMessage("Error adding expense", "Item quantity must be a number.");
            return false;
        } else if (generalPurchasesPanel.getAddQuantity() == 0) {
            showMessage("Error adding expense", "Item quantity cannot be 0 or empty.");
            return false;
        } else if (generalPurchasesPanel.getAddDop() == null) {
            showMessage("Error adding expense", "Date of purchase cannot be empty.");
            return false;
        }
        return true;
    }


    //  ADD MATERIAL EXPENSE


    void addMaterialExpense() {
        if (checkMaterialExpenses()) {
            String materialName = materialPurchasesPanel.getAddMaterial();
            double materialQuantity = materialPurchasesPanel.getAddQuantity();
            int vendorID = materialPurchasesPanel.getAddVendor();
            String materialInvoice = materialPurchasesPanel.getAddInvoice();
            java.sql.Date DOP = java.sql.Date.valueOf(materialPurchasesPanel.getAddDop());
            java.sql.Date DOE = java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));

            String sql = "INSERT INTO \"Material_Expenses\" VALUES (DEFAULT, ?, ?, ?, ?, ?, ?, ?, ?)";

            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, materialName);
                query.setDouble(2, materialQuantity);
                query.setString(3, "Kg");
                query.setInt(4, currentUserID);
                query.setDate(5, DOE);
                query.setDate(6, DOP);
                query.setInt(7, vendorID);
                query.setString(8, materialInvoice);
                query.executeUpdate();
                String updateQuery = "UPDATE \"Materials\" SET \"Available_quantity\" = " + materialQuantity + " WHERE \"Material_name\" = '" + materialName + "'";
                sqlStatement.executeUpdate(updateQuery);
                showMessage("Operation successful", "Material Expense added.");
                getMaterials();
                materialPurchasesPanel.clearAddFields();
                viewMaterialExpenses();
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Material Expense could not be added.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkMaterialExpenses() {
        if (materialPurchasesPanel.getAddMaterial().equals("")) {
            showMessage("Error adding expense", "Please select a Material.");
            return false;
        } else if (materialPurchasesPanel.getAddQuantity() == -13.11) {
            showMessage("Error adding expense", "Material quantity must be in numbers only.");
            return false;
        } else if (materialPurchasesPanel.getAddQuantity() == 0) {
            showMessage("Error adding expense", "Material quantity cannot be 0 or empty.");
            return false;
        } else if (materialPurchasesPanel.getAddDop() == null) {
            showMessage("Error adding expense.", "Date of purchase cannot be empty.");
            return false;
        }
        return true;
    }


    //  VIEW GENERAL EXPENSES


    void viewGeneralExpenses() {

        LocalDate fromDate = generalPurchasesPanel.getFilterFromDate();
        LocalDate toDate = generalPurchasesPanel.getFilterToDate();

        String VeDoeSelected = (generalPurchasesPanel.getFilterDoeSelected()) ? ((" WHERE \"Date_of_entry\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_entry\" <= '" + toDate + "'" : "")) : "";
        String VeDopSelected = (generalPurchasesPanel.getFilterDopSelected()) ? ((" WHERE \"Date_of_purchase\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_purchase\" <= '" + toDate + "'" : "")) : "";

        String query = "SELECT * FROM public.\"General_Expenses\"" + VeDoeSelected + VeDopSelected;

        if (checkViewGeneralExpenses()) {
            try {
                PreparedStatement expensesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet results = expensesQuery.executeQuery();
                generalPurchasesPanel.showGeneralPurchases(results, this);

            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing expenses.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewGeneralExpenses() {
        if (generalPurchasesPanel.getFilterDoeSelected() || generalPurchasesPanel.getFilterDopSelected()) {
            if (generalPurchasesPanel.getFilterFromDate() == null && generalPurchasesPanel.getFilterToDate() == null) {
                showMessage("Error viewing expenses","Please enter viewing dates.");
                return false;
            } else if (generalPurchasesPanel.getFilterFromDate() == null && generalPurchasesPanel.getFilterToDate() != null) {
                showMessage("Error viewing expenses","Please enter a From viewing date.");
                return false;
            }
        }
        return true;
    }

    void updateGeneralExpense(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            JTable generalPurchasesTable = generalPurchasesPanel.getPurchasesTable();
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = generalPurchasesTable.getValueAt(row, column).toString();
            String columnName = generalPurchasesTable.getColumnName(column);
            String id = generalPurchasesTable.getValueAt(row, 0).toString();

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
        JTable generalPurchasesTable = generalPurchasesPanel.getPurchasesTable();

        int[] rows = generalPurchasesTable.getSelectedRows();

        for (int i: rows) {
            String id = generalPurchasesTable.getValueAt(i, 0).toString();
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


    //  VIEW MATERIAL EXPENSES


    void viewMaterialExpenses() {
        LocalDate fromDate = materialPurchasesPanel.getFilterFromDate();
        LocalDate toDate = materialPurchasesPanel.getFilterToDate();

        String VeDoeSelected = (materialPurchasesPanel.getFilterDoeSelected()) ? ((" WHERE \"Date_of_entry\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_entry\" <= '" + toDate + "'" : "")) : "";
        String VeDopSelected = (materialPurchasesPanel.getFilterDopSelected()) ? ((" WHERE \"Date_of_purchase\" >= '" + fromDate + "'" ) + (toDate != null ? " AND \"Date_of_purchase\" <= '" + toDate + "'" : "")) : "";

        String query = "SELECT * FROM \"Material_Expenses\"" + VeDoeSelected + VeDopSelected;

        if (checkViewMaterialExpenses()) {
            try {
                PreparedStatement expensesQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet results = expensesQuery.executeQuery();
                materialPurchasesPanel.showMaterialExpenses(results, this);
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Error viewing expenses.");
                throwables.printStackTrace();
            }
        }
    }

    boolean checkViewMaterialExpenses() {
        if (materialPurchasesPanel.getFilterDoeSelected() || materialPurchasesPanel.getFilterDopSelected()) {
            if (materialPurchasesPanel.getFilterFromDate() == null && materialPurchasesPanel.getFilterToDate() == null) {
                showMessage("Error viewing expenses","Please enter viewing dates.");
                return false;
            } else if (materialPurchasesPanel.getFilterFromDate() == null && materialPurchasesPanel.getFilterToDate() != null) {
                showMessage("Error viewing expenses","Please enter a From viewing date.");
                return false;
            }
        }
        return true;
    }

    void updateMaterialExpense(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            JTable purchasesTable = materialPurchasesPanel.getPurchasesTable();
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = purchasesTable.getValueAt(row, column).toString();
            String columnName = purchasesTable.getColumnName(column);
            String id = purchasesTable.getValueAt(row, 0).toString();

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
        JTable purchasesTable = materialPurchasesPanel.getPurchasesTable();
        int[] rows = purchasesTable.getSelectedRows();

        for (int i: rows) {
            String id = purchasesTable.getValueAt(i, 0).toString();
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


    //  ADD ORDER


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
                query.setString(3, K.productionStatus_1);
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


    //  VIEW ORDERS


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


    //  ADD MATERIAL


    void addMaterial() {
        if (checkAddMaterial()) {
            String sql = "INSERT INTO \"Materials\" values (?, ?, ?, ?)";
            try {
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString( 1, rawMaterialsPanel.getAddName());
                query.setDouble( 2, 0.0);
                query.setDate( 3, java.sql.Date.valueOf(new SimpleDateFormat("yyyy-MM-dd").format(new Date())));
                query.setString( 4, rawMaterialsPanel.getAddUnit());
                query.executeUpdate();
                query.close();
                showMessage("Operation successful","New material added.");
                rawMaterialsPanel.clearAddFields();
                getMaterials();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation","Material could not be added.");
            }
        }
    }

    boolean checkAddMaterial() {
        if (rawMaterialsPanel.getAddName().equals("")) {
            showMessage("Error adding material","Material name cannot be empty.");
            return false;
        } else if (rawMaterialsPanel.getAddUnit().equals("")) {
            showMessage("Error adding material","Material unit cannot be empty.");
            return false;
        }
        return true;
    }


    //  VIEW MATERIALS


    void getMaterials() {
        String query = "SELECT * FROM public.\"Materials\"";
        try {
            PreparedStatement materialsQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            ResultSet materials = materialsQuery.executeQuery();
            rawMaterialsPanel.getMaterials(materials, this);
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    void updateMaterial(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            JTable materialsTable = rawMaterialsPanel.getMaterialsTable();

            int row = e.getFirstRow();
            int column = e.getColumn();
            String columnName = materialsTable.getColumnName(column);
            String newValue = materialsTable.getValueAt(row, column).toString();
            String materialName = materialsTable.getValueAt(row, 0).toString();

            String query = "UPDATE \"Materials\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Material_name\" = '" + materialName + "'";
            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Item updated successfully.");
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Could not update item. Please review the new values.");
            }
        }
        getMaterials();
        calculateFormulaPrices();
    }

    void deleteMaterial() {
        int row = rawMaterialsPanel.getMaterialsTable().getSelectedRow();
        String id = rawMaterialsPanel.getMaterialsTable().getValueAt(row, 0).toString();

        String query = "DELETE FROM \"Materials\" WHERE \"Material_ID\" = " + id;

        try {
            sqlStatement.executeUpdate(query);
            showMessage("Delete successful","Item deleted successfully.");
        } catch (SQLException throwables) {
            throwables.printStackTrace();
            showMessage("Error performing operation", "Could not delete item.");
        }
        getMaterials();
    }


    //  ADD PRODUCTION


    void addProduction() {
        AddProductionView ApView = productionPanel.getAddProductionView();

        if (checkAddProduction()) {
            String formula = ApView.getApFormula();
            double productionQuantity = ApView.getApProductionQuantity();
            double batchQuantity = ApView.getApBatchQuantity();
            int batchesNumber = (int) (productionQuantity/batchQuantity);


            String sql = "INSERT INTO \"Production\" values (DEFAULT, ?, ?, ?, ?, ?, ?, ?)";

            try {
                Array ordersArray = databaseConnection.createArrayOf("INTEGER", ApView.getApOrders());
                PreparedStatement query = databaseConnection.prepareStatement(sql);
                query.setString(1, formula);
                query.setString(2,productionQuantity + " Kg");
                query.setString(3,batchQuantity + " Kg");
                query.setInt(4, batchesNumber);
                query.setArray(5,null);
                query.setArray(6,ordersArray);
                query.setString(7, K.productionStatus_1);
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

        if (ApView.getApProductionQuantity() == -13.11) {
            showMessage("Error adding production", "Production quantity must be numbers.");
            return false;
        } else if (ApView.getApProductionQuantity() == 0) {
            showMessage("Error adding production", "Production quantity cannot be 0 or empty.");
            return false;
        } else if (ApView.getApBatchQuantity() == -13.11) {
            showMessage("Error adding production", "Batch quantity must be numbers.");
            return false;
        } else if (ApView.getApBatchQuantity() == 0) {
            showMessage("Error adding production", "Batch quantity cannot be 0 or empty.");
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


    //  VIEW PRODUCTIONS


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

            String query = "UPDATE \"Production\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Production_ID\" = " + serial;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Production order updated successfully.");
            } catch (SQLException throwables) {
                showMessage("Error performing operation", "Could not update production order. Please review the new values.");
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

            for (int i = 0 ; i < rawMaterialsPanel.getMaterialsTable().getRowCount() ; i++) {
                if (rawMaterialsPanel.getMaterialsTable().getValueAt(i, 1).toString().equals(materialName)) {
                    availableQuantity = Double.parseDouble(rawMaterialsPanel.getMaterialsTable().getValueAt(i, 2).toString());
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
                String id = "'" + productionTable.getValueAt(row, 0).toString() + "'";

                String query = "DELETE FROM \"Production\" WHERE \"Production_ID\" = " + id;

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


    //  CREATE NEW FORMULA


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
            calculateFormulaPrices();
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
                        formulaDescription += " " + rawMaterialsPanel.getMaterialsTable().getValueAt(i, 3).toString();
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


    //  VIEW FORMULAS


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

    void calculateFormulaPrices() {
        for (int i = 0 ; i < formulasPanel.getFormulasTable().getRowCount() ; i++) {
            double formulaPrice = 0.0;
            String formulaName = formulasPanel.getFormulasTable().getValueAt(i, 0).toString();
            String formulaDescription = formulasPanel.getFormulasTable().getValueAt(i, 1).toString();
            String [] materials = formulaDescription.split(" - ");
            Double [] materialQuantities = new Double[materials.length];
            for (int j = 0 ; j < materials.length ; j++) {
                String temp = materials[j].split(":")[1];
                temp = temp.substring(1);
                temp = temp.split(" ")[0];
                materialQuantities[j] = Double.parseDouble(temp);
                materials[j] = materials[j].split(":")[0];
            }
            for (int j = 0 ; j < materials.length ; j++) {
                String materialName = materials[j];
                String sql = "SELECT \"Price/Kg\" FROM \"Materials\" WHERE \"Material_name\" = '" + materialName + "'";
                try {
                    ResultSet materialInfo = sqlStatement.executeQuery(sql);
                    materialInfo.next();
                    if (materialInfo.getString(1) != null) {
                        String materialPricePerKg = materialInfo.getString(1);
                        formulaPrice += Double.parseDouble(materialPricePerKg.split("/")[0]) * materialQuantities[j];
                    } else {
                        formulaPrice = 0.0;
                        break;
                    }
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
            if (formulaPrice > 0) {
                String sql = "UPDATE \"Formulas\" SET \"Formula_price\" = '" + formulaPrice + " EGP' WHERE \"Formula_ID\" = '" + formulaName + "'";
                try {
                    sqlStatement.executeUpdate(sql);
                } catch (SQLException throwables) {
                    throwables.printStackTrace();
                }
            }
        }
        getFormulas();
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


    //  ADD VENDOR


    void addVendor() {
        if (checkAddVendor()) {
            String vendorName = vendorsPanel.getAddVendorName();
            String contactName = vendorsPanel.getAddContactName();
            String contactNumber = vendorsPanel.getAddContactNumber();
            String contactEmail = vendorsPanel.getAddContactEmail();

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
                vendorsPanel.clearAddFields();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Operation unsuccessful", "Could not add new vendor.");
            }
        }
    }

    boolean checkAddVendor() {
        if (vendorsPanel.getAddVendorName().equals("")) {
            showMessage("Error adding vendor", "Vendor name cannot be empty.");
            return false;
        } else if (vendorsPanel.getAddContactName().equals("")) {
            showMessage("Error adding vendor","Contact name cannot be empty.");
            return false;
        } else if (vendorsPanel.getAddContactNumber().equals("")) {
            showMessage("Error adding vendor","Contact number cannot be empty.");
            return false;
        } else if (vendorsPanel.getAddContactEmail().equals("")) {
            showMessage("Error adding vendor","Contact email cannot be empty.");
            return false;
        }
        return true;
    }


    //  VIEW VENDORS


    void viewVendors() {
        if (checkViewVendors()) {
            boolean vendorIsSelected = vendorsPanel.getFilterVendorNameSelected();
            boolean contactIsSelected = vendorsPanel.getFilterContactNameSelected();

            String query;

            if (vendorIsSelected) {
                String vendorName = "'" + vendorsPanel.getFilterVendorName() + "'";
                query = "SELECT * FROM public.\"Vendors\" WHERE \"Vendor_name\" = " + vendorName;
            } else if (contactIsSelected) {
                String contactName = "'" + vendorsPanel.getFilterContactName() + "'";
                query = "SELECT * FROM public.\"Vendors\" WHERE \"Contact_name\" = " + contactName;
            } else {
                query = "SELECT * FROM public.\"Vendors\"";
            }

            try {
                PreparedStatement vendorsQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet vendors = vendorsQuery.executeQuery();
                vendorsPanel.showVendors(vendors, this);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showMessage("Error performing operation", "Error viewing vendors.");
            }
        }
    }

    boolean checkViewVendors() {
        if (vendorsPanel.getFilterVendorNameSelected()) {
            if (vendorsPanel.getFilterVendorName().equals("")) {
                showMessage("Error viewing vendors", "Please enter a vendor name to filter by.");
                return false;
            }
        } else if (vendorsPanel.getFilterContactNameSelected()) {
            if (vendorsPanel.getFilterContactName().equals("")) {
                showMessage("Error viewing vendors", "Please enter a contact name to filter by.");
                return false;
            }
        }
        return true;
    }

    void updateVendor(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            JTable vendorsTable = vendorsPanel.getVendorsTable();
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = vendorsTable.getValueAt(row, column).toString();
            String columnName = vendorsTable.getColumnName(column);
            String id = vendorsTable.getValueAt(row, 0).toString();

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
        JTable vendorsTable = vendorsPanel.getVendorsTable();
        if (vendorsTable != null) {
            if (vendorsTable.getModel() != null) {
                int row = vendorsTable.getSelectedRow();
                String id = vendorsTable.getValueAt(row, 0).toString();

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


    //  STORAGE


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


    //  BATCHES


    void viewBatches() {
        if (checkViewStorage()) {
            boolean batchesFilterSelected = batchesPanel.getFilterBatchSelected();
            boolean productionFilterSelected = batchesPanel.getFilterProductionSelected();
            boolean formulaFilterSelected = batchesPanel.getFilterFormulaSelected();
            boolean statusFilterSelected = batchesPanel.getFilterStatusSelected();

            String query;

            if (batchesFilterSelected) {
                query = "SELECT * FROM \"Batches\" WHERE \"Batch_serial\" = " + batchesPanel.getFilterBatch();
            } else if (productionFilterSelected) {
                query = "SELECT * FROM \"Batches\" WHERE \"Production_order\" = " + batchesPanel.getFilterProduction();
            } else if (formulaFilterSelected) {
                query = "SELECT * FROM \"Batches\" WHERE \"Formula_name\" = '" + batchesPanel.getFilterFormula() + "'";
            } else if (statusFilterSelected) {
                query = "SELECT * FROM \"Batches\" WHERE \"Batch_status\" = '" + batchesPanel.getFilterStatus() + "'";
            } else {
                query = "SELECT * FROM \"Batches\"";
            }

            try {
                PreparedStatement materialsQuery = databaseConnection.prepareStatement(query, ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
                ResultSet batches = materialsQuery.executeQuery();
                homeView.getBatchesPanel().viewBatches(batches, this);
            } catch (SQLException throwables) {
                throwables.printStackTrace();
                showErrorMessage("Error viewing batches", "Could not view batches", throwables.getLocalizedMessage());
            }
        }
    }

    void updateBatch(TableModelEvent e) {
        if (checkUpdatePrivilege()) {
            JTable batchesTable = batchesPanel.getBatchesTable();
            int row = e.getFirstRow();
            int column = e.getColumn();
            String newValue = batchesTable.getValueAt(row, column).toString();
            String columnName = batchesTable.getColumnName(column);
            String serial = batchesTable.getValueAt(row, 0).toString();

            String query = "UPDATE \"Batches\" SET \"" + columnName + "\" = '" + newValue + "' WHERE \"Batch_serial\" = " + serial;

            try {
                sqlStatement.executeUpdate(query);
                showMessage("Update successful", "Batch updated successfully.");
            } catch (SQLException throwables) {
                showErrorMessage("Error performing operation", "Could not update batch. Please review the new values.", throwables.getLocalizedMessage());
                throwables.printStackTrace();
            }
        }
        viewBatches();
    }

    void deleteBatch() {
        JTable batchesTable = batchesPanel.getBatchesTable();
        if (batchesTable != null) {
            if (batchesTable.getModel() != null) {
                int row = batchesTable.getSelectedRow();
                String serial = batchesTable.getValueAt(row, 0).toString();

                String query = "DELETE FROM \"Batches\" WHERE \"Batch_serial\" = " + serial;

                try {
                    sqlStatement.executeUpdate(query);
                    showMessage("Delete successful","Batch deleted successfully.");
                } catch (SQLException throwables) {
                    showErrorMessage("Error performing operation", "Could not delete batch.", throwables.getLocalizedMessage());
                    throwables.printStackTrace();
                }
                viewBatches();
            }
        }
    }


    //  OTHER METHODS


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
        if (currentUserType.equals("Admin")) {
            return true;
        } else {
            showMessage("Confirm error", "Update unsuccessful\nAdmin privileges needed to confirm update");
            return false;
        }
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == view.getLoginView().getContinueButton()) {
            connectToServer();
        }

        // GENERAL EXPENSES

        else if (e.getSource() == generalPurchasesPanel.getAddPurchaseButton()) {
            addGeneralExpense();
        } else if (e.getSource() == generalPurchasesPanel.getViewPurchasesButton()) {
            viewGeneralExpenses();
        } else if (e.getSource() == generalPurchasesPanel.getDeletePurchaseButton()) {
            deleteGeneralExpense();
        }

        //  MATERIAL EXPENSES

        else if (e.getSource() == materialPurchasesPanel.getAddPurchaseButton()) {
            addMaterialExpense();
        } else if (e.getSource() == materialPurchasesPanel.getViewPurchasesButton()) {
            viewMaterialExpenses();
        } else if (e.getSource() == materialPurchasesPanel.getDeletePurchaseButton()) {
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

        else if (e.getSource() == rawMaterialsPanel.getRefreshMaterialsButton()) {
            getMaterials();
        } else if (e.getSource() == rawMaterialsPanel.getAddMaterialButton()) {
            addMaterial();
        } else if (e.getSource() == rawMaterialsPanel.getDeleteMaterialButton()) {
            deleteMaterial();
        }

        //  FORMULAS

        else if (e.getSource() == formulasPanel.getCreateNewFormulaButton()) {
            formulasPanel.showCreateFormulaView(this);
        } else if (e.getActionCommand().equals("Create Formula")) {
            checkCreateFormula();
        } else if (e.getSource() == formulasPanel.getRefreshFormulasButton()) {
            getFormulas();
            calculateFormulaPrices();
        } else if (e.getSource() == formulasPanel.getDeleteFormulaButton()) {
            deleteFormula();
        }

        //  VENDORS

        else if (e.getSource() == vendorsPanel.getAddVendorButton()) {
            addVendor();
        } else if (e.getSource() == vendorsPanel.getViewVendorsButton()) {
            viewVendors();
        } else if (e.getSource() == vendorsPanel.getDeleteVendorButton()) {
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

        //  BATCHES

        else if (e.getSource() == batchesPanel.getViewBatchesButton()) {
            viewBatches();
        } else if (e.getSource() == batchesPanel.getDeleteBatchButton()) {
            deleteBatch();
        }
    }

    @Override
    public void tableChanged(TableModelEvent e) {
        if (generalPurchasesPanel.getPurchasesTable() != null) {
            if (generalPurchasesPanel.getPurchasesTable().getModel() != null) {
                if (e.getSource() == generalPurchasesPanel.getPurchasesTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateGeneralExpense(e);
                    }
                }
            }
        } if (materialPurchasesPanel.getPurchasesTable() != null) {
            if (materialPurchasesPanel.getPurchasesTable().getModel() != null) {
                if (e.getSource() == materialPurchasesPanel.getPurchasesTable().getModel()) {
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
        } if (rawMaterialsPanel.getMaterialsTable() != null) {
            if (rawMaterialsPanel.getMaterialsTable().getModel() != null) {
                if (e.getSource() == rawMaterialsPanel.getMaterialsTable().getModel()) {
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
        } if (vendorsPanel.getVendorsTable() != null) {
            if (vendorsPanel.getVendorsTable().getModel() != null) {
                if (e.getSource() == vendorsPanel.getVendorsTable().getModel()) {
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
        } if (batchesPanel.getBatchesTable() != null) {
            if (batchesPanel.getBatchesTable().getModel() != null) {
                if (e.getSource() == batchesPanel.getBatchesTable().getModel()) {
                    if (e.getType() == TableModelEvent.UPDATE) {
                        updateBatch(e);
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


