import com.jogamp.opengl.*;
import com.jogamp.opengl.awt.GLJPanel;
import com.jogamp.opengl.util.texture.Texture;
import com.jogamp.opengl.util.texture.awt.AWTTextureIO;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Arrays;

public class ProyectoCapas3D extends JFrame {

    private final double[] z = { 1.5, 2.5, 3.5 };
    private final int[] alpha = { 255, 255, 255 };

    private final BufferedImage[] imagenes = new BufferedImage[3];
    private final Texture[] texturas = new Texture[3];

    private JComboBox<String> capaBox;
    private JSlider zSlider, alphaSlider;
    private JCheckBox depthCheck, mapaCheck;

    private boolean actualizando = false;
    private boolean depthTest = true;
    private boolean verMapa = false;

    private RenderPanel panel;

    public ProyectoCapas3D() {
        setTitle("Visor de Planos 3D");
        setSize(1000, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        cargarImagenes();
        crearUI();
    }

    private void cargarImagenes() {
        String[] rutas = {
                "Imagenes/ejemplo.jpg",
                "Imagenes/perfil.jpg",
                "Imagenes/anime-night-sky-illustration.jpg"
        };

        for (int i = 0; i < 3; i++) {
            try {
                imagenes[i] = ImageIO.read(new File(rutas[i]));
            } catch (Exception e) {
                imagenes[i] = texturaPrueba(i);
            }
        }
    }

    private BufferedImage texturaPrueba(int i) {
        BufferedImage img = new BufferedImage(128, 128, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = img.createGraphics();

        g.setColor(i == 0 ? Color.RED : i == 1 ? Color.GREEN : Color.BLUE);
        g.fillRect(0, 0, 128, 128);

        g.setColor(Color.WHITE);
        g.drawString("Textura " + (i + 1), 30, 64);

        g.dispose();
        return img;
    }

    private void crearUI() {
        panel = new RenderPanel();

        capaBox = new JComboBox<>(new String[] { "Capa 1", "Capa 2", "Capa 3" });
        zSlider = new JSlider(50, 500, 150);
        alphaSlider = new JSlider(0, 255, 255);

        depthCheck = new JCheckBox("Activar Depth Test", true);
        mapaCheck = new JCheckBox("Ver mapa de profundidad", false);

        capaBox.addActionListener(e -> actualizarControles());

        zSlider.addChangeListener(e -> {
            if (!actualizando) {
                z[capaBox.getSelectedIndex()] = zSlider.getValue() / 100.0;
                panel.repaint();
            }
        });

        alphaSlider.addChangeListener(e -> {
            if (!actualizando) {
                alpha[capaBox.getSelectedIndex()] = alphaSlider.getValue();
                panel.repaint();
            }
        });

        depthCheck.addActionListener(e -> {
            depthTest = depthCheck.isSelected();
            panel.repaint();
        });

        mapaCheck.addActionListener(e -> {
            verMapa = mapaCheck.isSelected();
            panel.repaint();
        });

        JPanel controles = new JPanel();
        controles.setLayout(new BoxLayout(controles, BoxLayout.Y_AXIS));
        controles.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        agregar(controles, "Seleccionar capa", capaBox);
        agregar(controles, "Profundidad Z", zSlider);
        agregar(controles, "Transparencia", alphaSlider);
        controles.add(depthCheck);
        controles.add(Box.createRigidArea(new Dimension(0, 10)));
        controles.add(mapaCheck);

        JSplitPane split = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, panel, controles);
        split.setDividerLocation(700);
        add(split);

        actualizarControles();
    }

    private void agregar(JPanel panel, String texto, JComponent comp) {
        panel.add(new JLabel(texto));
        comp.setMaximumSize(new Dimension(Integer.MAX_VALUE, 30));
        panel.add(comp);
        panel.add(Box.createRigidArea(new Dimension(0, 10)));
    }

    private void actualizarControles() {
        int i = capaBox.getSelectedIndex();
        actualizando = true;
        zSlider.setValue((int) (z[i] * 100));
        alphaSlider.setValue(alpha[i]);
        actualizando = false;
    }

    class RenderPanel extends GLJPanel implements GLEventListener {

        RenderPanel() {
            super(new GLCapabilities(GLProfile.get(GLProfile.GL2)));
            addGLEventListener(this);
        }

        @Override
        public void init(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            gl.glClearColor(0.08f, 0.08f, 0.08f, 1f);

            gl.glEnable(GL2.GL_BLEND);
            gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);

            gl.glEnable(GL2.GL_DEPTH_TEST);
            gl.glDepthFunc(GL2.GL_LEQUAL);

            gl.glEnable(GL2.GL_TEXTURE_2D);

            for (int i = 0; i < 3; i++) {
                texturas[i] = AWTTextureIO.newTexture(GLProfile.get(GLProfile.GL2), imagenes[i], true);
                texturas[i].setTexParameteri(gl, GL2.GL_TEXTURE_MIN_FILTER, GL2.GL_LINEAR);
                texturas[i].setTexParameteri(gl, GL2.GL_TEXTURE_MAG_FILTER, GL2.GL_LINEAR);
            }
        }

        @Override
        public void dispose(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();
            for (int i = 0; i < 3; i++) {
                if (texturas[i] != null) {
                    texturas[i].destroy(gl);
                }
            }
        }

        @Override
        public void display(GLAutoDrawable drawable) {
            GL2 gl = drawable.getGL().getGL2();

            gl.glClearColor(verMapa ? 0f : 0.08f, verMapa ? 0f : 0.08f, verMapa ? 0f : 0.08f, 1f);
            gl.glClear(GL2.GL_COLOR_BUFFER_BIT | GL2.GL_DEPTH_BUFFER_BIT);

            if (depthTest)
                gl.glEnable(GL2.GL_DEPTH_TEST);
            else
                gl.glDisable(GL2.GL_DEPTH_TEST);

            Integer[] orden = { 0, 1, 2 };
            Arrays.sort(orden, (a, b) -> Double.compare(z[b], z[a]));

            for (int i : orden) {
                dibujarPlano(gl, i);
            }
        }

        private void dibujarPlano(GL2 gl, int i) {
            double xCentro = (i - 1) * 90.0;
            double yCentro = (i - 1) * 30.0;

            double x1 = xCentro - 75;
            double x2 = xCentro + 75;
            double y1 = yCentro - 75;
            double y2 = yCentro + 75;

            boolean inclinado = (i == 1);
            double zIzq = inclinado ? z[i] - 0.4 : z[i];
            double zDer = inclinado ? z[i] + 0.4 : z[i];

            if (verMapa) {
                gl.glDisable(GL2.GL_TEXTURE_2D);
            } else {
                gl.glEnable(GL2.GL_TEXTURE_2D);
                texturas[i].bind(gl);
                gl.glColor4f(1f, 1f, 1f, alpha[i] / 255f);
            }

            gl.glBegin(GL2.GL_QUADS);

            if (verMapa) {
                float g1 = gris(zIzq);
                float g2 = gris(zDer);

                gl.glColor4f(g1, g1, g1, 1f);
                gl.glVertex3d(x1, y1, -zIzq);

                gl.glColor4f(g2, g2, g2, 1f);
                gl.glVertex3d(x2, y1, -zDer);

                gl.glColor4f(g2, g2, g2, 1f);
                gl.glVertex3d(x2, y2, -zDer);

                gl.glColor4f(g1, g1, g1, 1f);
                gl.glVertex3d(x1, y2, -zIzq);
            } else {
                gl.glTexCoord2f(0, 0);
                gl.glVertex3d(x1, y1, -zIzq);

                gl.glTexCoord2f(1, 0);
                gl.glVertex3d(x2, y1, -zDer);

                gl.glTexCoord2f(1, 1);
                gl.glVertex3d(x2, y2, -zDer);

                gl.glTexCoord2f(0, 1);
                gl.glVertex3d(x1, y2, -zIzq);
            }

            gl.glEnd();
        }

        private float gris(double z) {
            float g = (float) (1.0 - (z - 0.5) / 2.0);
            return Math.max(0f, Math.min(1f, g));
        }

        @Override
        public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
            GL2 gl = drawable.getGL().getGL2();
            if (height <= 0)
                height = 1;

            gl.glViewport(0, 0, width, height);
            gl.glMatrixMode(GL2.GL_PROJECTION);
            gl.glLoadIdentity();

            double near = 0.05;
            gl.glFrustum(-width / 2.0 * near, width / 2.0 * near,
                    height / 2.0 * near, -height / 2.0 * near,
                    near, 50.0);

            gl.glMatrixMode(GL2.GL_MODELVIEW);
            gl.glLoadIdentity();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> new ProyectoCapas3D().setVisible(true));
    }
}