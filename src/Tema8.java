import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class Tema8 {

    public static void main(String[] args) {
        try {
            BufferedImage imagen1 = ImageIO.read(new File("imagenes/paisaje.jpg"));
            BufferedImage imagen2 = ImageIO.read(new File("imagenes/universo.jpg"));

            float profundidad1 = 10.0f;
            float profundidad2 = 5.0f;

            int ancho = imagen1.getWidth();
            int alto = imagen1.getHeight();

            BufferedImage imagen2escalado = escalar(imagen2, ancho,alto);

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

                    int pixelPaisaje = imagen1.getRGB(x, y);
                    if (profundidad1 < zBuffer[x][y]) {
                        zBuffer[x][y] = profundidad1;
                        salida.setRGB(x, y, pixelPaisaje);
                    }

                    int dx = x - centroX;
                    int dy = y - centroY;

                    if (dx * dx + dy * dy <= radio2) {
                        int pixelUniverso = imagen2escalado.getRGB(x, y);

                        int r = (pixelUniverso >> 16) & 0xFF;
                        int g = (pixelUniverso >> 8) & 0xFF;
                        int b = pixelUniverso & 0xFF;
                        int brillo = (r + g + b) / 3;

                        if (brillo > 128 && profundidad2 < zBuffer[x][y]) {
                            zBuffer[x][y] = profundidad2;
                            salida.setRGB(x, y, pixelUniverso);
                        }
                    }
                }
            }

            ImageIO.write(salida, "png", new File("imagenes/ExamenTema8_2.png"));
            System.out.println("Imagen generada correctamente.");

        } catch (IOException e) {
            System.out.println(e.getMessage());
        }
    }
    static BufferedImage escalar(BufferedImage imagen, int ancho, int alto) {
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = salida.createGraphics();
        g.drawImage(imagen, 0, 0, ancho, alto, null);
        g.dispose();
        return salida;
    }
}

