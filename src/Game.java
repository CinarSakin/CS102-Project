// header
// 23.02.2026

package src;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import java.awt.Color;
import java.awt.Font;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.Timer;


public class Game {

    public static class fancyButton extends JButton {
        public fancyButton(String title){
            super(title);
        //    this.setFont(new Font("Cooper Black", Font.PLAIN, 15));
            this.setFont(new Font("Segoe UI Semibold Italic", Font.PLAIN, 15));     

            this.setContentAreaFilled(false);
            this.setOpaque(false);
            this.setFocusPainted(false);
            
            this.setForeground(new Color(0, 0, 0, 0.75f));

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mouseEntered(MouseEvent e) {
                    setForeground(new Color(0, 0, 0, 1f));
                }
            
                @Override
                public void mouseExited(MouseEvent e) {
                    setForeground(new Color(0, 0, 0, 0.75f));
                }
            });
        }
    }

    public static void main(String[] args) {
    
        // menu setup
        JFrame game = new JFrame("oyun");
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setSize(1280,720);

        JPanel panel = new JPanel();
        panel.setLayout(new FancyLayout());        

        fancyButton playButton = new fancyButton("Play");
        fancyButton setButton = new fancyButton("Settings");
        fancyButton exitButton = new fancyButton("Exit");

        JLabel title = new JLabel("Dungeonfall");
        title.setHorizontalAlignment(SwingConstants.CENTER);
        panel.add(title,new FancyLayout.UDim2(0.5, 0.03, 0.3, 0.08)
            .setAnchor(.5, 0)
        );

        panel.add(playButton, new FancyLayout.UDim2(0.5, 0.42, 0.2, 0.07));
        panel.add(setButton, new FancyLayout.UDim2(0.5, 0.5, 0.2, 0.07));
        panel.add(exitButton, new FancyLayout.UDim2(0.5, 0.58, 0.2, 0.07));
        game.add(panel);
        
        game.setVisible(true);

        
        // menu functions

        playButton.addActionListener(e -> {
            playButton.setText("In maintenance.");
            Timer timer = new Timer(1000, event -> {
                playButton.setText("Play");
                playButton.setEnabled(true);
            });
            timer.setRepeats(false);
            timer.start();            
        });

        setButton.addActionListener(e -> {
            setButton.setText("In maintenance.");
            Timer timer = new Timer(1000, event -> {
                setButton.setText("Settings");
                setButton.setEnabled(true);
            });
            timer.setRepeats(false);
            timer.start();            
        });

        exitButton.addActionListener(e -> {
            System.exit(0);
        });

    }
}
