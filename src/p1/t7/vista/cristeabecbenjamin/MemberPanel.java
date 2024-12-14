package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import java.util.List;
import javax.swing.table.DefaultTableModel;
import org.milaifontanals.club.*;
import java.util.Calendar;
import java.util.Set;
import java.util.HashSet;
import javax.swing.table.DefaultTableCellRenderer;

public class MemberPanel extends JPanel {
    private IClubOracleBD gBD;
    private Equip equip;
    private JTable taulaJugadorsDisponibles;
    private JTable taulaMembers;
    private DefaultTableModel modelJugadorsDisponibles;
    private DefaultTableModel modelMembers;
    private Set<Integer> jugadorsModificats = new HashSet<>();
    
    public MemberPanel(IClubOracleBD gBD, Equip equip) {
        this.gBD = gBD;
        this.equip = equip;
        setupUI();
        carregarDades();
    }
    
    private void setupUI() {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        setPreferredSize(new Dimension(800, 400));
        
        JPanel mainPanel = new JPanel(new BorderLayout());
        JPanel centralPanel = new JPanel(new GridLayout(1, 2, 10, 0));
        
        String[] columnasDisponibles = {"ID", "Nom", "Cognoms", "Edat", "Titular"};
        modelJugadorsDisponibles = new DefaultTableModel(columnasDisponibles, 0) {
            @Override
            public Class<?> getColumnClass(int column) {
                return column == 4 ? Boolean.class : Object.class;
            }
            
            @Override
            public boolean isCellEditable(int row, int column) {
                if (column == 4) {
                    try {
                        int idJugador = (int) getValueAt(row, 0);
                        Jugador j = gBD.obtenirJugador(idJugador);
                        return !esTitularEnAltreEquip(j);
                    } catch (Exception ex) {
                        return false;
                    }
                }
                return false;
            }
        };
        
        taulaJugadorsDisponibles = new JTable(modelJugadorsDisponibles);
        
        taulaJugadorsDisponibles.getColumnModel().getColumn(4).setCellRenderer(new DefaultTableCellRenderer() {
            private JCheckBox checkbox = new JCheckBox();
            
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                    boolean isSelected, boolean hasFocus, int row, int column) {
                
                try {
                    int idJugador = (int) table.getValueAt(row, 0);
                    Jugador j = gBD.obtenirJugador(idJugador);
                    boolean esTitular = esTitularEnAltreEquip(j);
                    
                    checkbox.setSelected(value != null && (Boolean) value);
                    checkbox.setEnabled(!esTitular);
                    checkbox.setBackground(esTitular ? new Color(220, 220, 220) : Color.WHITE);
                    return checkbox;
                    
                } catch (Exception ex) {
                    return super.getTableCellRendererComponent(table, value, isSelected, hasFocus, row, column);
                }
            }
        });
        
        JScrollPane scrollPaneDisponibles = new JScrollPane(taulaJugadorsDisponibles);
        
        JPanel leftPanel = new JPanel(new BorderLayout());
        leftPanel.setBorder(BorderFactory.createTitledBorder("Jugadors Disponibles"));
        leftPanel.add(scrollPaneDisponibles, BorderLayout.CENTER);
        
        String[] columnasMembers = {"ID", "Nom", "Cognoms", "Titular"};
        modelMembers = new DefaultTableModel(columnasMembers, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        
        taulaMembers = new JTable(modelMembers);
        JScrollPane scrollPaneMembers = new JScrollPane(taulaMembers);
        
        JPanel rightPanel = new JPanel(new BorderLayout());
        rightPanel.setBorder(BorderFactory.createTitledBorder("Membres de l'Equip"));
        rightPanel.add(scrollPaneMembers, BorderLayout.CENTER);
        
        JPanel arrowPanel = new JPanel();
        arrowPanel.setLayout(new BoxLayout(arrowPanel, BoxLayout.Y_AXIS));
        arrowPanel.setBorder(BorderFactory.createEmptyBorder(20, 5, 20, 5));
        
        JButton btnAdd = new JButton("→");
        JButton btnRemove = new JButton("←");
        
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);
        btnRemove.setAlignmentX(Component.CENTER_ALIGNMENT);
        
        arrowPanel.add(Box.createVerticalGlue());
        arrowPanel.add(btnAdd);
        arrowPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        arrowPanel.add(btnRemove);
        arrowPanel.add(Box.createVerticalGlue());
        
        JPanel bottomPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        JButton btnGuardar = new JButton("Guardar");
        JButton btnCancelar = new JButton("Cancelar");
        bottomPanel.add(btnGuardar);
        bottomPanel.add(btnCancelar);
        
        centralPanel.add(leftPanel);
        centralPanel.add(rightPanel);
        mainPanel.add(centralPanel, BorderLayout.CENTER);
        mainPanel.add(arrowPanel, BorderLayout.EAST);
        
        add(mainPanel, BorderLayout.CENTER);
        add(bottomPanel, BorderLayout.SOUTH);
        
        btnAdd.addActionListener(e -> {
            int selectedRow = taulaJugadorsDisponibles.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    int idJugador = (int) modelJugadorsDisponibles.getValueAt(selectedRow, 0);
                    String nom = (String) modelJugadorsDisponibles.getValueAt(selectedRow, 1);
                    String cognom = (String) modelJugadorsDisponibles.getValueAt(selectedRow, 2);
                    boolean isTitular = (Boolean) modelJugadorsDisponibles.getValueAt(selectedRow, 4);
                    
                    Jugador j = gBD.obtenirJugador(idJugador);
                    Membre m = new Membre(j, equip, 'C');  
                    
                    if (isTitular) {
                        m.setTitular_convidat('T');
                    }
                    
                    gBD.afegirMembre(m);
                    
                    modelMembers.addRow(new Object[]{
                        idJugador,
                        nom,
                        cognom,
                        isTitular ? "Sí" : "No"  
                    });
                    modelJugadorsDisponibles.removeRow(selectedRow);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al afegir membre: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    infoError(ex);
                }
            }
        });

        btnRemove.addActionListener(e -> {
            int selectedRow = taulaMembers.getSelectedRow();
            if (selectedRow != -1) {
                try {
                    int idJugador = (int) modelMembers.getValueAt(selectedRow, 0);
                    Jugador j = gBD.obtenirJugador(idJugador);
                    
                    gBD.esborrarMembre(idJugador, equip.getId());
                    
                    modelJugadorsDisponibles.addRow(new Object[]{
                        idJugador,
                        j.getNom(),
                        j.getCognom(),
                        calcularEdat(j),
                        false
                    });
                    modelMembers.removeRow(selectedRow);
                    
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, 
                        "Error al treure membre: " + ex.getMessage(),
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                    infoError(ex);
                }
            }
        });

        btnGuardar.addActionListener(e -> {
            try {
                gBD.confirmarCanvis();
                JOptionPane.showMessageDialog(this, 
                    "Canvis guardats correctament",
                    "Èxit",
                    JOptionPane.INFORMATION_MESSAGE);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al guardar els canvis: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });

        btnCancelar.addActionListener(e -> {
            try {
                gBD.desferCanvis();
                Window window = SwingUtilities.getWindowAncestor(this);
                if (window instanceof JDialog) {
                    ((JDialog) window).dispose();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, 
                    "Error al cancel·lar els canvis: " + ex.getMessage(),
                    "Error",
                    JOptionPane.ERROR_MESSAGE);
            }
        });
    }
    
    private void carregarDades() {
        try {
            modelJugadorsDisponibles.setRowCount(0);
            List<Jugador> jugadors = gBD.obtenirLlistaJugador();
            List<Membre> membresActuals = gBD.obtenirLlistaMembre(equip.getId());
            
            Set<Integer> idsMembresActuals = new HashSet<>();
            for (Membre m : membresActuals) {
                idsMembresActuals.add(m.getJ().getId());
            }
            
            for (Jugador j : jugadors) {
                if (compleixRequisits(j) && !idsMembresActuals.contains(j.getId())) {
                    modelJugadorsDisponibles.addRow(new Object[]{
                        j.getId(),
                        j.getNom(),
                        j.getCognom(),
                        calcularEdat(j),
                        false  
                    });
                }
            }
            
            modelMembers.setRowCount(0);
            for (Membre m : membresActuals) {
                Jugador j = m.getJ();
                modelMembers.addRow(new Object[]{
                    j.getId(),
                    j.getNom(),
                    j.getCognom(),
                    m.getTitular_convidat() == 'T' ? "Sí" : "No"
                });
            }
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al carregar dades: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }
    
    private boolean compleixRequisits(Jugador j) {
         if ((equip.getTipus() == Tipus.D && j.getSexe() != Sexe.D) || 
            (equip.getTipus() == Tipus.H && j.getSexe() != Sexe.H)) {
            return false;
        }
        int edat = calcularEdat(j);
        int edatMinima = equip.getCategoria().getEdat_minima();
        int edatMaxima = equip.getCategoria().getEdat_maxima();
        
        return !(edat >= edatMaxima);
    }
    
    private int calcularEdat(Jugador j) {
        Calendar calNaixement = Calendar.getInstance();
        calNaixement.setTime(j.getData_naix());

        int anyNaixement = calNaixement.get(Calendar.YEAR);
        int mesNaixement = calNaixement.get(Calendar.MONTH);  
        int diaNaixement = calNaixement.get(Calendar.DAY_OF_MONTH);

        Calendar calActual = Calendar.getInstance();
        int anyActual = calActual.get(Calendar.YEAR);
        int mesActual = calActual.get(Calendar.MONTH); 
        int diaActual = calActual.get(Calendar.DAY_OF_MONTH);

        int edad = anyActual - anyNaixement;

        if (mesActual < mesNaixement || (mesActual == mesNaixement && diaActual < diaNaixement)) {
            edad--;
        }

        return edad;
    }
    
    private boolean esTitularEnAltreEquip(Jugador j) throws GestorBDClub {
        List<Equip> equips = gBD.obtenirLlistaEquip();
        for (Equip e : equips) {
            if (e.getId() != equip.getId()) {  
                List<Membre> membres = gBD.obtenirLlistaMembre(e.getId());
                for (Membre m : membres) {
                    if (m.getJ().getId() == j.getId() && m.getTitular_convidat() == 'T') {
                        return true;  
                    }
                }
            }
        }
        return false;
    }
    
    private void handleTitularCheckboxChange(int row, boolean isSelected) {
        int idJugador = (int) modelJugadorsDisponibles.getValueAt(row, 0);
        try {
            Jugador j = gBD.obtenirJugador(idJugador);
            Membre m = new Membre(j, equip, isSelected ? 'T' : 'C');
            gBD.afegirMembre(m);
            gBD.confirmarCanvis();
            carregarDades();
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al modificar l'estat del jugador: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
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
