package View;

import Controller.SystemController;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class AddProductionView extends JFrame implements ActionListener {

    private Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();

    private HomeView homeView;
    private SystemController controller;

    private JPanel ApPanel;

    private JComboBox ApFormulasComboBox;
    private JTextField ApQuantityField;
    private JTextArea ApOrdersArea;
    private JButton ApAddButton;

    private JPanel packagingPanel;
    private JScrollPane packagingScrollPane;

    private JPanel tanksPanel;
    private JLabel tanksLabel;
    private JButton addTanksButton;

    private JPanel drumsPanel;
    private JLabel drumsLabel;
    private JButton addDrumsButton;

    private JPanel pailsPanel;
    private JLabel pailsLabel;
    private JButton addPailsButton;

    private JPanel cartonsPanel;
    private JLabel cartonsLabel;
    private JButton addCartonsButton;

    private JPanel gallonsPanel;
    private JLabel gallonsLabel;
    private JButton addGallonsButton;


    public AddProductionView(SystemController controller, HomeView homeView) {
        this.controller = controller;
        this.homeView = homeView;

        add(ApPanel);

        setSize(600,700);
        setLocation(screenDimensions.width/2 - getSize().width/2, screenDimensions.height/2 - getSize().height/2);

        setResizable(false);
        setVisible(true);

        ApAddButton.addActionListener(controller);

        getFormulas();

        initializeTanksPanel();
        initializeDrumsPanel();
        initializePailsPanel();
        initializeCartonsPanel();
        initializeGallonsPanel();

        addPackagingOptions();
    }

    private void getFormulas() {
        int formulasCount = homeView.getVfTable().getRowCount();
        ApFormulasComboBox.addItem("Select Formula");

        for (int i = 0 ; i < formulasCount ; i++) {
            ApFormulasComboBox.addItem(homeView.getVfTable().getValueAt(i, 0));
        }

    }

    private void addPackagingOptions() {

    }

    public JComboBox getApFormulasComboBox() {
        return ApFormulasComboBox;
    }

    public Double getApQuantity() {
        if (!ApQuantityField.getText().equals("")) {
            try {
                return Double.parseDouble(ApQuantityField.getText());
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        } else {
            return 0.0;
        }
        return -13.11;
    }

    public Object[] getApOrders() {
        Object[] array = ApOrdersArea.getText().split("\n");
        return array;
    }

    public JButton getApAddButton() {
        return ApAddButton;
    }

    private void initializeTanksPanel() {
        tanksLabel = new JLabel("Tanks");
        tanksLabel.setHorizontalAlignment(JLabel.CENTER);

        addTanksButton = new JButton("Add more tanks");
        addTanksButton.addActionListener(this);

        tanksPanel.setLayout(new GridLayout(2,1));
        tanksPanel.add(tanksLabel);
        tanksPanel.add(addTanksButton);
    }

    private void initializeDrumsPanel() {
        drumsLabel = new JLabel("Drums");
        drumsLabel.setHorizontalAlignment(JLabel.CENTER);

        addDrumsButton = new JButton("Add more drums");
        addDrumsButton.addActionListener(this);

        drumsPanel.setLayout(new GridLayout(2,1));
        drumsPanel.add(drumsLabel);
        drumsPanel.add(addDrumsButton);
    }

    private void initializePailsPanel() {
        pailsLabel = new JLabel("Pails");
        pailsLabel.setHorizontalAlignment(JLabel.CENTER);

        addPailsButton = new JButton("Add more pails");
        addPailsButton.addActionListener(this);

        pailsPanel.setLayout(new GridLayout(2,1));
        pailsPanel.add(pailsLabel);
        pailsPanel.add(addPailsButton);
    }

    private void initializeCartonsPanel() {
        cartonsLabel = new JLabel("Cartons");
        cartonsLabel.setHorizontalAlignment(JLabel.CENTER);

        addCartonsButton = new JButton("Add more cartons");
        addCartonsButton.addActionListener(this);

        cartonsPanel.setLayout(new GridLayout(2,1));
        cartonsPanel.add(cartonsLabel);
        cartonsPanel.add(addCartonsButton);
    }

    private void initializeGallonsPanel() {
        gallonsLabel = new JLabel("Gallons");
        gallonsLabel.setHorizontalAlignment(JLabel.CENTER);

        addGallonsButton = new JButton("Add more gallons");
        addGallonsButton.addActionListener(this);

        gallonsPanel.setLayout(new GridLayout(2,1));
        gallonsPanel.add(gallonsLabel);
        gallonsPanel.add(addGallonsButton);
    }

    private void addMore(JPanel panel) {
        GridLayout grid = (GridLayout) panel.getLayout();
        int rows = grid.getRows();
        grid.setRows(rows + 1);

        JLabel label1 = new JLabel("Quantity");
        label1.setHorizontalAlignment(JLabel.CENTER);
        JLabel label2 = new JLabel("Weight");
        label2.setHorizontalAlignment(JLabel.CENTER);

        JPanel newPanel = new JPanel(new GridLayout(1,4));
        newPanel.add(label1);
        newPanel.add(new JTextField());
        newPanel.add(label2);
        newPanel.add(new JTextField());

        panel.add(newPanel, rows-1);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addTanksButton) {
            addMore(tanksPanel);
        } else if (e.getSource() == addDrumsButton) {
            addMore(drumsPanel);
        } else if (e.getSource() == addPailsButton) {
            addMore(pailsPanel);
        } else if (e.getSource() == addCartonsButton) {
            addMore(cartonsPanel);
        } else if (e.getSource() == addGallonsButton) {
            addMore(gallonsPanel);
        }
        packagingPanel.setPreferredSize(new Dimension(0,packagingPanel.getHeight()+40));
        this.validate();
    }
}
