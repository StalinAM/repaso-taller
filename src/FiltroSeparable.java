package efectos;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;

public class FiltroSeparable {

    public static void main(String[] args) {

        try {
            File origen = new File("src/imag/grande.jpg");
            File filtroSeparable = new File("src/imag/filtroSeparable.png");

            BufferedImage original = ImageIO.read(origen);

            double[] kernelGaussiano = {
                1.0 / 4.0,
                2.0 / 4.0,
                1.0 / 4.0
            };

            BufferedImage resultado =
                    aplicarFiltroSeparable(original, kernelGaussiano);

            ImageIO.write(resultado, "png", filtroSeparable);

            System.out.println("Filtro aplicado correctamente.");

        } catch (IOException e) {

            System.err.println(
                "Error al procesar la imagen: "
                + e.getMessage()
            );
        }
    }

    public static BufferedImage aplicarFiltroSeparable(
            BufferedImage imagen,
            double[] kernel) {

        BufferedImage ch =
                convolucionHorizontal(imagen, kernel);

        BufferedImage cv =
                convolucionVertical(ch, kernel);

        return cv;
    }

    public static BufferedImage convolucionHorizontal(
            BufferedImage imagen,
            double[] kernel) {

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        BufferedImage salida =
                new BufferedImage(
                        ancho,
                        alto,
                        BufferedImage.TYPE_INT_ARGB
                );

        int radio = kernel.length / 2;

        for (int y = 0; y < alto; y++) {

            for (int x = 0; x < ancho; x++) {

                double sumaRojo = 0;
                double sumaVerde = 0;
                double sumaAzul = 0;
                double sumaAlfa = 0;

                for (int k = -radio; k <= radio; k++) {

                    int vecinoX = clamp(x + k, 0, ancho - 1);

                    int pixel = imagen.getRGB(vecinoX, y);

                    int alfa = (pixel >>> 24) & 0xFF;
                    int rojo = (pixel >>> 16) & 0xFF;
                    int verde = (pixel >>> 8) & 0xFF;
                    int azul = pixel & 0xFF;

                    sumaAlfa += alfa * kernel[k + radio];
                    sumaRojo += rojo * kernel[k + radio];
                    sumaVerde += verde * kernel[k + radio];
                    sumaAzul += azul * kernel[k + radio];
                }

                int a = clamp((int) Math.round(sumaAlfa));
                int r = clamp((int) Math.round(sumaRojo));
                int g = clamp((int) Math.round(sumaVerde));
                int b = clamp((int) Math.round(sumaAzul));

                int nuevoPixel =
                        (a << 24) |
                        (r << 16) |
                        (g << 8) |
                        b;

                salida.setRGB(x, y, nuevoPixel);
            }
        }

        return salida;
    }
    
    
    public static BufferedImage convolucionVertical(
            BufferedImage imagen,
            double[] kernel) {

        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        BufferedImage salida = new BufferedImage(
                ancho,
                alto,
                BufferedImage.TYPE_INT_ARGB
        );

        int radio = kernel.length / 2;

        for (int y = 0; y < alto; y++) {

            for (int x = 0; x < ancho; x++) {

                double sumaRojo = 0;
                double sumaVerde = 0;
                double sumaAzul = 0;
                double sumaAlfa = 0;

                for (int k = -radio; k <= radio; k++) {

                    int vecinoY = clamp(y + k, 0, alto - 1);

                    int pixel = imagen.getRGB(x, vecinoY);

                    int alfa = (pixel >>> 24) & 0xFF;
                    int rojo = (pixel >>> 16) & 0xFF;
                    int verde = (pixel >>> 8) & 0xFF;
                    int azul = pixel & 0xFF;

                    sumaAlfa += alfa * kernel[k + radio];
                    sumaRojo += rojo * kernel[k + radio];
                    sumaVerde += verde * kernel[k + radio];
                    sumaAzul += azul * kernel[k + radio];
                }

                int a = clamp((int) Math.round(sumaAlfa));
                int r = clamp((int) Math.round(sumaRojo));
                int g = clamp((int) Math.round(sumaVerde));
                int b = clamp((int) Math.round(sumaAzul));

                int nuevoPixel =
                        (a << 24) |
                        (r << 16) |
                        (g << 8) |
                        b;

                salida.setRGB(x, y, nuevoPixel);
            }
        }

        return salida;
    }
    
    public static int clamp(int valor) {
        return clamp(valor, 0, 255);
    }

    public static int clamp(int valor, int minimo, int maximo) {
        if (valor < minimo) {
            return minimo;
        }
        if (valor > maximo) {
            return maximo;
        }
        return valor;
    }
}