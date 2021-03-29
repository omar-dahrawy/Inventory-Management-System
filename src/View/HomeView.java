package View;

import Controller.SystemController;
import View.MainPanels.*;
import javax.swing.*;

public class HomeView {

    private JTabbedPane mainTabbedPane;

    //  MAIN PANELS

    private JPanel homePanel;
    private final OrdersPanel ordersPanel;
    private final BatchesPanel batchesPanel;
    private final StoragePanel storagePanel;
    private final VendorsPanel vendorsPanel;
    private final FormulasPanel formulasPanel;
    private final ProductionPanel productionPanel;
    private final RawMaterialsPanel rawMaterialsPanel;
    private final GeneralPurchasesPanel generalPurchasesPanel;
    private final MaterialPurchasesPanel materialPurchasesPanel;

    public HomeView() {
        ordersPanel = new OrdersPanel(this);
        batchesPanel = new BatchesPanel(this);
        storagePanel = new StoragePanel(this);
        vendorsPanel = new VendorsPanel(this);
        formulasPanel = new FormulasPanel(this);
        productionPanel = new ProductionPanel(this);
        rawMaterialsPanel = new RawMaterialsPanel(this);
        generalPurchasesPanel = new GeneralPurchasesPanel();
        materialPurchasesPanel = new MaterialPurchasesPanel(this);

        JTabbedPane materialsTabbedPane = new JTabbedPane();
        materialsTabbedPane.add("Raw Materials", rawMaterialsPanel);
        materialsTabbedPane.add("Vendors", vendorsPanel);

        JTabbedPane purchasesTabbedPane = new JTabbedPane();
        purchasesTabbedPane.add("General Purchases", generalPurchasesPanel);
        purchasesTabbedPane.add("Material Purchases", materialPurchasesPanel);

        JTabbedPane productionTabbedPane = new JTabbedPane();
        productionTabbedPane.add("Production Orders", productionPanel);
        productionTabbedPane.add("Batches", batchesPanel);

        mainTabbedPane.add("Purchases", purchasesTabbedPane);
        mainTabbedPane.add("Materials", materialsTabbedPane);
        mainTabbedPane.add("Orders", ordersPanel);
        mainTabbedPane.add("Production", productionTabbedPane);
        mainTabbedPane.add("Formulas", formulasPanel);
        mainTabbedPane.add("Storage", storagePanel);
    }

    public void addActionListeners(SystemController controller) {
        ordersPanel.addActionListeners(controller);
        batchesPanel.addActionListeners(controller);
        storagePanel.addActionListeners(controller);
        vendorsPanel.addActionListeners(controller);
        formulasPanel.addActionListeners(controller);
        productionPanel.addActionListeners(controller);
        rawMaterialsPanel.addActionListeners(controller);
        generalPurchasesPanel.addActionListeners(controller);
        materialPurchasesPanel.addActionListeners(controller);
    }

    //  MAIN PANELS GETTERS

    public JPanel getHomePanel() {
        return homePanel;
    }

    public OrdersPanel getOrdersPanel() {
        return ordersPanel;
    }

    public BatchesPanel getBatchesPanel() {
        return batchesPanel;
    }

    public StoragePanel getStoragePanel() {
        return storagePanel;
    }

    public VendorsPanel getVendorsPanel() {
        return vendorsPanel;
    }

    public FormulasPanel getFormulasPanel() {
        return formulasPanel;
    }

    public ProductionPanel getProductionPanel() {
        return productionPanel;
    }

    public RawMaterialsPanel getRawMaterialsPanel() {
        return rawMaterialsPanel;
    }

    public GeneralPurchasesPanel getGeneralPurchasesPanel() {
        return generalPurchasesPanel;
    }

    public MaterialPurchasesPanel getMaterialPurchasesPanel() {
        return materialPurchasesPanel;
    }
}