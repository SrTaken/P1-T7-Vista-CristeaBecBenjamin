package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import org.milaifontanals.club.Equip;
import org.milaifontanals.club.IClubOracleBD;
import java.util.List;

/**
 * @author isard
 */
public class EquipsWindow extends JPanel {
    private IClubOracleBD gBD;
    private List<Equip> equips;
    private DefaultTableModel tableModel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable table;

    public EquipsWindow(IClubOracleBD gBD) {
        this.gBD = gBD;
        setLayout(new BorderLayout(20, 20)); // Margin
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel listPanel = createListPanel();
        JPanel addPanel = new ModEquipWindow(gBD, mainPanel, "listPanel", null);
        
        
        
        
        //table.getSelectedRow();

        mainPanel.add(listPanel, "listPanel");
        mainPanel.add(addPanel, "addPanel");
        
        add(mainPanel, BorderLayout.CENTER);
        showListPanel();
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        // Filtros
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(230, 230, 230));

        JTextField txtFilter = new JTextField(15);
        JButton btnApplyFilter = new JButton("Aplicar Filtres");

        filterPanel.add(new JLabel("Filtrar per nom:"));
        filterPanel.add(txtFilter);
        filterPanel.add(btnApplyFilter);

        panel.add(filterPanel, BorderLayout.NORTH);

        // DataGrid
        String[] columnNames = {"ID", "Nom", "Categoria", "Tipus", "Temporada"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        table.setPreferredScrollableViewportSize(new Dimension(300, 200));
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        // Botones
        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton btnAdd = new JButton("Afegir Equip");
        JButton btnEdit = new JButton("Editar Equip");
        JButton btnDelete = new JButton("Eliminar Equip");

        btnAdd.addActionListener(e -> showAddPanel());
        btnEdit.addActionListener(e -> showModPanel());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        carregarEquips();

        return panel;
    }

    public void carregarEquips() {
        tableModel.setRowCount(0); // Limpiar la tabla
        try {
            equips = gBD.obtenirLlistaEquip();
            for (Equip equip : equips) {
                Object[] rowData = {equip.getId(), equip.getNom(), equip.getCategoria(), equip.getTipus(), equip.getTemporada()};
                tableModel.addRow(rowData);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar els equips: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            infoError(ex);
        }
    }

    private void showAddPanel() {
        cardLayout.show(mainPanel, "addPanel");
    }
    private void showModPanel() {
        int selectedRow = table.getSelectedRow();
        if(selectedRow != -1){
            Equip e = equips.get(selectedRow);
            JPanel modPanel = new ModEquipWindow(gBD, mainPanel, "listPanel", e);
            mainPanel.add(modPanel, "modPanel");
            cardLayout.show(mainPanel, "modPanel");
        }
            
    }

    public void showListPanel() {
        carregarEquips();
        cardLayout.show(mainPanel, "listPanel");
    }

    private static void infoError(Throwable aux) {
        do {
            if (aux.getMessage() != null) {
                System.out.println("\t" + aux.getMessage());
            }
            aux = aux.getCause();
        } while (aux != null);
    }
}
