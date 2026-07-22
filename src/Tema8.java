import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Tema8 {

    public static void main(String[] args) {
        try {
            BufferedImage paisaje = ImageIO.read(new File("imagenes/paisaje.jpg"));
            BufferedImage universoOriginal = ImageIO.read(new File("imagenes/universo.jpg"));

            int ancho = paisaje.getWidth();
            int alto = paisaje.getHeight();

            BufferedImage universo = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            Graphics2D g = universo.createGraphics();
            g.drawImage(universoOriginal, 0, 0, ancho, alto, null);
            g.dispose();

            BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
            float[][] zBuffer = new float[ancho][alto];

            for (int x = 0; x < ancho; x++) {
                for (int y = 0; y < alto; y++) {
                    zBuffer[x][y] = Float.POSITIVE_INFINITY;
                }
            }

            int centroX = ancho / 2;
            int centroY = alto / 2;
            int radio = Math.min(ancho, alto) / 3;
            int radio2 = radio * radio;

            for (int y = 0; y < alto; y++) {
                for (int x = 0; x < ancho; x++) {

                    int colorPaisaje = paisaje.getRGB(x, y);
                    if (10.0f < zBuffer[x][y]) {
                        zBuffer[x][y] = 10.0f;
                        salida.setRGB(x, y, colorPaisaje);
                    }

                    int dx = x - centroX;
                    int dy = y - centroY;

                    if (dx * dx + dy * dy <= radio2) {
                        int colorUniverso = universo.getRGB(x, y);

                        int r = (colorUniverso >> 16) & 0xFF;
                        int gr = (colorUniverso >> 8) & 0xFF;
                        int b = colorUniverso & 0xFF;
                        int brillo = (r + gr + b) / 3;

                        if (brillo > 128 && 5.0f < zBuffer[x][y]) {
                            zBuffer[x][y] = 5.0f;
                            salida.setRGB(x, y, colorUniverso);
                        }
                    }
                }
            }

            ImageIO.write(salida, "png", new File("imagenes/ExamenTema8_1.png"));
            System.out.println("Imagen generada correctamente.");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
}