package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import org.milaifontanals.club.Equip;
import org.milaifontanals.club.IClubOracleBD;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.milaifontanals.club.GestorBDClub;
import java.util.ArrayList;
import java.util.Collections;
import javax.swing.Box;

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
    private boolean ordenadoPorCategoria = false;

    public EquipsWindow(IClubOracleBD gBD) {
        this.gBD = gBD;
        setLayout(new BorderLayout(20, 20)); // Margin
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel listPanel = createListPanel();
        JPanel addPanel = new ModEquipWindow(gBD, mainPanel, "listPanel", null);

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
        JButton btnSortByCategory = new JButton("Ordenar per Categoria");

        btnSortByCategory.addActionListener(e -> {
            if (!ordenadoPorCategoria) {
                List<Equip> equiposOrdenados = new ArrayList<>(equips);
                Collections.sort(equiposOrdenados, (e1, e2) -> 
                    e1.getCategoria().toString().compareTo(e2.getCategoria().toString()));
                
                tableModel.setRowCount(0);
                for (Equip equip : equiposOrdenados) {
                    Object[] rowData = {equip.getNom(), equip.getCategoria(), 
                                      equip.getTipus(), equip.getTemporada()};
                    tableModel.addRow(rowData);
                }
                equips = equiposOrdenados;
                btnSortByCategory.setText("Treure ordre");
            } else {
                carregarEquips();
                btnSortByCategory.setText("Ordenar per Categoria");
            }
            ordenadoPorCategoria = !ordenadoPorCategoria;
        });

        btnApplyFilter.addActionListener(e -> {
            String filtro = txtFilter.getText().trim().toLowerCase();
            if (filtro.isEmpty()) {
                carregarEquips(); 
                return;
            }

            tableModel.setRowCount(0);
            for (Equip equip : equips) {
                if (equip.getNom().toLowerCase().contains(filtro)) {
                    Object[] rowData = {equip.getNom(), equip.getCategoria(), 
                                      equip.getTipus(), equip.getTemporada()};
                    tableModel.addRow(rowData);
                }
            }
        });

        txtFilter.addKeyListener(new java.awt.event.KeyAdapter() {
            @Override
            public void keyPressed(java.awt.event.KeyEvent evt) {
                if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ESCAPE) {
                    txtFilter.setText("");
                    carregarEquips();
                } else if (evt.getKeyCode() == java.awt.event.KeyEvent.VK_ENTER) {
                    btnApplyFilter.doClick(); 
                }
            }
        });

        filterPanel.add(new JLabel("Filtrar per nom:"));
        filterPanel.add(txtFilter);
        filterPanel.add(btnApplyFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(btnSortByCategory);

        panel.add(filterPanel, BorderLayout.NORTH);

        // DataGrid
        String[] columnNames = {"Nom", "Categoria", "Tipus", "Temporada"};
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
        btnDelete.addActionListener(e -> eliminarEquip());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        carregarEquips();

        return panel;
    }

    public void carregarEquips() {
        tableModel.setRowCount(0); 
        try {
            equips = gBD.obtenirLlistaEquip();
            for (Equip equip : equips) {
                Object[] rowData = {equip.getNom(), equip.getCategoria(), equip.getTipus(), equip.getTemporada()};
                tableModel.addRow(rowData);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar els equips: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            infoError(ex);
        }
    }

    private void eliminarEquip() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String nomEquip = (String) tableModel.getValueAt(selectedRow, 0);
            Equip equipSeleccionado = null;
            for (Equip e : equips) {
                if (e.getNom().equals(nomEquip)) {
                    equipSeleccionado = e;
                    break;
                }
            }

            if (equipSeleccionado != null) {
                int opcion = JOptionPane.showConfirmDialog(this, 
                    "Estàs segur que vols eliminar el equip " + equipSeleccionado.getNom() + "?", 
                    "Confirmació d'eliminació", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION) {
                    try {
                        gBD.esborrarEquip(equipSeleccionado);
                        gBD.confirmarCanvis();
                        JOptionPane.showMessageDialog(this, "Equip esborrat correctament!", "Èxit", JOptionPane.INFORMATION_MESSAGE);
                        carregarEquips();
                    } catch (GestorBDClub ex) {
                        JOptionPane.showMessageDialog(this, "Error al eliminar el equip: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        infoError(ex);
                    }
                }
            }
        }
    }

    
    private void showAddPanel() {
        cardLayout.show(mainPanel, "addPanel");
        carregarEquips();
    }
    private void showModPanel() {
        int selectedRow = table.getSelectedRow();
        if(selectedRow != -1){
            String nomEquip = (String) tableModel.getValueAt(selectedRow, 0);
            Equip equipSeleccionado = null;
            for (Equip e : equips) {
                if (e.getNom().equals(nomEquip)) {
                    equipSeleccionado = e;
                    break;
                }
            }
            
            if (equipSeleccionado != null) {
                JPanel modPanel = new ModEquipWindow(gBD, mainPanel, "listPanel", equipSeleccionado);
                mainPanel.add(modPanel, "modPanel");
                cardLayout.show(mainPanel, "modPanel");
                carregarEquips();
            }
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
