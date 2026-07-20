import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Main {

    public static void main(String[] args) {
        try {
            File entrada = new File("imagenes/original.jpg");
            BufferedImage imagen = ImageIO.read(entrada);

            if (imagen == null) {
                throw new IOException("No se pudo leer la imagen.");
            }

            ImageIO.write(aumentarRojo(imagen), "jpg", new File("imagenes/1_rojo.jpg"));
            ImageIO.write(escalaDeGrisesLuminancia(imagen), "jpg", new File("imagenes/2_grises.jpg"));
            ImageIO.write(umbralizar(imagen, 100), "jpg", new File("imagenes/3_umbral_100.jpg"));
            ImageIO.write(umbralizar(imagen, 50), "jpg", new File("imagenes/3_umbral_50.jpg"));
            ImageIO.write(umbralizar(imagen, 200), "jpg", new File("imagenes/3_umbral_200.jpg"));

            ImageIO.write(modificarSaturacion(imagen, 1.20f), "jpg", new File("imagenes/4_saturacion_120.jpg"));
            ImageIO.write(modificarSaturacion(imagen, 0.60f), "jpg", new File("imagenes/4_saturacion_60.jpg"));
            ImageIO.write(modificarSaturacion(imagen, 1.0f), "jpg", new File("imagenes/4_saturacion_100.jpg"));
            ImageIO.write(modificarSaturacion(imagen, 0.0f), "jpg", new File("imagenes/4_saturacion_0.jpg"));

            ImageIO.write(rotarHue(imagen, 60), "jpg", new File("imagenes/5_hue_60.jpg"));
            ImageIO.write(rotarHue(imagen, 150), "jpg", new File("imagenes/5_hue_150.jpg"));
            ImageIO.write(rotarHue(imagen, 360), "jpg", new File("imagenes/5_hue_360.jpg"));
            ImageIO.write(rotarHue(imagen, 0), "jpg", new File("imagenes/5_hue_0.jpg"));

            ImageIO.write(modificarBrillo(imagen, 40), "jpg", new File("imagenes/6_brillo_40.jpg"));
            ImageIO.write(modificarBrillo(imagen, -40), "jpg", new File("imagenes/6_brillo_m40.jpg"));

            ImageIO.write(interpolarConBlanco(imagen), "jpg", new File("imagenes/7_interpolacion_blanco.jpg"));
            ImageIO.write(interpolarConNegro(imagen), "jpg", new File("imagenes/8_interpolacion_negro.jpg"));

            ImageIO.write(altoContraste(imagen, 1.8f), "jpg", new File("imagenes/9_alto_contraste.jpg"));
            mostrarCMYKTodosLosPixeles(imagen);

            System.out.println("Ejercicios resueltos y guardados correctamente.");

        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        }
    }

    // 1. Aumentar el canal rojo
    public static BufferedImage aumentarRojo(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int a = (pixel >> 24) & 0xFF;
                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                r = clamp(r + 40);

                int nuevoPixel = (a << 24) | (r << 16) | (g << 8) | b;
                salida.setRGB(x, y, nuevoPixel);
            }
        }
        return salida;
    }

    // 2. Escala grises
    public static BufferedImage escalaDeGrisesLuminancia(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                int gris = clamp((int) (0.299 * r + 0.587 * g + 0.114 * b));
                int nuevoPixel = (gris << 16) | (gris << 8) | gris;
                salida.setRGB(x, y, nuevoPixel);
            }
        }
        return salida;
    }

    // 3. Umbralización
    public static BufferedImage umbralizar(BufferedImage imagen, int umbral) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                int gris = (int) (0.299 * r + 0.587 * g + 0.114 * b);
                int valor = gris >= umbral ? 255 : 0;

                int nuevoPixel = (valor << 16) | (valor << 8) | valor;
                salida.setRGB(x, y, nuevoPixel);
            }
        }
        return salida;
    }

    // 4. Modificar saturación
    public static BufferedImage modificarSaturacion(BufferedImage imagen, float saturacion) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                int gris = (int) (0.299 * r + 0.587 * g + 0.114 * b);

                r = clamp((int) (gris + saturacion * (r - gris)));
                g = clamp((int) (gris + saturacion * (g - gris)));
                b = clamp((int) (gris + saturacion * (b - gris)));

                int nuevoPixel = (r << 16) | (g << 8) | b;
                salida.setRGB(x, y, nuevoPixel);
            }
        }

        return salida;
    }

    // 5. Rotar hue
    public static BufferedImage rotarHue(BufferedImage imagen, float grados) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                float[] hsb = java.awt.Color.RGBtoHSB(r, g, b, null);
                float nuevoHue = (hsb[0] + (grados / 360f)) % 1.0f;
                if (nuevoHue < 0)
                    nuevoHue += 1.0f;

                int rgbNuevo = java.awt.Color.HSBtoRGB(nuevoHue, hsb[1], hsb[2]);
                salida.setRGB(x, y, rgbNuevo);
            }
        }
        return salida;
    }

    // 6. Modificar brillo
    public static BufferedImage modificarBrillo(BufferedImage imagen, int factor) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                r = clamp(r + factor);
                g = clamp(g + factor);
                b = clamp(b + factor);

                int nuevoPixel = (r << 16) | (g << 8) | b;
                salida.setRGB(x, y, nuevoPixel);
            }
        }
        return salida;
    }

    // 7. Interpolación entre la imagen original y blanco
    public static BufferedImage interpolarConBlanco(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                float alpha = 0.5f;

                int rNuevo = clamp((int) (r * (1 - alpha) + 255 * alpha));
                int gNuevo = clamp((int) (g * (1 - alpha) + 255 * alpha));
                int bNuevo = clamp((int) (b * (1 - alpha) + 255 * alpha));

                int nuevoPixel = (rNuevo << 16) | (gNuevo << 8) | bNuevo;
                salida.setRGB(x, y, nuevoPixel);
            }
        }
        return salida;
    }

    // 8. Interpolación entre la imagen original y negro
    public static BufferedImage interpolarConNegro(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                float alpha = 0.5f;

                int rNuevo = clamp((int) (r * (1 - alpha)));
                int gNuevo = clamp((int) (g * (1 - alpha)));
                int bNuevo = clamp((int) (b * (1 - alpha)));

                int nuevoPixel = (rNuevo << 16) | (gNuevo << 8) | bNuevo;
                salida.setRGB(x, y, nuevoPixel);
            }
        }
        return salida;
    }

    // 9. Alto contraste
    public static BufferedImage altoContraste(BufferedImage imagen, float factor) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();
        BufferedImage salida = new BufferedImage(ancho, alto, BufferedImage.TYPE_INT_RGB);

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                r = clamp((int) ((r - 128) * factor + 128));
                g = clamp((int) ((g - 128) * factor + 128));
                b = clamp((int) ((b - 128) * factor + 128));

                int nuevoPixel = (r << 16) | (g << 8) | b;
                salida.setRGB(x, y, nuevoPixel);
            }
        }
        return salida;
    }

    // EXTRA: mostrar CMYK de todos los píxeles
    public static void mostrarCMYKTodosLosPixeles(BufferedImage imagen) {
        int ancho = imagen.getWidth();
        int alto = imagen.getHeight();

        System.out.println("======================================");
        System.out.println("CMYK de todos los píxeles");
        System.out.println("======================================");

        for (int y = 0; y < alto; y++) {
            for (int x = 0; x < ancho; x++) {
                int pixel = imagen.getRGB(x, y);

                int r = (pixel >> 16) & 0xFF;
                int g = (pixel >> 8) & 0xFF;
                int b = pixel & 0xFF;

                float[] cmyk = rgbToCmyk(r, g, b);

                System.out.println("Pixel (" + x + ", " + y + ") - RGB: (" + r + ", " + g + ", " + b + ") - CMYK: (" +
                        Math.round(cmyk[0] * 100) + "%, " +
                        Math.round(cmyk[1] * 100) + "%, " +
                        Math.round(cmyk[2] * 100) + "%, " +
                        Math.round(cmyk[3] * 100) + "%)");
            }
        }
        System.out.println("======================================");
    }

    // Conversión RGB -> CMYK
    public static float[] rgbToCmyk(int r, int g, int b) {
        float rf = r / 255f;
        float gf = g / 255f;
        float bf = b / 255f;

        float k = 1.0f - Math.max(rf, Math.max(gf, bf));

        if (k >= 1.0f) {
            return new float[] { 0f, 0f, 0f, 1f };
        }

        float c = (1.0f - rf - k) / (1.0f - k);
        float m = (1.0f - gf - k) / (1.0f - k);
        float y = (1.0f - bf - k) / (1.0f - k);

        return new float[] { c, m, y, k };
    }

    private static int clamp(int valor) {
        return Math.clamp(valor, 0, 255);
    }

}