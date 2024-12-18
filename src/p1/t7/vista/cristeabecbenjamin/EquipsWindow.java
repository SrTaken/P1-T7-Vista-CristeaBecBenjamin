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
import org.milaifontanals.club.Categoria;
import org.milaifontanals.club.Temporada;
import java.util.Calendar;
import org.milaifontanals.club.Membre;

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
    private JComboBox<Temporada> cmbFiltroTemporada;
    private JComboBox<Categoria> cmbFiltroCategoria;
    private JPanel tablePanel;
    private JLabel noEquipsLabel;
    private JScrollPane scrollPane;
    private JTextField txtFilter;
    private String lastFilterText = "";
    private Temporada lastTemporada = null;
    private Categoria lastCategoria = null;

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

        // filtritos
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(230, 230, 230));

        txtFilter = new JTextField(15);
        filterPanel.add(new JLabel("Filtrar per nom:"));
        filterPanel.add(txtFilter);

        filterPanel.add(new JLabel("Temporada:"));
        cmbFiltroTemporada = new JComboBox<>();
        cmbFiltroTemporada.addItem(null); 
        try {
            List<Temporada> temporadas = gBD.obtenirLlistaTemporada();
            int currentYear = getCurrentYear();
            for (Temporada t : temporadas) {
                cmbFiltroTemporada.addItem(t);
                if (t.getYear() == currentYear) {
                    cmbFiltroTemporada.setSelectedItem(t);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar temporades: " + ex.getMessage());
        }
        filterPanel.add(cmbFiltroTemporada);

        filterPanel.add(new JLabel("Categoria:"));
        cmbFiltroCategoria = new JComboBox<>();
        cmbFiltroCategoria.addItem(null); 
        try {
            List<Categoria> categorias = gBD.obtenirLlistaCategoria();
            for (Categoria c : categorias) {
                cmbFiltroCategoria.addItem(c);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar categories: " + ex.getMessage());
        }
        filterPanel.add(cmbFiltroCategoria);

        JButton btnApplyFilter = new JButton("Aplicar Filtres");
        JButton btnClearFilters = new JButton("Netejar Filtres");

        btnApplyFilter.addActionListener(e -> aplicarFiltros(txtFilter.getText().trim()));
        btnClearFilters.addActionListener(e -> {
            txtFilter.setText("");
            int currentYear = getCurrentYear();
            for (int i = 0; i < cmbFiltroTemporada.getItemCount(); i++) {
                Temporada t = cmbFiltroTemporada.getItemAt(i);
                if (t != null && t.getYear() == currentYear) {
                    cmbFiltroTemporada.setSelectedItem(t);
                    break;
                }
            }
            cmbFiltroCategoria.setSelectedItem(null);
            carregarEquips();
        });

        filterPanel.add(btnApplyFilter);
        filterPanel.add(btnClearFilters);

        panel.add(filterPanel, BorderLayout.NORTH);

        tablePanel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"Nom", "Categoria", "Tipus", "Temporada"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(300, 200));
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

        scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        noEquipsLabel = new JLabel("No existeix cap equip", SwingConstants.CENTER);
        noEquipsLabel.setForeground(Color.RED);
        noEquipsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        noEquipsLabel.setVisible(false);
        
        panel.add(tablePanel, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBackground(new Color(230, 230, 230));

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonPanel.setBackground(new Color(230, 230, 230));
        
        JButton btnReload = new JButton("Recarregar");
        btnReload.addActionListener(e -> {
            try {
                guardarEstadoFiltros();
                equips = gBD.obtenirLlistaEquip();
                tableModel.setRowCount(0);
                
                if (equips != null) {
                    ocultarMensajeNoEquips();
                    for (Equip equip : equips) {
                        Object[] rowData = {
                            equip.getNom(), 
                            equip.getCategoria(), 
                            equip.getTipus(), 
                            equip.getTemporada()
                        };
                        tableModel.addRow(rowData);
                    }
                    aplicarFiltros(lastFilterText);
                } else {
                    mostrarMensajeNoEquips();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al recarregar els equips: " + ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                infoError(ex);
            }
        });
        
        leftButtonPanel.add(btnReload);

        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.setBackground(new Color(230, 230, 230));

        JButton btnAdd = new JButton("Afegir Equip");
        JButton btnEdit = new JButton("Editar Equip");
        JButton btnDelete = new JButton("Eliminar Equip");

        btnAdd.addActionListener(e -> showAddPanel());
        btnEdit.addActionListener(e -> showModPanel());
        btnDelete.addActionListener(e -> eliminarEquip());

        rightButtonPanel.add(btnAdd);
        rightButtonPanel.add(btnEdit);
        rightButtonPanel.add(btnDelete);

        buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        carregarEquips();

        return panel;
    }

    public void carregarEquips() {
        tableModel.setRowCount(0);
        try {
            equips = gBD.obtenirLlistaEquip();
            if (equips != null) {
                //Filtrar temp actual
                List<Equip> equipsFiltrados = new ArrayList<>();
                int currentYear = getCurrentYear();
                
                for (Equip equip : equips) {
                    if (equip.getTemporada().getYear() == currentYear) {
                        equipsFiltrados.add(equip);
                        Object[] rowData = {equip.getNom(), equip.getCategoria(), equip.getTipus(), equip.getTemporada()};
                        tableModel.addRow(rowData);
                    }
                }
                equips = equipsFiltrados;
            }
            
            if (tableModel.getRowCount() == 0) {
                mostrarMensajeNoEquips();
            } else {
                ocultarMensajeNoEquips();
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar els equips: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            infoError(ex);
        }
    }

    private void mostrarMensajeNoEquips() {
        scrollPane.setVisible(false);
        if (noEquipsLabel.getParent() == null) {
            tablePanel.add(noEquipsLabel, BorderLayout.CENTER);
        }
        noEquipsLabel.setVisible(true);
        tablePanel.revalidate();
        tablePanel.repaint();
    }

    private void ocultarMensajeNoEquips() {
        noEquipsLabel.setVisible(false);
        scrollPane.setVisible(true);
        tablePanel.revalidate();
        tablePanel.repaint();
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
                try {
                    guardarEstadoFiltros();
                    
                    List<Membre> membres = gBD.obtenirLlistaMembre(equipSeleccionado.getId());
                    
                    String mensaje;
                    if (membres != null && !membres.isEmpty()) {
                        mensaje = String.format("L'equip %s té %d membres. Si continues s'eliminaran els membres i l'equip. Estàs segur?", 
                            equipSeleccionado.getNom(), membres.size());
                    } else {
                        mensaje = "Estàs segur que vols eliminar l'equip " + equipSeleccionado.getNom() + "?";
                    }

                    int opcion = JOptionPane.showConfirmDialog(this, 
                        mensaje,
                        "Confirmació d'eliminació", 
                        JOptionPane.YES_NO_OPTION, 
                        JOptionPane.WARNING_MESSAGE);

                    if (opcion == JOptionPane.YES_OPTION) {
                        if (membres != null && !membres.isEmpty()) {
                            for (Membre membre : membres) {
                                gBD.esborrarMembre(membre.getJ().getId(), equipSeleccionado.getId());
                            }
                        }
                        
                        gBD.esborrarEquip(equipSeleccionado);
                        gBD.confirmarCanvis();
                        
                        JOptionPane.showMessageDialog(this, 
                            "Equip esborrat correctament!", 
                            "Èxit", 
                            JOptionPane.INFORMATION_MESSAGE);
                            
                        equips = gBD.obtenirLlistaEquip();
                        tableModel.setRowCount(0);
                        if (equips != null) {
                            for (Equip equip : equips) {
                                Object[] rowData = {equip.getNom(), equip.getCategoria(), equip.getTipus(), equip.getTemporada()};
                                tableModel.addRow(rowData);
                            }
                        }
                        restaurarFiltros();
                    }
                } catch (GestorBDClub ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al eliminar l'equip: " + ex.getMessage(), 
                        "Error", 
                        JOptionPane.ERROR_MESSAGE);
                    infoError(ex);
                }
            }
        }
    }

    
    private void showAddPanel() {
        guardarEstadoFiltros();
        cardLayout.show(mainPanel, "addPanel");
    }
    private void showModPanel() {
        int selectedRow = table.getSelectedRow();
        if(selectedRow != -1){
            guardarEstadoFiltros();
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
            }
        }
    }

    public void showListPanel() {
        carregarEquips();
        cardLayout.show(mainPanel, "listPanel");
        restaurarFiltros();
    }

    private static void infoError(Throwable aux) {
        do {
            if (aux.getMessage() != null) {
                System.out.println("\t" + aux.getMessage());
            }
            aux = aux.getCause();
        } while (aux != null);
    }
    
    private void aplicarFiltros(String nombreFiltro) {
        tableModel.setRowCount(0);
        try {
            List<Equip> equipsFiltrados = new ArrayList<>();
            List<Equip> todosEquips = gBD.obtenirLlistaEquip();
            
            for (Equip equip : todosEquips) {
                boolean cumpleNombre = nombreFiltro.isEmpty() || 
                    equip.getNom().toLowerCase().contains(nombreFiltro.toLowerCase());
                
                Temporada tempSeleccionada = (Temporada) cmbFiltroTemporada.getSelectedItem();
                boolean cumpleTemporada = tempSeleccionada == null || 
                    equip.getTemporada().getYear() == tempSeleccionada.getYear();
                
                Categoria catSeleccionada = (Categoria) cmbFiltroCategoria.getSelectedItem();
                boolean cumpleCategoria = catSeleccionada == null || 
                    equip.getCategoria().getId() == catSeleccionada.getId();
                
                if (cumpleNombre && cumpleTemporada && cumpleCategoria) {
                    equipsFiltrados.add(equip);
                }
            }
            
            equips = equipsFiltrados;
            
            if (equips.isEmpty()) {
                mostrarMensajeNoEquips();
            } else {
                ocultarMensajeNoEquips();
                for (Equip equip : equips) {
                    Object[] rowData = {equip.getNom(), equip.getCategoria(), equip.getTipus(), equip.getTemporada()};
                    tableModel.addRow(rowData);
                }
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al aplicar els filtres: " + ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            infoError(ex);
        }
    }

    private int getCurrentYear() {
        return Calendar.getInstance().get(Calendar.YEAR) % 100;  // Obtiene los ultimos 2 dígitos para que coincida en como lo tengo en la db
    }

    private void guardarEstadoFiltros() {
        lastFilterText = txtFilter.getText().trim();
        lastTemporada = (Temporada) cmbFiltroTemporada.getSelectedItem();
        lastCategoria = (Categoria) cmbFiltroCategoria.getSelectedItem();
    }

    private void restaurarFiltros() {
        txtFilter.setText(lastFilterText);
        
        if (lastTemporada == null) {
            int currentYear = getCurrentYear();
            for (int i = 0; i < cmbFiltroTemporada.getItemCount(); i++) {
                Temporada t = cmbFiltroTemporada.getItemAt(i);
                if (t != null && t.getYear() == currentYear) {
                    cmbFiltroTemporada.setSelectedItem(t);
                    lastTemporada = t;
                    break;
                }
            }
        } else {
            cmbFiltroTemporada.setSelectedItem(lastTemporada);
        }
        
        cmbFiltroCategoria.setSelectedItem(lastCategoria);
        aplicarFiltros(lastFilterText);
    }
}
