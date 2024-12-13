package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import javax.swing.table.DefaultTableModel;
import org.milaifontanals.club.Jugador;
import org.milaifontanals.club.IClubOracleBD;
import java.util.List;
import org.milaifontanals.club.GestorBDClub;
import java.util.ArrayList;
import java.util.Collections;
import org.milaifontanals.club.Categoria;
import java.time.LocalDate;
import java.time.Period;
import java.util.Date;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class JugadorsPanel extends JPanel {
    private IClubOracleBD gBD;
    private List<Jugador> jugadors;
    private DefaultTableModel tableModel;
    private CardLayout cardLayout;
    private JPanel mainPanel;
    private JTable table;
    private boolean ordenadoPorApellido = false;
    private boolean ordenadoPorFecha = false;
    private JTextField txtFilterId;
    private JTextField txtFilterNom;
    private JTextField txtFilterData;
    private JComboBox<Categoria> cmbCategoria;
    private JButton btnSortByCognom;
    private JButton btnSortByData;

    public JugadorsPanel(IClubOracleBD gBD) {
        this.gBD = gBD;
        setLayout(new BorderLayout(20, 20));
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        cardLayout = new CardLayout();
        mainPanel = new JPanel(cardLayout);

        JPanel listPanel = createListPanel();
        JPanel addPanel = new ModJugadorWindow(gBD, mainPanel, "listPanel", null);

        mainPanel.add(listPanel, "listPanel");
        mainPanel.add(addPanel, "addPanel");

        add(mainPanel, BorderLayout.CENTER);
        showListPanel();
    }

    private JPanel createListPanel() {
        JPanel panel = new JPanel(new BorderLayout(10, 10));

        //filtritos
        JPanel filterPanel = new JPanel();
        filterPanel.setLayout(new FlowLayout(FlowLayout.LEFT));
        filterPanel.setBackground(new Color(230, 230, 230));

        txtFilterId = new JTextField(10);
        txtFilterNom = new JTextField(10);
        txtFilterData = new JTextField(10);
        JButton btnApplyFilter = new JButton("Aplicar Filtres");
        cmbCategoria = new JComboBox<>();
        JButton btnFilterByCategory = new JButton("Filtrar per Categoria");
        btnSortByCognom = new JButton("Ordenar per Cognom");
        btnSortByData = new JButton("Ordenar per Data Naixement");

        cargarCategorias();

        btnApplyFilter.addActionListener(e -> aplicarFiltros());
        btnFilterByCategory.addActionListener(e -> aplicarFiltros());
        
        btnSortByCognom.addActionListener(e -> ordenarPorCognom());
        btnSortByData.addActionListener(e -> ordenarPorData());

        KeyAdapter keyAdapter = new KeyAdapter() {
            @Override
            public void keyPressed(KeyEvent evt) {
                if (evt.getKeyCode() == KeyEvent.VK_ESCAPE) {
                    limpiarFiltros();
                } else if (evt.getKeyCode() == KeyEvent.VK_ENTER) {
                    aplicarFiltros();
                }
            }
        };

        txtFilterId.addKeyListener(keyAdapter);
        txtFilterNom.addKeyListener(keyAdapter);
        txtFilterData.addKeyListener(keyAdapter);

        filterPanel.add(new JLabel("ID Legal:"));
        filterPanel.add(txtFilterId);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Nom:"));
        filterPanel.add(txtFilterNom);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(new JLabel("Data Naixement:"));
        filterPanel.add(txtFilterData);
        filterPanel.add(Box.createHorizontalStrut(10));
        filterPanel.add(btnApplyFilter);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(new JLabel("Categoria:"));
        filterPanel.add(cmbCategoria);
        filterPanel.add(btnFilterByCategory);
        filterPanel.add(Box.createHorizontalStrut(20));
        filterPanel.add(btnSortByCognom);
        filterPanel.add(btnSortByData);

        panel.add(filterPanel, BorderLayout.NORTH);

        String[] columnNames = {"ID Legal", "Nom", "Cognom", "Sexe", "Data Naixement", "Fi Revisió Mèdica"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);

        table.setPreferredScrollableViewportSize(new Dimension(300, 200));
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

        JScrollPane scrollPane = new JScrollPane(table);
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new FlowLayout(FlowLayout.RIGHT));
        buttonPanel.setBackground(new Color(230, 230, 230));

        JButton btnAdd = new JButton("Afegir Jugador");
        JButton btnEdit = new JButton("Editar Jugador");
        JButton btnDelete = new JButton("Eliminar Jugador");

        btnAdd.addActionListener(e -> showAddPanel());
        btnEdit.addActionListener(e -> showModPanel());
        btnDelete.addActionListener(e -> eliminarJugador());

        buttonPanel.add(btnAdd);
        buttonPanel.add(btnEdit);
        buttonPanel.add(btnDelete);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        carregarJugadors();

        return panel;
    }

    private void carregarJugadors() {
        tableModel.setRowCount(0);
        try {
            jugadors = gBD.obtenirLlistaJugador();
            for (Jugador jugador : jugadors) {
                Object[] rowData = {
                    jugador.getIdLegal(),
                    jugador.getNom(), 
                    jugador.getCognom(),
                    jugador.getSexe(),
                    jugador.getData_naix(),
                    jugador.getAny_fi_revisio_medica()
                };
                tableModel.addRow(rowData);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar els jugadors: " + ex.getMessage(), 
                "Error", JOptionPane.ERROR_MESSAGE);
            infoError(ex);
        }
    }

    private void eliminarJugador() {
        int selectedRow = table.getSelectedRow();
        if (selectedRow != -1) {
            String idLegal = (String) tableModel.getValueAt(selectedRow, 0);
            Jugador jugadorSeleccionado = null;
            
            for (Jugador j : jugadors) {
                if (j.getIdLegal().equals(idLegal)) {
                    jugadorSeleccionado = j;
                    break;
                }
            }

            if (jugadorSeleccionado != null) {
                int opcion = JOptionPane.showConfirmDialog(this, 
                    "Estàs segur que vols eliminar el jugador " + jugadorSeleccionado.getNom() + 
                    " " + jugadorSeleccionado.getCognom() + "?", 
                    "Confirmació d'eliminació", 
                    JOptionPane.YES_NO_OPTION, 
                    JOptionPane.WARNING_MESSAGE);

                if (opcion == JOptionPane.YES_OPTION) {
                    try {
                        gBD.esborrarJugador(jugadorSeleccionado.getId());
                        gBD.confirmarCanvis();
                        JOptionPane.showMessageDialog(this, "Jugador esborrat correctament!", 
                            "Èxit", JOptionPane.INFORMATION_MESSAGE);
                        carregarJugadors();
                    } catch (GestorBDClub ex) {
                        JOptionPane.showMessageDialog(this, "Error al eliminar el jugador: " + 
                            ex.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                        infoError(ex);
                    }
                }
            }
        }
    }

    private void showAddPanel() {
        cardLayout.show(mainPanel, "addPanel");
        carregarJugadors();
    }

    private void showModPanel() {
        int selectedRow = table.getSelectedRow();
        if(selectedRow != -1){
            String idLegal = (String) tableModel.getValueAt(selectedRow, 0);
            Jugador jugadorSeleccionado = null;
            
            for (Jugador j : jugadors) {
                if (j.getIdLegal().equals(idLegal)) {
                    jugadorSeleccionado = j;
                    break;
                }
            }
            
            if (jugadorSeleccionado != null) {
                JPanel modPanel = new ModJugadorWindow(gBD, mainPanel, "listPanel", jugadorSeleccionado);
                mainPanel.add(modPanel, "modPanel");
                cardLayout.show(mainPanel, "modPanel");
                carregarJugadors();
            }
        }
    }

    public void showListPanel() {
        carregarJugadors();
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

    private int calcularEdad(Date fechaNacimiento) {
        if (fechaNacimiento == null) return 0;
        
        // Convertir java.util.Date a LocalDate
        LocalDate fechaNac = new java.sql.Date(fechaNacimiento.getTime()).toLocalDate();
        LocalDate ahora = LocalDate.now();
        
        return Period.between(fechaNac, ahora).getYears();
    }

    private void cargarCategorias() {
        try {
            List<Categoria> categorias = gBD.obtenirLlistaCategoria();
            cmbCategoria.addItem(null);
            for (Categoria cat : categorias) {
                cmbCategoria.addItem(cat);
            }
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, "Error al carregar les categories: " + ex.getMessage(),
                "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void ordenarPorCognom() {
        if (!ordenadoPorApellido) {
            List<Jugador> jugadorsOrdenados = new ArrayList<>(jugadors);
            jugadorsOrdenados = aplicarFiltrosLista(jugadorsOrdenados);
            Collections.sort(jugadorsOrdenados, (j1, j2) -> 
                j1.getCognom().compareToIgnoreCase(j2.getCognom()));
            actualizarTabla(jugadorsOrdenados);
            btnSortByCognom.setText("Treure ordre Cognom");
        } else {
            aplicarFiltros();
            btnSortByCognom.setText("Ordenar per Cognom");
        }
        ordenadoPorApellido = !ordenadoPorApellido;
    }

    private void ordenarPorData() {
        if (!ordenadoPorFecha) {
            List<Jugador> jugadorsOrdenados = new ArrayList<>(jugadors);
            jugadorsOrdenados = aplicarFiltrosLista(jugadorsOrdenados);
            Collections.sort(jugadorsOrdenados, (j1, j2) -> 
                j1.getData_naix().compareTo(j2.getData_naix()));
            actualizarTabla(jugadorsOrdenados);
            btnSortByData.setText("Treure ordre Data");
        } else {
            aplicarFiltros();
            btnSortByData.setText("Ordenar per Data Naixement");
        }
        ordenadoPorFecha = !ordenadoPorFecha;
    }

    private void aplicarFiltros() {
        String filtroId = txtFilterId.getText().trim().toLowerCase();
        String filtroNom = txtFilterNom.getText().trim().toLowerCase();
        String filtroData = txtFilterData.getText().trim();
        Categoria selectedCategoria = (Categoria) cmbCategoria.getSelectedItem();

        if (filtroId.isEmpty() && filtroNom.isEmpty() && selectedCategoria == null && filtroData.isEmpty()) {
            carregarJugadors();
            return;
        }

        tableModel.setRowCount(0);
        for (Jugador jugador : jugadors) {
            if (cumpleFiltros(jugador, filtroId, filtroNom, selectedCategoria, filtroData)) {
                actualizarFilaTabla(jugador);
            }
        }
    }

    private boolean cumpleFiltros(Jugador jugador, String filtroId, String filtroNom, 
        Categoria selectedCategoria, String filtroData) {
        boolean matchId = filtroId.isEmpty() || 
            jugador.getIdLegal().toLowerCase().contains(filtroId);
        boolean matchNom = filtroNom.isEmpty() || 
            jugador.getNom().toLowerCase().contains(filtroNom);
        boolean matchCategoria = true;
        boolean matchData = true;

        if (selectedCategoria != null) {
            int edad = calcularEdad(jugador.getData_naix());
            matchCategoria = edad >= selectedCategoria.getEdat_minima()&& 
                            edad <= selectedCategoria.getEdat_maxima();
        }

        if (!filtroData.isEmpty()) {
            String fechaJugador = new java.text.SimpleDateFormat("dd/MM/yyyy")
                .format(jugador.getData_naix());
            matchData = fechaJugador.contains(filtroData);
        }

        return matchId && matchNom && matchCategoria && matchData;
    }

    private void limpiarFiltros() {
        txtFilterId.setText("");
        txtFilterNom.setText("");
        txtFilterData.setText("");
        cmbCategoria.setSelectedItem(null);
        carregarJugadors();
    }

    private void actualizarFilaTabla(Jugador jugador) {
        Object[] rowData = {
            jugador.getIdLegal(),
            jugador.getNom(), 
            jugador.getCognom(),
            jugador.getSexe(),
            jugador.getData_naix(),
            jugador.getAny_fi_revisio_medica()
        };
        tableModel.addRow(rowData);
    }

    private void actualizarTabla(List<Jugador> jugadorsOrdenados) {
        tableModel.setRowCount(0);
        for (Jugador jugador : jugadorsOrdenados) {
            Object[] rowData = {
                jugador.getIdLegal(),
                jugador.getNom(), 
                jugador.getCognom(),
                jugador.getSexe(),
                jugador.getData_naix(),
                jugador.getAny_fi_revisio_medica()
            };
            tableModel.addRow(rowData);
        }
    }

    private List<Jugador> aplicarFiltrosLista(List<Jugador> listaJugadores) {
        String filtroId = txtFilterId.getText().trim().toLowerCase();
        String filtroNom = txtFilterNom.getText().trim().toLowerCase();
        String filtroData = txtFilterData.getText().trim();
        Categoria selectedCategoria = (Categoria) cmbCategoria.getSelectedItem();

        if (filtroId.isEmpty() && filtroNom.isEmpty() && selectedCategoria == null && filtroData.isEmpty()) {
            return listaJugadores;
        }

        return listaJugadores.stream()
            .filter(jugador -> cumpleFiltros(jugador, filtroId, filtroNom, selectedCategoria, filtroData))
            .collect(java.util.stream.Collectors.toList());
    }
}
