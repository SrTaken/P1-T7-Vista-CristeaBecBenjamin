package p1.t7.vista.cristeabecbenjamin;

import java.awt.*;
import javax.swing.*;
import org.milaifontanals.club.IClubOracleBD;

public class MainMenu extends JFrame {
    private IClubOracleBD gBD;
    private JPanel contentPanel;
    private CardLayout cardLayout;
    
    public MainMenu(IClubOracleBD gBD) {
        this.gBD = gBD;
        setTitle("Menú Principal");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setExtendedState(Frame.MAXIMIZED_BOTH);
        setLocationRelativeTo(null);
        
        UIManager.put("Button.font", new Font("SansSerif", Font.BOLD, 14));
        UIManager.put("Panel.background", Color.LIGHT_GRAY);

        cardLayout = new CardLayout();
        contentPanel = new JPanel(cardLayout);
        

        
        JPanel welcomePanel = crearWelcomePanel();
        contentPanel.add(welcomePanel, "welcome");
        
        JPanel equipsPanel = new EquipsWindow(gBD);
        contentPanel.add(equipsPanel, "equips");
        
        setLayout(new BorderLayout());

        sidePanel();

        setVisible(true);
    }

    private JPanel crearWelcomePanel() {
        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        JLabel lblWelcome = new JLabel("Benvingut/da al gestor de clubs esportius del gran Benjamin Cristea!", SwingConstants.CENTER);
        lblWelcome.setFont(new Font("SansSerif", Font.BOLD, 24));
        contentPanel.add(lblWelcome, BorderLayout.CENTER);
        return contentPanel;
    }
    
    private void sidePanel() {
        JPanel sideMenu = new JPanel();
        sideMenu.setLayout(new GridLayout(0, 1, 5, 5));
        sideMenu.setBackground(new Color(54, 57, 63));
        sideMenu.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton btnEquips = crearBoton("Equips");
        JButton btnJugadors = crearBoton("Jugadors");
        JButton btnTemp = crearBoton("Temporada");
        JButton btnCategoria = crearBoton("Categoria");
        JButton btnUsuari = crearBoton("Usuari");
        JButton btnSortir = crearBoton("Sortir");

        sideMenu.add(btnEquips);
        sideMenu.add(btnJugadors);
        sideMenu.add(btnTemp);
        sideMenu.add(btnCategoria);
        sideMenu.add(btnUsuari);
        sideMenu.add(new JLabel()); // Espacio en blanco como el view en android
        sideMenu.add(btnSortir);

        
        add(sideMenu, BorderLayout.WEST);
        add(contentPanel, BorderLayout.CENTER);

        btnEquips.addActionListener(e -> cardLayout.show(contentPanel, "equips"));
        btnJugadors.addActionListener(e -> mostrarContenido(contentPanel, "Gestió de Jugadors"));
        btnTemp.addActionListener(e -> mostrarContenido(contentPanel, "Gestió de Partits"));
        btnCategoria.addActionListener(e -> mostrarContenido(contentPanel, "Estadístiques"));
        btnUsuari.addActionListener(e -> mostrarContenido(contentPanel, "Configuració"));
        btnSortir.addActionListener(e -> System.exit(0)); 
    }

    private JButton crearBoton(String text) {
        JButton btn = new JButton(text);
        btn.setBackground(new Color(47, 49, 54));
        btn.setForeground(Color.WHITE);
        btn.setFocusPainted(false);
        btn.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20)); 
        btn.setPreferredSize(new Dimension(150, 40));
        return btn;
    }

    private void mostrarContenido(JPanel contentPanel, String mensaje) {
        contentPanel.removeAll();
        JLabel lbl = new JLabel(mensaje, SwingConstants.CENTER);
        lbl.setFont(new Font("SansSerif", Font.BOLD, 24));
        contentPanel.add(lbl, BorderLayout.CENTER);
        contentPanel.revalidate();
        contentPanel.repaint();
    }

    
}
