import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.RoundRectangle2D;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WaifuHub extends JFrame implements ActionListener {
    private JLabel imageLabel;
    private JButton regenerateButton;
    private static final int IMAGE_WIDTH = 400;
    private static final int IMAGE_HEIGHT = 600;

    public WaifuHub() {
        setTitle("Waifu Hub");
        setSize(800, 700);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JLabel headerLabel = new JLabel("Welcome to Waifu Hub", JLabel.CENTER);
        headerLabel.setFont(new Font("Serif", Font.BOLD, 24));
        headerLabel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        add(headerLabel, BorderLayout.NORTH);

        imageLabel = new JLabel();
        imageLabel.setHorizontalAlignment(JLabel.CENTER);
        imageLabel.setVerticalAlignment(JLabel.CENTER);
        imageLabel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));

        JPanel imagePanel = new JPanel() {
            @Override
            protected void paintComponent(Graphics g) {
                super.paintComponent(g);
                Graphics2D g2d = (Graphics2D) g;
                g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
                g2d.setColor(Color.GRAY);
                g2d.fillRoundRect(10, 10, getWidth() - 20, getHeight() - 20, 30, 30);
            }
        };
        imagePanel.setLayout(new BorderLayout());
        imagePanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        imagePanel.setPreferredSize(new Dimension(IMAGE_WIDTH, IMAGE_HEIGHT));
        imagePanel.add(imageLabel, BorderLayout.CENTER);
        add(imagePanel, BorderLayout.CENTER);

        regenerateButton = new JButton("Regenerate Waifu");
        regenerateButton.setFont(new Font("Serif", Font.BOLD, 18));
        regenerateButton.setBackground(new Color(100, 149, 237));
        regenerateButton.setForeground(Color.WHITE);
        regenerateButton.setFocusPainted(false);
        regenerateButton.setBorder(BorderFactory.createCompoundBorder(
                BorderFactory.createLineBorder(new Color(30, 144, 255), 2, true),
                BorderFactory.createEmptyBorder(10, 20, 10, 20)));
        regenerateButton.setCursor(new Cursor(Cursor.HAND_CURSOR));
        regenerateButton.addActionListener(this);
        add(regenerateButton, BorderLayout.SOUTH);

        fetchAndDisplayImage();
    }

    private void fetchAndDisplayImage() {
        try {
            URL url = new URL("https://api.waifu.pics/sfw/waifu");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.connect();

            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            StringBuilder response = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                response.append(line);
            }
            reader.close();

            String jsonResponse = response.toString();
            String imageUrl = extractImageUrl(jsonResponse);

            URL imgUrl = new URL(imageUrl);
            BufferedImage image = ImageIO.read(imgUrl);

            Image scaledImage = image.getScaledInstance(IMAGE_WIDTH, IMAGE_HEIGHT, Image.SCALE_SMOOTH);
            ImageIcon imageIcon = new ImageIcon(scaledImage);
            imageLabel.setIcon(imageIcon);

        } catch (Exception e) {
            e.printStackTrace();
            JOptionPane.showMessageDialog(this, "Failed to fetch image", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private String extractImageUrl(String jsonResponse) {
        Pattern pattern = Pattern.compile("\"url\":\"(.*?)\"");
        Matcher matcher = pattern.matcher(jsonResponse);
        if (matcher.find()) {
            return matcher.group(1).replace("\\", "");
        }
        return null;
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == regenerateButton) {
            fetchAndDisplayImage();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            WaifuHub frame = new WaifuHub();
            frame.setVisible(true);
        });
    }
}
