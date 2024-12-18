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
import java.text.SimpleDateFormat;
import java.util.Calendar;

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
    private JPanel tablePanel;
    private JLabel noJugadorsLabel;
    private JScrollPane scrollPane;
    
    private static final SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

    //Esto es para guardar el estado de los filtros
    private String lastIdFilter = "";
    private String lastNomFilter = "";
    private String lastDataFilter = "";
    private Categoria lastCategoria = null;

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

        // filtritos de locos
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

        JPanel buttonPanel = new JPanel();
        buttonPanel.setLayout(new BorderLayout());
        buttonPanel.setBackground(new Color(230, 230, 230));

        JPanel leftButtonPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        leftButtonPanel.setBackground(new Color(230, 230, 230));
        
        JButton btnReload = new JButton("Recarregar");
        btnReload.addActionListener(e -> {
            try {
                guardarEstadoFiltros();
                jugadors = gBD.obtenirLlistaJugador();
                tableModel.setRowCount(0);
                
                if (jugadors != null && !jugadors.isEmpty()) {
                    ocultarMensajeNoJugadors();
                    for (Jugador jugador : jugadors) {
                        Object[] rowData = {
                            jugador.getIdLegal(),
                            jugador.getNom(), 
                            jugador.getCognom(),
                            jugador.getSexe(),
                            sdf.format(jugador.getData_naix()),
                            calcularEdad(jugador.getData_naix()),
                            obtenerCategoria(calcularEdad(jugador.getData_naix())),
                            jugador.getAny_fi_revisio_medica()
                        };
                        tableModel.addRow(rowData);
                    }
                    aplicarFiltros();
                } else {
                    mostrarMensajeNoJugadors();
                }
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, 
                    "Error al recarregar els jugadors: " + ex.getMessage(),
                    "Error", 
                    JOptionPane.ERROR_MESSAGE);
                infoError(ex);
            }
        });
        
        leftButtonPanel.add(btnReload);

        JPanel rightButtonPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        rightButtonPanel.setBackground(new Color(230, 230, 230));

        JButton btnAdd = new JButton("Afegir Jugador");
        JButton btnEdit = new JButton("Editar Jugador");
        JButton btnDelete = new JButton("Eliminar Jugador");

        btnAdd.addActionListener(e -> showAddPanel());
        btnEdit.addActionListener(e -> showModPanel());
        btnDelete.addActionListener(e -> eliminarJugador());

        rightButtonPanel.add(btnAdd);
        rightButtonPanel.add(btnEdit);
        rightButtonPanel.add(btnDelete);

        // Añadir los paneles al buttonPanel principal
        buttonPanel.add(leftButtonPanel, BorderLayout.WEST);
        buttonPanel.add(rightButtonPanel, BorderLayout.EAST);

        panel.add(buttonPanel, BorderLayout.SOUTH);

        panel.add(filterPanel, BorderLayout.NORTH);

        tablePanel = new JPanel(new BorderLayout());
        
        String[] columnNames = {"ID Legal", "Nom", "Cognom", "Sexe", "Data Naixement", "Edat", "Categoria", "Fi Revisió Mèdica"};
        tableModel = new DefaultTableModel(columnNames, 0);
        table = new JTable(tableModel);
        table.setPreferredScrollableViewportSize(new Dimension(300, 200));
        table.setFillsViewportHeight(true);
        table.setRowHeight(30);

        scrollPane = new JScrollPane(table);
        tablePanel.add(scrollPane, BorderLayout.CENTER);

        noJugadorsLabel = new JLabel("No existeix cap jugador", SwingConstants.CENTER);
        noJugadorsLabel.setForeground(Color.RED);
        noJugadorsLabel.setFont(new Font("SansSerif", Font.BOLD, 18));
        noJugadorsLabel.setVisible(false);
        
        panel.add(tablePanel, BorderLayout.CENTER);

        carregarJugadors();

        return panel;
    }

    private void carregarJugadors() {
        tableModel.setRowCount(0);
        try {
            jugadors = gBD.obtenirLlistaJugador();
            if (jugadors != null && !jugadors.isEmpty()) {
                ocultarMensajeNoJugadors();
                for (Jugador jugador : jugadors) {
                    Object[] rowData = {
                        jugador.getIdLegal(),
                        jugador.getNom(), 
                        jugador.getCognom(),
                        jugador.getSexe(),
                        sdf.format(jugador.getData_naix()),
                        calcularEdad(jugador.getData_naix()),
                        obtenerCategoria(calcularEdad(jugador.getData_naix())),
                        jugador.getAny_fi_revisio_medica()
                    };
                    tableModel.addRow(rowData);
                }
            } else {
                mostrarMensajeNoJugadors();
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

    private void guardarEstadoFiltros() {
        lastIdFilter = txtFilterId.getText().trim();
        lastNomFilter = txtFilterNom.getText().trim();
        lastDataFilter = txtFilterData.getText().trim();
        lastCategoria = (Categoria) cmbCategoria.getSelectedItem();
    }

    private void restaurarFiltros() {
        txtFilterId.setText(lastIdFilter);
        txtFilterNom.setText(lastNomFilter);
        txtFilterData.setText(lastDataFilter);
        cmbCategoria.setSelectedItem(lastCategoria);
        
        // Aplicar los filtros si hay alguno activo
        if (!lastIdFilter.isEmpty() || !lastNomFilter.isEmpty() || 
            !lastDataFilter.isEmpty() || lastCategoria != null) {
            aplicarFiltros();
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
            }
        }
    }

    public void showListPanel() {
        try {
            // Actualizar la lista de jugadores
            jugadors = gBD.obtenirLlistaJugador();
            
            // Limpiar y actualizar la tabla
            tableModel.setRowCount(0);
            if (jugadors != null && !jugadors.isEmpty()) {
                ocultarMensajeNoJugadors();
                for (Jugador jugador : jugadors) {
                    Object[] rowData = {
                        jugador.getIdLegal(),
                        jugador.getNom(), 
                        jugador.getCognom(),
                        jugador.getSexe(),
                        sdf.format(jugador.getData_naix()),
                        calcularEdad(jugador.getData_naix()),
                        obtenerCategoria(calcularEdad(jugador.getData_naix())),
                        jugador.getAny_fi_revisio_medica()
                    };
                    tableModel.addRow(rowData);
                }
            } else {
                mostrarMensajeNoJugadors();
            }
            
            // Mostrar el panel y restaurar filtros
            cardLayout.show(mainPanel, "listPanel");
            restaurarFiltros();
            
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this, 
                "Error al actualitzar la llista de jugadors: " + ex.getMessage(), 
                "Error", 
                JOptionPane.ERROR_MESSAGE);
            infoError(ex);
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
            
            int currentYear = Calendar.getInstance().get(Calendar.YEAR);
            Categoria categoriaActual = null;
            
            for (Categoria cat : categorias) {
                cmbCategoria.addItem(cat);
                if (cat.getEdat_minima() <= currentYear && cat.getEdat_maxima() >= currentYear) {
                    categoriaActual = cat;
                }
            }
            
            if (categoriaActual != null) {
                cmbCategoria.setSelectedItem(categoriaActual);
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
        boolean hayResultados = false;
        for (Jugador jugador : jugadors) {
            if (cumpleFiltros(jugador, filtroId, filtroNom, selectedCategoria, filtroData)) {
                actualizarFilaTabla(jugador);
                hayResultados = true;
            }
        }

        if (!hayResultados) {
            mostrarMensajeNoJugadors();
        } else {
            ocultarMensajeNoJugadors();
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
            String fechaJugador = sdf.format(jugador.getData_naix());
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
        int edad = calcularEdad(jugador.getData_naix());
        String categoria = obtenerCategoria(edad);
        
        Object[] rowData = {
            jugador.getIdLegal(),
            jugador.getNom(), 
            jugador.getCognom(),
            jugador.getSexe(),
            sdf.format(jugador.getData_naix()),
            edad,
            categoria,
            jugador.getAny_fi_revisio_medica()
        };
        tableModel.addRow(rowData);
    }

    private void actualizarTabla(List<Jugador> jugadorsOrdenados) {
        tableModel.setRowCount(0);
        for (Jugador jugador : jugadorsOrdenados) {
            actualizarFilaTabla(jugador);
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

    private void mostrarMensajeNoJugadors() {
        scrollPane.setVisible(false);
        if (noJugadorsLabel.getParent() == null) {
            tablePanel.add(noJugadorsLabel, BorderLayout.CENTER);
        }
        noJugadorsLabel.setVisible(true);
        tablePanel.revalidate();
        tablePanel.repaint();
    }

    private void ocultarMensajeNoJugadors() {
        noJugadorsLabel.setVisible(false);
        scrollPane.setVisible(true);
        tablePanel.revalidate();
        tablePanel.repaint();
    }

    private String obtenerCategoria(int edad) {
        try {
            List<Categoria> categorias = gBD.obtenirLlistaCategoria();
            for (Categoria cat : categorias) {
                if (edad >= cat.getEdat_minima() && edad <= cat.getEdat_maxima()) {
                    return cat.getCategoria();
                }
            }
            return "Sense categoria";
        } catch (Exception ex) {
            return "Error";
        }
    }
}
