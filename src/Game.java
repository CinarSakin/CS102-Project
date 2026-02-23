// header
// 23.02.2026

package src;
import javax.swing.Icon;
import javax.swing.ImageIcon;
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

    public static ImageIcon btn_default = new ImageIcon("assets/buttonDefault.png");
    public static ImageIcon btn_pressed = new ImageIcon("assets/buttonPressed.png");    

    public static class fancyButton extends JButton {
        private java.awt.Image backgroundImage;
        private boolean pressed = false;
        public fancyButton(String title){
            super(title);
        //    this.setFont(new Font("Cooper Black", Font.PLAIN, 15));
        //    this.setFont(new Font("Segoe UI Semibold Italic", Font.PLAIN, 15));     
            this.setFont(new Font("ByteBounce", Font.PLAIN, 15));

            this.setContentAreaFilled(false);
            this.setOpaque(false);
            this.setFocusPainted(false);
            this.setBorderPainted(false);

            this.setForeground(new Color(0,0,0));

            backgroundImage = btn_default.getImage();

            this.setHorizontalTextPosition(SwingConstants.CENTER); // Yazıyı yatayda merkeze al
            this.setVerticalTextPosition(SwingConstants.CENTER);   // Yazıyı dikeyde merkeze al

            this.addMouseListener(new MouseAdapter() {
                @Override
                public void mousePressed(MouseEvent e) {
                //    setForeground(new Color(0, 0, 0, 1f));
                    backgroundImage = btn_pressed.getImage();
                    pressed = true;
                }
            
                @Override
                public void mouseReleased(MouseEvent e) {
                //    setForeground(new Color(0, 0, 0, 1f));
                    backgroundImage = btn_default.getImage();
                    pressed = false;
                }
            });
        }
        protected void paintComponent(java.awt.Graphics g) {
        //    if (backgroundImage != null) {
        //        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        //    }
        //    super.paintComponent(g);


            if (backgroundImage != null) {
            //    int x = getWidth();
            //    int y = getHeight();
            //    g.drawImage(backgroundImage, (int)(x*.1), (int)(y*.1), (int)(x*.8), (int)(y*.8), this);
                g.drawImage(backgroundImage, 0,0, getWidth(), getHeight(), this);
            }

            int shiftAmount;
            if (pressed){
                shiftAmount = -getHeight() / 12;
            }
            else{
                shiftAmount = getHeight() / 24;
            }

            g.translate(0, -shiftAmount); 

            super.paintComponent(g);

            g.translate(0, shiftAmount);

        }
    }

    public static void main(String[] args) {
    
        try {
            Font customFont = Font.createFont(Font.TRUETYPE_FONT, new java.io.File("assets/ByteBounce.ttf"));
            java.awt.GraphicsEnvironment ge = java.awt.GraphicsEnvironment.getLocalGraphicsEnvironment();
            ge.registerFont(customFont);
        } catch (java.awt.FontFormatException e) {
            System.err.println("Font formatı hatalı!");
        } catch (java.io.IOException e) {
            System.err.println("Font dosyası bulunamadı! Yolun doğru olduğundan emin ol.");
        }

        // menu setup
        JFrame game = new JFrame("oyun");
        game.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        game.setSize(1280,900);

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

        panel.add(playButton, new FancyLayout.UDim2(0.5, 0.41, 0.2, 0.07));
        panel.add(setButton, new FancyLayout.UDim2(0.5, 0.5, 0.2, 0.07));
        panel.add(exitButton, new FancyLayout.UDim2(0.5, 0.59, 0.2, 0.07));
        game.add(panel);
        
        game.setVisible(true);

        
        // menu functions

        playButton.addActionListener(e -> {
            playButton.setText("WIP");
            Timer timer = new Timer(1000, event -> {
                playButton.setText("Play");
                playButton.setEnabled(true);
            });
            timer.setRepeats(false);
            timer.start();            
        });

        setButton.addActionListener(e -> {
            setButton.setText("WIP");
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
