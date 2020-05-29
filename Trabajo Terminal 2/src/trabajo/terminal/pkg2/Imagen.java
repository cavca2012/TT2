/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package trabajo.terminal.pkg2;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import javax.swing.JOptionPane;

/**
 *
 * @author cavca
 */
public class Imagen {

    BufferedImage im;
    BufferedImage imOriginal;
    ArrayList<GlobuloRojo> globulos;

    public Imagen(BufferedImage im) {
        imOriginal = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        Graphics gra = imOriginal.getGraphics();
        gra.drawImage(im, 0, 0, null);
        gra.dispose();

        this.im = im;
        globulos = new ArrayList<>();
    }

    public BufferedImage analizar() {
//        im = binarizar(im, obtenerOtsu(-1));
//        im = componentesC(im,1);
//        im = and(im,imOriginal);
//        im = recortarM(im);

        quitarColor();
        im = fMediana(im, -1);
        im = binarizar(im, obtenerOtsu(im,-1));
        im = componentesC(im,true);
        im = pasarANeggativo(im);

        int tamañoK = 10;
        char kernel[][] = new char[tamañoK][tamañoK];

        for (int i = 0; i < tamañoK; i++) {
            for (int j = 0; j < tamañoK; j++) {
                kernel[i][j] = '1';
            }
        }

        BufferedImage marcadores = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        Graphics gra = marcadores.getGraphics();
        gra.drawImage(im, 0, 0, null);
        gra.dispose();

        int contE = 0;

        while (contE < 1) {
            marcadores = erosion(marcadores, kernel);
            contE++;;
        }
        marcadores = componentesC(marcadores,false);
        
        im = tDDistancia(im);
        im = watershed(marcadores);
        return im;
    }

    public static BufferedImage pasarANeggativo(BufferedImage im) {
        BufferedImage img = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im, 0, 0, null);
        gra.dispose();

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                img.setRGB(i, j, 0xFFFFFF - (img.getRGB(i, j) & 0xFFFFFF) + (img.getRGB(i, j) & 0xFF000000));
            }
        }

        return img;
    }

    private void quitarColor() {
        int pixC = 0;
        int sum;
        int byn, r, g, b;

        for (int i = 0; i < im.getWidth(); i++) {
            for (int j = 0; j < im.getHeight(); j++) {
                pixC = im.getRGB(i, j);
                byn = ((pixC >> 24) & 255);
                r = ((pixC >> 16) & 255);
                g = ((pixC >> 8) & 255);
                b = (pixC & 255);
                sum = r + g + b;

                r = (sum / 3) & 255;
                g = (sum / 3) & 255;
                b = (sum / 3) & 255;
                // byn=(sum/4)&255;

                pixC = (byn << 24) + (r << 16) + (g << 8) + b;

                im.setRGB(i, j, pixC);
            }
        }
    }

    private BufferedImage fMediana(BufferedImage im2, int contCompA) {
        BufferedImage img = new BufferedImage(im2.getWidth(), im2.getHeight(), im2.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im2, 0, 0, null);
        gra.dispose();

        int contComp = 0;
        int pixC = 0;
        int byn[] = new int[9];
        int cont;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                byn = new int[9];
                cont = 0;

                for (int ii = i - 1; ii <= i + 1; ii++) {
                    for (int jj = j - 1; jj <= j + 1; jj++) {
                        try {
                            pixC = im2.getRGB(ii, jj);
                            byn[cont] = (pixC & 255);
                        } catch (Exception e) {
                            byn[cont] = -1;
                        }
                        cont++;
                    }
                }

                byn = ordenar(byn);

//                System.out.println((byn[(int) Math.floor(byn.length / 2)] + byn[(int) Math.ceil(byn.length / 2)]) +" =?"+ (im.getRGB(i, j)&255));
                if (((byn[(int) Math.floor(byn.length / 2)] + byn[(int) Math.ceil(byn.length / 2)]) / 2) == (im2.getRGB(i, j) & 255)) {
                    contComp++;
                }

                pixC = ((int) ((byn[(int) Math.floor(byn.length / 2)] + byn[(int) Math.ceil(byn.length / 2)]) / 2) << 24) + ((int) ((byn[(int) Math.floor(byn.length / 2)] + byn[(int) Math.ceil(byn.length / 2)]) / 2) << 16) + ((int) ((byn[(int) Math.floor(byn.length / 2)] + byn[(int) Math.ceil(byn.length / 2)]) / 2) << 8) + (int) ((byn[(int) Math.floor(byn.length / 2)] + byn[(int) Math.ceil(byn.length / 2)]) / 2);

                img.setRGB(i, j, pixC);
            }
        }
        if (contComp == (img.getWidth() * img.getHeight()) || contComp == contCompA) {
            System.out.println("\t" + contComp + " , " + (img.getWidth() * img.getHeight()));
//            this.im = img;
            return img;
        } else {
            System.out.println(contComp + " , " + (img.getWidth() * img.getHeight()));
            img = fMediana(img, contComp);
//            return img;
        }
        return img;
    }

    private int[] ordenar(int[] rgb) {
        int rgbl = rgb.length;
        for (int i = 0; i < rgb.length; i++) {
            if (rgb[i] == -1) {
                rgbl--;
            }
        }
        int rgbP[] = new int[rgbl];
        int mayor, iMayor = -1;

        for (int i = rgbP.length - 1; i >= 0; i--) {
            mayor = -1;
            for (int j = 0; j < rgb.length; j++) {
                if (rgb[j] > mayor) {
                    mayor = rgb[j];
                    iMayor = j;
                }
            }
            rgbP[i] = mayor;
            rgb[iMayor] = -1;
        }

        return rgbP;
    }

    private int obtenerOtsu(BufferedImage im, int omitir) {

        float promedio = 0;
        float otsu = 0;

        BufferedImage img = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im, 0, 0, null);
        gra.dispose();

        int hist[][] = new int[4][256];
        int pixC;
        int alf, r, g, b, sum;
        double proml = 0, otsuK = 0;

        for (int i = 0; i < hist.length; i++) {
            for (int j = 0; j < hist[i].length; j++) {
                hist[i][j] = 0;
            }
        }

        int omitriF;
        int omitriT = 0;
        for (int i = 0; i < img.getWidth(); i++) {
            proml = 0;
            omitriF = 0;
            for (int j = 0; j < img.getHeight(); j++) {
                pixC = img.getRGB(i, j) & 16777215;
                if (omitir == pixC) {
                    omitriF++;
                    continue;
                }
                alf = ((pixC >> 24) & 255);
                r = ((pixC >> 16) & 255);
                g = ((pixC >> 8) & 255);
                b = (pixC & 255);
                sum = r + g + b;

                alf = (sum / 3) & 255;
                proml += alf;
                hist[0][alf]++;
            }
            promedio += ((float) proml / (img.getHeight() - omitriF)) * ((img.getHeight() - omitriF) / img.getHeight());
            omitriT += omitriF;
        }
        promedio /= img.getWidth();

        double zw, in;
        double prom0 = 0, prom1 = 0;
        double alf0 = 0, alf1 = 0;
        double q;
        double maxOts = -1;
        for (int i = 0; i < 256; i++) {
            prom0 = 0;
            prom1 = 0;
            alf0 = 0;
            alf1 = 0;

            otsuK += hist[0][i];
            for (int j = 0; j < i; j++) {
                prom0 += hist[0][j] * j;
            }
            prom0 /= otsuK;
            for (int j = i; j < 256; j++) {
                prom1 += hist[0][j] * j;
            }
            prom1 /= (img.getWidth() * img.getHeight() - otsuK - omitriT);

            for (int j = 0; j < i; j++) {
                alf0 += Math.pow(j - prom0, 2) * ((float) hist[0][j] / (img.getWidth() * img.getHeight() - omitriT));
            }
            for (int j = i; j < 256; j++) {
                alf1 += Math.pow(j - prom1, 2) * ((float) hist[0][j] / (img.getWidth() * img.getHeight() - omitriT));
            }

            zw = (otsuK / (img.getWidth() * img.getHeight() - omitriT)) * Math.pow(prom0 - promedio, 2) + (1 - (otsuK / (img.getWidth() * img.getHeight() - omitriT))) * Math.pow(prom1 - promedio, 2);
            in = (otsuK / (img.getWidth() * img.getHeight() - omitriT)) * alf0 + (1 - (otsuK / (img.getWidth() * img.getHeight() - omitriT))) * alf1;

            q = zw / in;

            if (q >= maxOts) {
                maxOts = q;
                otsu = i;
            }
        }

        System.out.println("Otsu: " + otsu);

        return Math.round(otsu);
    }

    public BufferedImage binarizarInv(BufferedImage im2, int umbral) {
        BufferedImage img = new BufferedImage(im2.getWidth(), im2.getHeight(), im2.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im2, 0, 0, null);
        gra.dispose();

        int pixC = 0;
        int byn;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                pixC = img.getRGB(i, j);
                byn = (pixC & 255);
                // alf=(sum/4)&255;

                if (byn >= umbral) {
                    byn = 0;
                } else {
                    byn = 255;
                }

                pixC = (byn << 24) + (byn << 16) + (byn << 8) + byn;

                img.setRGB(i, j, pixC);
            }
        }
        return img;
    }

    public BufferedImage binarizar(BufferedImage im2, int umbral) {
        BufferedImage img = new BufferedImage(im2.getWidth(), im2.getHeight(), im2.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im2, 0, 0, null);
        gra.dispose();

        int pixC = 0;
        int byn;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                pixC = img.getRGB(i, j);
                byn = (pixC & 255);
                // alf=(sum/4)&255;

                if (byn >= umbral) {
                    byn = 255;
                } else {
                    byn = 0;
                }

                pixC = (byn << 24) + (byn << 16) + (byn << 8) + byn;

                img.setRGB(i, j, pixC);
            }
        }
        return img;
    }

    public BufferedImage tDDistancia(BufferedImage im) {
        im = binarizar(im, obtenerOtsu(im,-1));

        BufferedImage img = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im, 0, 0, null);
        gra.dispose();

        im = binarizar(im, obtenerOtsu(im,-1));

        BufferedImage img2 = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        gra = img2.getGraphics();
        gra.drawImage(im, 0, 0, null);
        gra.dispose();

        int pixC, alf, r, g, b;
        float mayor = 0;
        float imgM[][] = new float[im.getWidth()][im.getHeight()];
        for (int i = 0; i < im.getWidth(); i++) {
            for (int j = 0; j < im.getHeight(); j++) {

                if ((im.getRGB(i, j) & 255) == 0) {

                } else {
                    imgM[i][j] = im.getWidth() * im.getHeight();
                    for (int ii = i - 1; ii <= i; ii++) {
                        for (int jj = j - 1; jj <= j + 1; jj++) {
                            try {
                                if (ii == i && jj == j) {
                                    break;
                                } else {
                                    if (imgM[ii][jj] + Math.sqrt(Math.pow(ii - i, 2) + Math.pow(jj - j, 2)) < imgM[i][j]) {
                                        imgM[i][j] = imgM[ii][jj] + (float) Math.sqrt(Math.pow(ii - i, 2) + Math.pow(jj - j, 2));
//                                        System.out.println(i+","+j+"  ->  "+imgM[i][j]);
                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                }
            }
        }

        for (int i = im.getWidth() - 1; i >= 0; i--) {
            for (int j = im.getHeight() - 1; j >= 0; j--) {

                if ((im.getRGB(i, j) & 255) == 0) {

                } else {
                    for (int ii = i + 1; ii >= i; ii--) {
                        for (int jj = j + 1; jj >= j - 1; jj--) {
                            try {
                                if (ii == i && jj == j) {
                                    break;
                                } else {
                                    if (imgM[ii][jj] + Math.sqrt(Math.pow(ii - i, 2) + Math.pow(jj - j, 2)) < imgM[i][j]) {
                                        imgM[i][j] = imgM[ii][jj] + (float) Math.sqrt(Math.pow(ii - i, 2) + Math.pow(jj - j, 2));

                                    }
                                }
                            } catch (Exception e) {

                            }
                        }
                    }
                    if (imgM[i][j] > mayor) {
                        mayor = imgM[i][j];
                    }
                }
            }
        }

        imgM = recalibrarRango(imgM, 0, mayor);

        for (int i = 0; i < im.getWidth(); i++) {
            for (int j = 0; j < im.getHeight(); j++) {
                pixC = ((int) imgM[i][j] << 24) + ((int) imgM[i][j] << 16) + ((int) imgM[i][j] << 8) + (int) imgM[i][j];
                img.setRGB(i, j, pixC);
            }
        }

        return img;
    }

    private float[][] recalibrarRango(float[][] pixeles, float menor, float mayor) {
        float matriz[][] = pixeles;

        for (int i = 0; i < pixeles.length; i++) {
            for (int j = 0; j < pixeles[0].length; j++) {
                matriz[i][j] = (pixeles[i][j] * 255f) / (Math.abs(menor) + Math.abs(mayor));
            }
        }

        return matriz;
    }

    public BufferedImage watershed(BufferedImage marcadores) {
//        im = binarizar(im, obtenerOtsu(im));

        ArrayList<int[]> colores = new ArrayList<int[]>();
        ArrayList<Integer> col = new ArrayList<>();
        col.add(16777215);

        BufferedImage img = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im, 0, 0, null);
        gra.dispose();

        int wt = 0;

//        im = binarizar(im, obtenerOtsu(im));
        BufferedImage img2 = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());

        Graphics gra2 = img2.getGraphics();
        gra2.drawImage(marcadores, 0, 0, null);
        gra2.dispose();

        int pixC, alf, r, g, b;
        int contB, contB2, contBB;
        boolean primera;

        for (int i = 255; i > wt; i--) {
            contB = 0;
            contB2 = 0;
            contBB = 0;
            primera = true;
            do {
                System.out.println(i);

                img = binarizar(im, i);
//            img = AndOrXor(AndOrXor(binarizar(img2, 1),img,'x'),img,'>');
//            img = AndOrXor(img, img2, 'x');
                for (int j = 0; j < im.getWidth(); j++) {
                    for (int k = 0; k < im.getHeight(); k++) {
                        if ((img2.getRGB(j, k) & 16777215) > 0) {
                            
                                img.setRGB(j, k, img2.getRGB(j, k));
                                
                            if(!col.contains(img.getRGB(j, k)&16777215)){
                                int color[] = {j, k, img.getRGB(j, k)};
                                colores.add(color);
                                col.add(img.getRGB(j, k)&16777215);
                            }
                            
                        }
                    }
                }

                boolean fin;
                double distancia;
                for (int j = 0; j < im.getWidth(); j++) {
                    for (int k = 0; k < im.getHeight(); k++) {
                        int vecinos = contBB;
                        if ((img.getRGB(j, k) & 16777215) == 16777215) {
                            fin = false;
                            distancia = 0;
                            for (int jj = j - 1; jj <= j + 1 && !fin; jj++) {
                                for (int kk = k - 1; kk <= k + 1 && !fin; kk++) {
                                    if (jj == j && kk == k) {
                                        continue;
                                    }
                                    try {
                                        if ((img.getRGB(jj, kk) & 16777215) == 0) {
                                            vecinos++;
                                        } else {
                                            if ((img.getRGB(jj, kk) & 16777215) < 16777215) {
                                                pixC = img.getRGB(jj, kk);
                                                for (int[] c : colores) {
                                                    if (c[2] == pixC) {
                                                        if (distancia == 0 || distancia > Math.sqrt(Math.pow(j - c[0], 2) + Math.pow(k - c[1], 2))) {
                                                            distancia = Math.sqrt(Math.pow(j - c[0], 2) + Math.pow(k - c[1], 2));
                                                            img2.setRGB(j, k, pixC);
                                                            img.setRGB(j, k, 0);
                                                            vecinos = 0;
                                                        }
                                                    }
                                                }

//                                                fin = true;
                                            }
                                        }
                                    } catch (ArrayIndexOutOfBoundsException e) {
                                        vecinos++;
                                    }
                                }
                            }

//                            obtenerOtsu(-1);
                            if (vecinos == 8) {
                                if (i > 50) {
                                    pixC = ((int) ((j * 255.0) / im.getWidth()) << 16) + ((int) ((k * 255.0) / im.getHeight()) << 8) + (int) ((Math.random() * 253) + 1);
                                    img.setRGB(j, k, pixC);
                                    int color[] = {j, k, img.getRGB(j, k)};
                                    colores.add(color);
                                } else {
                                    if (primera) {
                                        img.setRGB(j, k, 0);
                                        contB++;
                                    } else {
                                        img.setRGB(j, k, 0);
                                        contB2++;
                                    }
                                }
                            } else {
                                if ((img.getRGB(j, k) & 16777215) == 16777215) {
                                    if (primera) {
                                        img.setRGB(j, k, 0);
                                        contB++;
                                    } else {
                                        img.setRGB(j, k, 0);
                                        contB2++;
                                    }
                                }
                            }
                        }
                    }
                }
                img2 = AndOrXor(img2, img, '>');
                System.out.println("\t" + contBB);
                System.out.println("\t" + contB + " - " + contB2);
                if (!primera) {
                    if (contB == contB2) {
                        contBB++;
                        contB2 = 0;
                    } else {
                        contB = contB2;
                        contB2 = 0;
                        contBB = 0;
                    }
                }
                primera = false;
            } while (contB > 0 && contBB < 8);
        }

//            img = binarizarUno(img2, colores.get(6));
//            GlobuloRojo gr = new GlobuloRojo(img, colores.get(6));
//            globulos.add(gr);
//            gr.describirContorno();
//        int color[];
//        for(int i=0; i<colores.size(); i++){
//            System.out.println(i);
//            color = colores.get(i);
//            img = binarizarUno(img2, color);
//            GlobuloRojo gr = new GlobuloRojo(img,color);
//            globulos.add(gr);
//            gr.describirContorno();
//        }
        
        for (int[] color : colores) {
            img = binarizarUno(img2, color);
            GlobuloRojo gr = new GlobuloRojo(img, color);
            globulos.add(gr);
            gr.describirContorno();
        }

        return img2;
    }

    public BufferedImage erosion(BufferedImage im, char[][] kern) {
        im = binarizar(im, obtenerOtsu(im, -1));

        BufferedImage img = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        Graphics gra = img.getGraphics();
//        gra.drawImage(newImage, 0, 0, null);
//        gra.dispose();
//        gra.setColor(Color.white);
//        gra.fillRect(0, 0, im.getWidth(), im.getHeight());

        int conta = 0;
//        for (int iM = 0; iM < kern.length; iM++) {
//            for (int jM = 0; jM < kern[iM].length; jM++) {
//                if (kern[iM][jM] == '0') {
//                    conta++;
//                }
//            }
//        }
        conta = kern.length * kern[0].length;

        int pixC, alf, r, g, b;
        int cont;
        for (int i = kern.length / 2; i < im.getWidth() - kern.length / 2; i++) {
            for (int j = kern[0].length / 2; j < im.getHeight() - kern[0].length / 2; j++) {
                cont = 0;
                for (int iM = 0; iM < kern.length; iM++) {
                    for (int jM = 0; jM < kern[iM].length; jM++) {

                        if (kern[iM][jM] == 'x') {
                            cont++;
                            continue;
                        } else {
                            if (((im.getRGB(i + iM - (int) (kern.length / 2), j + jM - (int) (kern[iM].length / 2)) & 1) + "").charAt(0) == kern[iM][jM]) {
                                cont++;
                            } else {
                                cont = 0;
                                break;
                            }
                        }
                    }
                }
                if (cont == conta) {
                    pixC = ((int) 255 << 24) + ((int) 255 << 16) + ((int) 255 << 8) + (int) 255;
                    img.setRGB(i, j, pixC);
                }
//                else{
//                    
//                }
            }
        }

        return img;
    }

    public BufferedImage AndOrXor(BufferedImage im, BufferedImage img2, char andor) {
        int width, height;
        if (im.getWidth() < img2.getWidth()) {
            width = im.getWidth();
        } else {
            width = img2.getWidth();
        }
        if (im.getHeight() < img2.getHeight()) {
            height = im.getHeight();
        } else {
            height = img2.getHeight();
        }
        BufferedImage img = new BufferedImage(width, height, im.getType());
//    	Graphics gra = img.getGraphics();
//	    gra.drawImage(im, 0, 0, null);
//	    gra.dispose();

        int pixC = 0, pixC2 = 0;
        int alf, r, g, b;
        int alf2, r2, g2, b2;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                pixC = im.getRGB(i, j);
                alf = ((pixC >> 24) & 255);
                r = ((pixC >> 16) & 255);
                g = ((pixC >> 8) & 255);
                b = (pixC & 255);
                pixC2 = img2.getRGB(i, j);
                alf2 = ((pixC2 >> 24) & 255);
                r2 = ((pixC2 >> 16) & 255);
                g2 = ((pixC2 >> 8) & 255);
                b2 = (pixC2 & 255);

                if (andor == '<') {
                    alf &= alf2;
                    r &= r2;
                    g &= g2;
                    b &= b2;
                } else {
                    if (andor == '>') {
                        alf |= alf2;
                        r |= r2;
                        g |= g2;
                        b |= b2;
                    } else {
                        if (andor == 'x') {
                            alf ^= alf2;
                            r ^= r2;
                            g ^= g2;
                            b ^= b2;
                        }
                    }
                }

                pixC = (alf << 24) + (r << 16) + (g << 8) + b;

                img.setRGB(i, j, pixC);
            }
        }

        return img;
    }

    public BufferedImage binarizarUno(BufferedImage im, int umbral[]) {
        BufferedImage img = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im, 0, 0, null);
        gra.dispose();

        int pixC = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                pixC = img.getRGB(i, j);

                if (pixC == umbral[2]) {
                    pixC = (255 << 24) + (255 << 16) + (255 << 8) + 255;
                } else {
                    pixC = 0;
                }

                img.setRGB(i, j, pixC);
            }
        }

        return img;
    }

    public static BufferedImage componentesC(BufferedImage im, boolean rellenar) {

        int margen = 0;

        BufferedImage img = new BufferedImage(im.getWidth() + (margen * 2), im.getHeight() + (margen * 2), im.getType());
        Graphics gra = img.getGraphics();
        gra.drawImage(im, margen, margen, null);
        gra.dispose();

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                if (j == 0 || j == img.getHeight() - 1 || i == 0 || i == img.getWidth() - 1) {
                    img.setRGB(i, j, 16777215);
                }
            }
        }

        int cont = 100;

        for (int j = 0; j < img.getHeight(); j++) {
            for (int i = 0; i < img.getWidth(); i++) {
                if ((img.getRGB(i, j) & 255) != 0) {
//                    img.setRGB(i, j, cont++);
                    img.setRGB(i, j, cont);
                    cont += 100;
                }
            }
        }

        int contDif = 1;

        while (contDif != 0) {
            contDif = 0;
            for (int j = 0; j < img.getHeight(); j++) {
                for (int i = 0; i < img.getWidth(); i++) {
//                System.out.println(img.getRGB(i, j));
                    if ((img.getRGB(i, j) & 16777215) != 0) {
                        for (int l = j - 1; l <= j + 1; l++) {
                            for (int k = i - 1; k <= i + 1; k++) {
                                try {
                                    if ((img.getRGB(k, l) & 16777215) != 0) {
                                        if ((img.getRGB(k, l) & 16777215) < (img.getRGB(i, j) & 16777215)) {
                                            img.setRGB(i, j, img.getRGB(k, l));
                                        } else {
                                            img.setRGB(k, l, img.getRGB(i, j));
                                        }
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    }

                }
            }

            for (int j = img.getHeight() - 1; j >= 0; j--) {
                for (int i = img.getWidth() - 1; i >= 0; i--) {
//                System.out.println(img.getRGB(i, j));
                    if ((img.getRGB(i, j) & 16777215) != 0) {
                        for (int l = j + 1; l >= j - 1; l--) {
                            for (int k = i + 1; k >= i - 1; k--) {
                                try {
                                    if ((img.getRGB(k, l) & 16777215) != 0 && (img.getRGB(k, l) & 16777215) < (img.getRGB(i, j) & 16777215)) {
                                        img.setRGB(i, j, img.getRGB(k, l));
                                        break;
                                    }
                                } catch (Exception e) {

                                }
                            }
                        }
                    }

                }
            }

            for (int j = 0; j < im.getHeight(); j++) {
                for (int i = 0; i < im.getWidth(); i++) {
                    if ((im.getRGB(i, j) & 16777215) != (img.getRGB(i + margen, j + margen) & 16777215)) {
                        contDif++;
                        im.setRGB(i, j, img.getRGB(i + margen, j + margen));
                    }
                }
            }
            System.out.println("asdas");

        }

        if (rellenar) {
            ArrayList<Integer> colores = new ArrayList();
            ArrayList<Integer> cantidad = new ArrayList();
            int color;

            for (int j = 0; j < im.getHeight(); j++) {
                for (int i = 0; i < im.getWidth(); i++) {
                    color = im.getRGB(i, j) & 16777215;
                    if (color == 0) {
                        continue;
                    }

                    if ((colores.indexOf(color)) == -1) {
                        colores.add(color);
                        cantidad.add(1);
                    } else {
                        cantidad.set(colores.indexOf(color), cantidad.get(colores.indexOf(color)) + 1);
                    }
                }
            }

            int index = 0;

            for (int i = 1; i < colores.size(); i++) {
                if (cantidad.get(i) > cantidad.get(index)) {
                    index = i;
                }
            }

            color = colores.get(index);

            for (int j = 0; j < im.getHeight(); j++) {
                for (int i = 0; i < im.getWidth(); i++) {
                    if ((im.getRGB(i, j) & 16777215) != color) {
                        im.setRGB(i, j, 0);
                    } else {
                        im.setRGB(i, j, 16777215);
                    }
                }
            }
        }

        return im;
    }

    public static BufferedImage recortarM(BufferedImage im) {
        int xmin = im.getWidth();
        int xmax = 0;
        int ymin = im.getHeight();
        int ymax = 0;

        for (int j = 0; j < im.getHeight(); j++) {
            for (int i = 0; i < im.getWidth(); i++) {
                if ((im.getRGB(i, j) & 16777215) != 0) {
                    if (i < xmin) {
                        xmin = i;
                    }
                    if (i > xmax) {
                        xmax = i;
                    }
                    if (j < ymin) {
                        ymin = j;
                    }
                    if (j > ymax) {
                        ymax = j;
                    }
                }
            }
        }
        System.out.println(xmax + " " + xmin + " " + ymax + " " + ymin);

        BufferedImage img = im.getSubimage(xmin, ymin, xmax - xmin, ymax - ymin);

        return img;
    }

    public static BufferedImage and(BufferedImage im, BufferedImage img2) {

        BufferedImage img = new BufferedImage(im.getWidth(), im.getHeight(), im.getType());

        int pixC = 0, pixC2 = 0;

        for (int i = 0; i < img.getWidth(); i++) {
            for (int j = 0; j < img.getHeight(); j++) {
                pixC = im.getRGB(i, j) & 16777215;
                pixC2 = img2.getRGB(i, j) & 16777215;

                img.setRGB(i, j, pixC & pixC2);
            }
        }

        return img;
    }

    public BufferedImage getIm() {
        return im;
    }

    public void setIm(BufferedImage im) {
        this.im = im;
    }
}
