package View.HelperPanels;

import Controller.SystemController;
import View.HomeView;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class AddBatchContainersView extends JFrame implements ActionListener {

    private Dimension screenDimensions = Toolkit.getDefaultToolkit().getScreenSize();
    private Font font;

    private HomeView homeView;
    private SystemController controller;

    private JPanel ApPanel;

    private JButton ApAddButton;

    private JPanel packagingPanel;

    private JPanel tanksPanel;
    private JPanel drumsPanel;
    private JPanel pailsPanel;
    private JPanel cartonsPanel;
    private JPanel gallonsPanel;
    private JLabel batchSerialLabel;

    private JLabel tanksLabel;
    private JLabel drumsLabel;
    private JLabel pailsLabel;
    private JLabel cartonsLabel;
    private JLabel gallonsLabel;

    private JButton addTanksButton;
    private JButton addDrumsButton;
    private JButton addPailsButton;
    private JButton addCartonsButton;
    private JButton addGallonsButton;

    private ArrayList<JPanel> tanksPanels;
    private ArrayList<JPanel> drumsPanels;
    private ArrayList<JPanel> pailsPanels;
    private ArrayList<JPanel> cartonsPanels;
    private ArrayList<JPanel> gallonsPanels;

    private String batchSerial;
    private String batchFormula;

    public AddBatchContainersView(SystemController controller, HomeView homeView, String batchSerial, String formulaName) {
        this.controller = controller;
        this.homeView = homeView;
        batchSerialLabel.setText("Batch serial: " + batchSerial);

        this.batchSerial = batchSerial;
        this.batchFormula = formulaName;

        add(ApPanel);

        initializeTanksPanel();
        initializeDrumsPanel();
        initializePailsPanel();
        initializeCartonsPanel();
        initializeGallonsPanel();

        ApAddButton.addActionListener(controller);

        setSize(600,700);
        setLocation(screenDimensions.width/2 - getSize().width/2, screenDimensions.height/2 - getSize().height/2);

        setResizable(false);
        setVisible(true);
    }

    private void initializeTanksPanel() {
        tanksLabel = new JLabel("Tanks");
        tanksLabel.setHorizontalAlignment(JLabel.CENTER);
        font = new Font(tanksLabel.getFont().getName(), Font.BOLD, tanksLabel.getFont().getSize() + 3);
        tanksLabel.setFont(font);

        addTanksButton = new JButton("Add more tanks");
        addTanksButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(new JLabel(""));
        panel.add(addTanksButton);
        panel.add(new JLabel(""));

        tanksPanel.setLayout(new GridLayout(2,1));
        tanksPanel.add(tanksLabel);
        tanksPanel.add(panel);

        tanksPanels = new ArrayList<>();
    }

    private void initializeDrumsPanel() {
        drumsLabel = new JLabel("Drums");
        drumsLabel.setHorizontalAlignment(JLabel.CENTER);
        drumsLabel.setFont(font);

        addDrumsButton = new JButton("Add more drums");
        addDrumsButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(new JLabel(""));
        panel.add(addDrumsButton);
        panel.add(new JLabel(""));

        drumsPanel.setLayout(new GridLayout(2,1));
        drumsPanel.add(drumsLabel);
        drumsPanel.add(panel);

        drumsPanels = new ArrayList<>();
    }

    private void initializePailsPanel() {
        pailsLabel = new JLabel("Pails");
        pailsLabel.setHorizontalAlignment(JLabel.CENTER);
        pailsLabel.setFont(font);

        addPailsButton = new JButton("Add more pails");
        addPailsButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(new JLabel(""));
        panel.add(addPailsButton);
        panel.add(new JLabel(""));

        pailsPanel.setLayout(new GridLayout(2,1));
        pailsPanel.add(pailsLabel);
        pailsPanel.add(panel);

        pailsPanels = new ArrayList<>();
    }

    private void initializeCartonsPanel() {
        cartonsLabel = new JLabel("Cartons");
        cartonsLabel.setHorizontalAlignment(JLabel.CENTER);
        cartonsLabel.setFont(font);

        addCartonsButton = new JButton("Add more cartons");
        addCartonsButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(new JLabel(""));
        panel.add(addCartonsButton);
        panel.add(new JLabel(""));

        cartonsPanel.setLayout(new GridLayout(2,1));
        cartonsPanel.add(cartonsLabel);
        cartonsPanel.add(panel);

        cartonsPanels = new ArrayList<>();
    }

    private void initializeGallonsPanel() {
        gallonsLabel = new JLabel("Gallons");
        gallonsLabel.setHorizontalAlignment(JLabel.CENTER);
        gallonsLabel.setFont(font);

        addGallonsButton = new JButton("Add more gallons");
        addGallonsButton.addActionListener(this);

        JPanel panel = new JPanel();
        panel.add(new JLabel(""));
        panel.add(addGallonsButton);
        panel.add(new JLabel(""));

        gallonsPanel.setLayout(new GridLayout(2,1));
        gallonsPanel.add(gallonsLabel);
        gallonsPanel.add(panel);

        gallonsPanels = new ArrayList<>();
    }

    public ArrayList<JPanel> getTanksPanels() {
        return tanksPanels;
    }

    public ArrayList<JPanel> getDrumsPanels() {
        return drumsPanels;
    }

    public ArrayList<JPanel> getPailsPanels() {
        return pailsPanels;
    }

    public ArrayList<JPanel> getCartonsPanels() {
        return cartonsPanels;
    }

    public ArrayList<JPanel> getGallonsPanels() {
        return gallonsPanels;
    }

    private JPanel addMoreFields(JPanel panel) {
        GridLayout grid = (GridLayout) panel.getLayout();
        if (grid.getRows() == 2) {
            grid.setRows(grid.getRows() + 2);
            JPanel newPanel = new JPanel(new GridLayout(1,3));
            JLabel label1 = new JLabel("#");
            JLabel label2 = new JLabel("Quantity");
            JLabel label3 = new JLabel("Weight");
            label1.setHorizontalAlignment(JLabel.CENTER);
            label2.setHorizontalAlignment(JLabel.CENTER);
            label3.setHorizontalAlignment(JLabel.CENTER);
            label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
            label2.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
            label3.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));
            newPanel.add(label1);
            newPanel.add(label2);
            newPanel.add(label3);

            panel.add(newPanel, 1);
            packagingPanel.setPreferredSize(new Dimension(0,packagingPanel.getHeight()+70));
        } else {
            grid.setRows(grid.getRows() + 1);
        }

        JLabel label1 = new JLabel((grid.getRows()-3) + "");
        label1.setHorizontalAlignment(JLabel.CENTER);
        label1.setFont(new Font(label1.getFont().getName(), Font.BOLD, label1.getFont().getSize()));

        JPanel newPanel = new JPanel(new GridLayout(1,3));
        newPanel.add(label1);
        newPanel.add(new JTextField());
        newPanel.add(new JTextField());
        panel.add(newPanel, grid.getRows()-2);
        packagingPanel.setPreferredSize(new Dimension(0,packagingPanel.getHeight()+70));

        return newPanel;
    }

    public String getBatchFormula() {
        return batchFormula;
    }

    public String getBatchSerial() {
        return batchSerial;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == addTanksButton) {
            tanksPanels.add(addMoreFields(tanksPanel));
        } else if (e.getSource() == addDrumsButton) {
            drumsPanels.add(addMoreFields(drumsPanel));
        } else if (e.getSource() == addPailsButton) {
            pailsPanels.add(addMoreFields(pailsPanel));
        } else if (e.getSource() == addCartonsButton) {
            cartonsPanels.add(addMoreFields(cartonsPanel));
        } else if (e.getSource() == addGallonsButton) {
            gallonsPanels.add(addMoreFields(gallonsPanel));
        }
        this.validate();
    }
}
