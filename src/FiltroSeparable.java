import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FiltroSeparable {

    public static void main(String[] args) throws IOException {
        BufferedImage img = ImageIO.read(new File("imagenes/original.jpg"));
        double[] kernel = { 0.25, 0.5, 0.25 };
        int repeticiones = 12;

        BufferedImage resultado = img;

        for (int i = 0; i < repeticiones; i++) {
            resultado = convolucion(resultado, kernel, true);
            resultado = convolucion(resultado, kernel, false);
        }

        ImageIO.write(resultado, "png", new File("imagenes/filtroSeparable.png"));
        System.out.println("Filtro aplicado correctamente.");
    }

    public static BufferedImage convolucion(BufferedImage img, double[] kernel, boolean horizontal) {
        int ancho = img.getWidth();
        int alto = img.getHeight();
        int radio = kernel.length / 2;

        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_ARGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {

                double sumaA = 0;
                double sumaR = 0;
                double sumaG = 0;
                double sumaB = 0;

                for (int k = -radio; k <= radio; k++) {
                    int px = horizontal ? Math.clamp(x + k, 0, ancho - 1) : x;
                    int py = horizontal ? y : Math.clamp(y + k, 0, alto - 1);

                    int pixel = img.getRGB(px, py);
                    double peso = kernel[k + radio];

                    int a = (pixel >>> 24) & 0xFF;
                    int r = (pixel >>> 16) & 0xFF;
                    int g = (pixel >>> 8) & 0xFF;
                    int b = pixel & 0xFF;

                    sumaA += a * peso;
                    sumaR += r * peso;
                    sumaG += g * peso;
                    sumaB += b * peso;
                }

                int a = (int) Math.round(sumaA);
                int r = (int) Math.round(sumaR);
                int g = (int) Math.round(sumaG);
                int b = (int) Math.round(sumaB);

                a = Math.clamp(a, 0, 255);
                r = Math.clamp(r, 0, 255);
                g = Math.clamp(g, 0, 255);
                b = Math.clamp(b, 0, 255);

                int nuevoPixel = (a << 24) | (r << 16) | (g << 8) | b;
                salida.setRGB(x, y, nuevoPixel);
            }
        }

        return salida;
    }
}