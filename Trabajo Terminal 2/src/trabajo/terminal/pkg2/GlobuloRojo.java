/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajo.terminal.pkg2;

import java.awt.image.BufferedImage;
import java.text.DecimalFormat;
import java.util.ArrayList;
import javax.annotation.PostConstruct;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JTextField;

/**
 *
 * @author cavca
 */
public class GlobuloRojo extends JFrame {

    BufferedImage globulo;
    ArrayList<Integer> contorno;
    ArrayList<Double> distancias;
    int[] centro = new int[2];

    DecimalFormat df2 = new DecimalFormat("#.##");

    JTextField jtf1;
    JTextField jtf2;
    ImageIcon ii;
    JLabel jl;

    public GlobuloRojo(BufferedImage img, int[] color) {
        globulo = img;
        contorno = new ArrayList();
        distancias = new ArrayList();
        centro[0] = color[0];
        centro[1] = color[1];

        ii = new ImageIcon(globulo);
        jl = new JLabel(ii);
        jtf1 = new JTextField("Contorno: ");
        jtf2 = new JTextField("Distancias: ");

        setSize(800, 800);
        setLocationRelativeTo(null);
        setLayout(null);

        jl.setBounds(150, 0, 500, 500);
        jtf1.setBounds(0, 600, 800, 50);
        jtf2.setBounds(0, 700, 800, 50);
        jtf1.setEditable(false);
        jtf2.setEditable(false);
        add(jl);
        add(jtf1);
        add(jtf2);
        setVisible(true);
    }

    public void describirContornoEstable() {

        setSize(800, 800);
        setLocationRelativeTo(null);
        setLayout(null);

        jl.setBounds(150, 0, 500, 500);
        jtf1.setBounds(0, 600, 800, 50);
        jtf2.setBounds(0, 700, 800, 50);
        jtf1.setEditable(false);
        jtf2.setEditable(false);
        add(jl);
        add(jtf1);
        add(jtf2);
        setVisible(true);

        int x = centro[0], y = centro[1];
        try {
            do {
//                System.out.println("\t" + x + "," + y + " = " + (globulo.getRGB(x, y) & 255));
                y--;
            } while ((globulo.getRGB(x, y) & 255) == 255);
            y++;
        } catch (Exception e) {
        }

        int xi = x, yi = y;

        do {
            int i = x + 1, j = y;

            int contorno = 7;
            double teta = 0;
            boolean bAntes = false, bDespues = false;

            do {
                bAntes = bDespues;

                i -= Math.sin(teta);
                j -= Math.cos(teta);
                contorno = (contorno + 1) % 8;

                if (i < x - 1 || i > x + 1 || j < y - 1 || j > y + 1) {
                    i += Math.sin(teta);
                    j += Math.cos(teta);
                    teta += Math.PI / 2;

                    i -= Math.sin(teta);
                    j -= Math.cos(teta);
                }

                try {
                    if ((globulo.getRGB(i, j) & 255) == 255) {
                        bDespues = true;
                    } else {
                        bDespues = false;
                    }
                } catch (Exception e) {
                    bDespues = false;
                }

            } while (!(bAntes && !bDespues));

            i += Math.sin(teta);
            j += Math.cos(teta);

            jtf1.setText(jtf1.getText() + contorno + ",");
//            System.out.println(jtf1.getText() + contorno + ",");

            x = i;
            y = j;

            jtf2.setText(jtf2.getText() + df2.format(Math.sqrt(Math.pow(x - centro[0], 2) + Math.pow(y - centro[1], 2))) + ",");
//            System.out.printf(jtf2.getText() + df2.format(Math.sqrt(Math.pow(x-centro[0], 2)+Math.pow(y-centro[1], 2))) + ",");
        } while (x != xi || y != yi);

        System.out.println("\t" + x + "," + y + " = " + (globulo.getRGB(x, y) & 255));
    }

    public boolean describirContorno() {

        int x = centro[0], y = centro[1];
        try {
            do {
//                System.out.println("\t" + x + "," + y + " = " + (globulo.getRGB(x, y) & 255));
                y--;
            } while (y >= 0 && (globulo.getRGB(x, y) & 255) == 255);
            y++;
        } catch (Exception e) {
            return false;
        }

        int xi = x, yi = y;
        int dx = 0, dy = -1;
        double teta = 0;
        int contorno = 2;
        int i, j;
        int sin = (int) Math.sin(teta);
        int cos = (int) Math.cos(teta);

        do {
            i = x - dy;
            j = y + dx;

            contorno = (contorno + 5) % 8;

            boolean bAntes = false, bDespues = false;

            try {
                if ((globulo.getRGB(i, j) & 255) == 255) {
                    bDespues = true;
                } else {
                    bDespues = false;
                }
            } catch (Exception e) {
                return false;
//                bDespues = false;
            }

            do {
                bAntes = bDespues;

                i -= sin;
                j -= cos;
                contorno = (contorno + 1) % 8;

                if (i < x - 1 || i > x + 1 || j < y - 1 || j > y + 1) {
                    i += sin;
                    j += cos;
                    teta += Math.PI / 2;
                    teta %= 2 * Math.PI;

                    sin = (int) Math.sin(teta);
                    cos = (int) Math.cos(teta);

                    i -= sin;
                    j -= cos;
                }

                try {
                    if ((globulo.getRGB(i, j) & 255) == 255) {
                        bDespues = true;
                    } else {
                        bDespues = false;
                    }
                } catch (Exception e) {
                    return false;
//                    bDespues = false;
                }

            } while (!(bAntes && !bDespues));

            i += sin;

            j += cos;

//            System.out.println(jtf1.getText() + contorno + ",");
            jtf1.setText(jtf1.getText() + contorno + ",");

            dx = i - x;
            dy = j - y;

//            if(dx == 0 || dy == 0){
            teta += Math.PI * (3f / 2f);
            teta %= 2 * Math.PI;
//            }

            sin = (int) Math.sin(teta);
            cos = (int) Math.cos(teta);

            x = i;
            y = j;

//            System.out.printf(jtf2.getText() + df2.format(Math.sqrt(Math.pow(x-centro[0], 2)+Math.pow(y-centro[1], 2))) + ",\n");
            jtf2.setText(jtf2.getText() + df2.format(Math.sqrt(Math.pow(x - centro[0], 2) + Math.pow(y - centro[1], 2))) + ",");

        } while (x != xi || y != yi);

        System.out.println(jtf1.getText());
        System.out.println(jtf2.getText() + "\n");
        
        return true;
    }

}
