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

            BufferedImage universo = escalar(universoOriginal, ancho, alto);
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

                    pintarSiMasCerca(salida, zBuffer, x, y, paisaje.getRGB(x, y), 10.0f);

                    int dx = x - centroX;
                    int dy = y - centroY;

                    if (dx * dx + dy * dy <= radio2) {
                        int colorUniverso = universo.getRGB(x, y);

                        if (brilloPromedio(colorUniverso) > 128) {
                            pintarSiMasCerca(salida, zBuffer, x, y, colorUniverso, 5.0f);
                        }
                    }
                }
            }

            ImageIO.write(salida, "png", new File("imagenes/ExamenTema8.png"));
            System.out.println("Imagen generada correctamente.");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }

    private static void pintarSiMasCerca(BufferedImage img, float[][] zBuffer,
                                         int x, int y, int color, float z) {
        if (z < zBuffer[x][y]) {
            zBuffer[x][y] = z;
            img.setRGB(x, y, color);
        }
    }

    private static int brilloPromedio(int argb) {
        int r = (argb >> 16) & 0xFF;
        int g = (argb >> 8) & 0xFF;
        int b = argb & 0xFF;
        return (r + g + b) / 3;
    }

    private static BufferedImage escalar(BufferedImage img, int ancho, int alto) {
        BufferedImage out = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g = out.createGraphics();
        g.drawImage(img.getScaledInstance(ancho, alto, Image.SCALE_FAST), 0, 0, null);
        g.dispose();
        return out;
    }
}