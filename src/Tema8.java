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

            float profundidadPaisaje = 10.0f;
            float profundidadUniverso = 5.0f;

            int ancho = paisaje.getWidth();
            int alto = paisaje.getHeight();

            BufferedImage universo = escalar(universoOriginal, ancho,alto);

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

                    int pixelPaisaje = paisaje.getRGB(x, y);
                    if (profundidadPaisaje < zBuffer[x][y]) {
                        zBuffer[x][y] = profundidadPaisaje;
                        salida.setRGB(x, y, pixelPaisaje);
                    }

                    int dx = x - centroX;
                    int dy = y - centroY;

                    if (dx * dx + dy * dy <= radio2) {
                        int pixelUniverso = universo.getRGB(x, y);

                        int r = (pixelUniverso >> 16) & 0xFF;
                        int gr = (pixelUniverso >> 8) & 0xFF;
                        int b = pixelUniverso & 0xFF;
                        int brillo = (r + gr + b) / 3;

                        if (brillo > 128 && profundidadUniverso < zBuffer[x][y]) {
                            zBuffer[x][y] = profundidadUniverso;
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

