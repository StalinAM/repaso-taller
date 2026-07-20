import java.awt.*;
import javax.swing.*;

public class EjercicioCuadradosZBuffer extends JPanel {

    static final int ANCHO = 800, ALTO = 600;
    Color[][] canvas = new Color[ANCHO][ALTO];
    float[][] zBuffer = new float[ANCHO][ALTO];

    float zA = 0.4f, zB = 0.6f;

    public EjercicioCuadradosZBuffer() {
        renderizar();
    }

    private void renderizar() {
        for (int x = 0; x < ANCHO; x++) {
            for (int y = 0; y < ALTO; y++) {
                canvas[x][y] = Color.GRAY;
                zBuffer[x][y] = 1.0f;
            }
        }

        dibujarCuadrado(100, 100, 200, zA, Color.CYAN);
        dibujarCuadrado(250, 200, 200, zB, Color.MAGENTA);
        repaint();
    }

    private void dibujarCuadrado(int x0, int y0, int tam, float z, Color color) {
        for (int x = x0; x < x0 + tam; x++) {
            for (int y = y0; y < y0 + tam; y++) {
                if (x >= 0 && x < ANCHO && y >= 0 && y < ALTO && z < zBuffer[x][y]) {
                    zBuffer[x][y] = z;
                    canvas[x][y] = color;
                }
            }
        }
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (int x = 0; x < ANCHO; x++) {
            for (int y = 0; y < ALTO; y++) {
                g.setColor(canvas[x][y]);
                g.fillRect(x, y, 1, 1);
            }
        }
    }

    public static void main(String[] args) {
        JFrame ventana = new JFrame("Rasterización + Z-Buffer");
        EjercicioCuadradosZBuffer panel = new EjercicioCuadradosZBuffer();

        JSlider sA = new JSlider(10, 90, 40);
        JSlider sB = new JSlider(10, 90, 60);

        sA.addChangeListener(e -> {
            panel.zA = sA.getValue() / 100f;
            panel.renderizar();
        });

        sB.addChangeListener(e -> {
            panel.zB = sB.getValue() / 100f;
            panel.renderizar();
        });

        JPanel controles = new JPanel();
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.setPreferredSize(new Dimension(200, ALTO));
        controles.add(new JLabel("Profundidad Cyan:"));
        controles.add(sA);
        controles.add(new JLabel("Profundidad Magenta:"));
        controles.add(sB);

        ventana.setLayout(new BorderLayout());
        ventana.add(panel, BorderLayout.CENTER);
        ventana.add(controles, BorderLayout.EAST);
        ventana.setSize(ANCHO + 200, ALTO);
        ventana.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        ventana.setVisible(true);
    }
}