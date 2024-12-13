package p1.t7.vista.cristeabecbenjamin;

import javax.swing.*;
import java.awt.*;
import org.milaifontanals.club.IClubOracleBD;

public class JugadorsPanel extends JPanel {
    public JugadorsPanel(IClubOracleBD gBD) {
        setLayout(new BorderLayout());

        JLabel lblJugadors = new JLabel("Gestió de Jugadors", SwingConstants.CENTER);
        lblJugadors.setFont(new Font("SansSerif", Font.BOLD, 24));
        add(lblJugadors, BorderLayout.CENTER);
    }
}
