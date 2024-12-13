package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import org.milaifontanals.club.IClubOracleBD;
import org.milaifontanals.club.Temporada;
import java.util.List;
import javax.swing.border.EmptyBorder;

public class TempWindow extends JPanel {
    private IClubOracleBD gBD;
    private JList<Temporada> temporadaList;
    private DefaultListModel<Temporada> listModel;
    private JTextField txtNewTemp;
    
    public TempWindow(IClubOracleBD gBD) {
        this.gBD = gBD;
        setLayout(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(20, 20, 20, 20);

        JPanel mainPanel = new JPanel(new GridLayout(1, 2, 40, 0));
        mainPanel.setPreferredSize(new Dimension(600, 300));

        JPanel leftPanel = new JPanel(new BorderLayout(10, 10));
        leftPanel.setBorder(createTitledBorder("Temporades Existents"));

        listModel = new DefaultListModel<>();
        temporadaList = new JList<>(listModel);
        temporadaList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        temporadaList.setFont(new Font("SansSerif", Font.PLAIN, 14));
        temporadaList.setFixedCellHeight(25);

        JScrollPane scrollPane = new JScrollPane(temporadaList);
        scrollPane.setPreferredSize(new Dimension(150, 200));

        JButton btnDelete = new JButton("Eliminar");
        btnDelete.setBackground(new Color(178, 34, 34));
        btnDelete.setForeground(Color.WHITE);
        btnDelete.setFocusPainted(false);
        btnDelete.setPreferredSize(new Dimension(100, 30));

        JPanel deleteButtonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        deleteButtonPanel.add(btnDelete);

        leftPanel.add(scrollPane, BorderLayout.CENTER);
        leftPanel.add(deleteButtonPanel, BorderLayout.SOUTH);

        JPanel rightPanel = new JPanel(new BorderLayout(10, 10));
        rightPanel.setBorder(createTitledBorder("Afegir Nova Temporada"));

        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new BoxLayout(inputPanel, BoxLayout.Y_AXIS));

        JPanel fieldPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        JLabel lblNewTemp = new JLabel("Any (YY):");
        txtNewTemp = new JTextField(5);
        txtNewTemp.setFont(new Font("SansSerif", Font.PLAIN, 14));
        txtNewTemp.setMaximumSize(new Dimension(60, 25));

        fieldPanel.add(lblNewTemp);
        fieldPanel.add(txtNewTemp);

        JButton btnAdd = new JButton("Afegir");
        btnAdd.setBackground(new Color(46, 139, 87));
        btnAdd.setForeground(Color.WHITE);
        btnAdd.setFocusPainted(false);
        btnAdd.setPreferredSize(new Dimension(100, 30));
        btnAdd.setAlignmentX(Component.CENTER_ALIGNMENT);

        inputPanel.add(Box.createVerticalGlue());
        inputPanel.add(fieldPanel);
        inputPanel.add(Box.createRigidArea(new Dimension(0, 10)));
        inputPanel.add(btnAdd);
        inputPanel.add(Box.createVerticalGlue());

        rightPanel.add(inputPanel, BorderLayout.CENTER);

        mainPanel.add(leftPanel);
        mainPanel.add(rightPanel);

        add(mainPanel, gbc);

        btnAdd.addActionListener(e -> {
            String input = txtNewTemp.getText().trim();
            if (!input.isEmpty()) {
                try {
                    int year = Integer.parseInt(input);
                    if (year >= 0 && year <= 99) {
                        try {
                            gBD.afegirTemporada(new Temporada(year));
                            gBD.confirmarCanvis();
                            refreshList();
                            txtNewTemp.setText("");
                        } catch (Exception ex) {
                            JOptionPane.showMessageDialog(this,
                                "Error al afegir la temporada: " + ex.getMessage(),
                                "Error",
                                JOptionPane.ERROR_MESSAGE);
                        }
                    } else {
                        JOptionPane.showMessageDialog(this,
                            "L'any ha d'estar entre 00 i 99",
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(this,
                        "L'any ha de ser un número",
                        "Error",
                        JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
        btnDelete.addActionListener(e -> {
            Temporada selected = temporadaList.getSelectedValue();
            if (selected != null) {
                int confirm = JOptionPane.showConfirmDialog(this,
                    "Estàs segur que vols eliminar la temporada " + selected.getYear() + "?",
                    "Confirmar eliminació",
                    JOptionPane.YES_NO_OPTION);
                
                if (confirm == JOptionPane.YES_OPTION) {
                    try {
                        gBD.esborrarTemporada(selected.getYear());
                        gBD.confirmarCanvis();
                        refreshList();
                    } catch (Exception ex) {
                        JOptionPane.showMessageDialog(this,
                            "Error al eliminar la temporada: " + ex.getMessage(),
                            "Error",
                            JOptionPane.ERROR_MESSAGE);
                    }
                }
            } else {
                JOptionPane.showMessageDialog(this,
                    "Selecciona una temporada per eliminar",
                    "Avís",
                    JOptionPane.WARNING_MESSAGE);
            }
        });

        refreshList();
    }
    
    private void refreshList() {
        try {
            listModel.clear();
            List<Temporada> temporadas = gBD.obtenirLlistaTemporada();
            temporadas.forEach(listModel::addElement);
        } catch (Exception ex) {
            JOptionPane.showMessageDialog(this,
                "Error al carregar les temporades: " + ex.getMessage(),
                "Error",
                JOptionPane.ERROR_MESSAGE);
        }
    }

    private javax.swing.border.TitledBorder createTitledBorder(String title) {
        javax.swing.border.TitledBorder border = BorderFactory.createTitledBorder(
            BorderFactory.createLineBorder(new Color(200, 200, 200)),
            title,
            javax.swing.border.TitledBorder.CENTER,
            javax.swing.border.TitledBorder.TOP,
            new Font("SansSerif", Font.BOLD, 12),
            new Color(70, 70, 70)
        );
        border.setTitlePosition(javax.swing.border.TitledBorder.ABOVE_TOP);
        return border;
    }
}
