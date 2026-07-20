import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import javax.imageio.ImageIO;
import javax.swing.*;

public class VisorPlanosSoftware extends JPanel {

    static final int ANCHO = 900;
    static final int ALTO = 600;

    BufferedImage frameBuffer = new BufferedImage(ANCHO, ALTO, BufferedImage.TYPE_INT_RGB);
    float[][] zBuffer = new float[ANCHO][ALTO];

    BufferedImage tex1, tex2, tex3;

    float z1 = 0.2f, z2 = 0.5f, z3 = 0.8f;
    float a1 = 1.0f, a2 = 0.7f, a3 = 0.5f;

    boolean usarZBuffer = true;
    boolean verProfundidad = false;

    public VisorPlanosSoftware() {
        cargarTexturas();
        renderizar();
    }

    private void cargarTexturas() {
        try {
            tex1 = ImageIO.read(new File("imagenes/universo.jpg"));
            tex2 = ImageIO.read(new File("imagenes/paisaje.jpg"));
            tex3 = ImageIO.read(new File("imagenes/original.jpg"));
        } catch (Exception e) {
            tex1 = crearTexturaFallback(Color.RED, Color.WHITE);
            tex2 = crearTexturaFallback(Color.GREEN, Color.BLACK);
            tex3 = crearTexturaFallback(Color.BLUE, Color.YELLOW);
        }
    }

    private BufferedImage crearTexturaFallback(Color c1, Color c2) {
        BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_RGB);
        for (int y = 0; y < img.getHeight(); y++) {
            for (int x = 0; x < img.getWidth(); x++) {
                boolean bloque = ((x / 16) + (y / 16)) % 2 == 0;
                img.setRGB(x, y, (bloque ? c1 : c2).getRGB());
            }
        }
        return img;
    }

    private void limpiarBuffers() {
        for (int y = 0; y < ALTO; y++) {
            for (int x = 0; x < ANCHO; x++) {
                frameBuffer.setRGB(x, y, new Color(40, 40, 40).getRGB());
                zBuffer[x][y] = Float.POSITIVE_INFINITY;
            }
        }
    }

    private void renderizar() {
        limpiarBuffers();

        dibujarPlano(tex3, 500, 120, 260, 220, z3, a3, false);
        dibujarPlano(tex2, 300, 180, 260, 220, z2, a2, true);
        dibujarPlano(tex1, 120, 100, 260, 220, z1, a1, false);

        repaint();
    }

    private void dibujarPlano(BufferedImage textura, int x0, int y0, int ancho, int alto,
                              float z, float alpha, boolean inclinado) {

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                int sx = x0 + x;
                int sy = y0 + y;

                if (sx < 0 || sx >= ANCHO || sy < 0 || sy >= ALTO) {
                    continue;
                }

                float zPixel = z;

                if (inclinado) {
                    zPixel = z + (x / (float) ancho) * 0.25f;
                }

                if (usarZBuffer && zPixel >= zBuffer[sx][sy]) {
                    continue;
                }

                int tx = x * textura.getWidth() / ancho;
                int ty = y * textura.getHeight() / alto;

                int colorTextura;

                if (verProfundidad) {
                    int gris = (int) (255 * (1.0f - Math.min(zPixel, 1.0f)));
                    gris = Math.max(0, Math.min(255, gris));
                    colorTextura = new Color(gris, gris, gris).getRGB();
                } else {
                    colorTextura = textura.getRGB(tx, ty);
                }

                int colorFinal = mezclar(frameBuffer.getRGB(sx, sy), colorTextura, alpha);

                frameBuffer.setRGB(sx, sy, colorFinal);

                if (usarZBuffer) {
                    zBuffer[sx][sy] = zPixel;
                }
            }
        }
    }

    private int mezclar(int fondo, int superior, float alpha) {
        int r1 = (fondo >> 16) & 0xFF;
        int g1 = (fondo >> 8) & 0xFF;
        int b1 = fondo & 0xFF;

        int r2 = (superior >> 16) & 0xFF;
        int g2 = (superior >> 8) & 0xFF;
        int b2 = superior & 0xFF;

        int r = (int) (r2 * alpha + r1 * (1 - alpha));
        int g = (int) (g2 * alpha + g1 * (1 - alpha));
        int b = (int) (b2 * alpha + b1 * (1 - alpha));

        return (r << 16) | (g << 8) | b;
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(frameBuffer, 0, 0, null);
    }

    @Override
    public Dimension getPreferredSize() {
        return new Dimension(ANCHO, ALTO);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            VisorPlanosSoftware panel = new VisorPlanosSoftware();

            JFrame ventana = new JFrame("Visor de Planos Texturizados - Software");
            ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            ventana.setLayout(new BorderLayout());

            JPanel controles = new JPanel();
            controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
            controles.setPreferredSize(new Dimension(260, ALTO));

            JCheckBox checkZ = new JCheckBox("Activar Z-Buffer", true);
            JCheckBox checkProf = new JCheckBox("Ver profundidad", false);

            JSlider sliderZ1 = new JSlider(0, 100, 20);
            JSlider sliderZ2 = new JSlider(0, 100, 50);
            JSlider sliderZ3 = new JSlider(0, 100, 80);

            JSlider sliderA1 = new JSlider(0, 100, 100);
            JSlider sliderA2 = new JSlider(0, 100, 70);
            JSlider sliderA3 = new JSlider(0, 100, 50);

            checkZ.addActionListener(e -> {
                panel.usarZBuffer = checkZ.isSelected();
                panel.renderizar();
            });

            checkProf.addActionListener(e -> {
                panel.verProfundidad = checkProf.isSelected();
                panel.renderizar();
            });

            sliderZ1.addChangeListener(e -> {
                panel.z1 = sliderZ1.getValue() / 100f;
                panel.renderizar();
            });

            sliderZ2.addChangeListener(e -> {
                panel.z2 = sliderZ2.getValue() / 100f;
                panel.renderizar();
            });

            sliderZ3.addChangeListener(e -> {
                panel.z3 = sliderZ3.getValue() / 100f;
                panel.renderizar();
            });

            sliderA1.addChangeListener(e -> {
                panel.a1 = sliderA1.getValue() / 100f;
                panel.renderizar();
            });

            sliderA2.addChangeListener(e -> {
                panel.a2 = sliderA2.getValue() / 100f;
                panel.renderizar();
            });

            sliderA3.addChangeListener(e -> {
                panel.a3 = sliderA3.getValue() / 100f;
                panel.renderizar();
            });

            controles.add(new JLabel("Profundidad Plano 1"));
            controles.add(sliderZ1);
            controles.add(new JLabel("Profundidad Plano 2"));
            controles.add(sliderZ2);
            controles.add(new JLabel("Profundidad Plano 3"));
            controles.add(sliderZ3);

            controles.add(Box.createVerticalStrut(20));

            controles.add(new JLabel("Opacidad Plano 1"));
            controles.add(sliderA1);
            controles.add(new JLabel("Opacidad Plano 2"));
            controles.add(sliderA2);
            controles.add(new JLabel("Opacidad Plano 3"));
            controles.add(sliderA3);

            controles.add(Box.createVerticalStrut(20));
            controles.add(checkZ);
            controles.add(checkProf);

            ventana.add(panel, BorderLayout.CENTER);
            ventana.add(controles, BorderLayout.EAST);
            ventana.pack();
            ventana.setLocationRelativeTo(null);
            ventana.setVisible(true);
        });
    }
}