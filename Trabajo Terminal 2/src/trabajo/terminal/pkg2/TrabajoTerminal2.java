/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package trabajo.terminal.pkg2;

import com.sun.java.accessibility.util.SwingEventMonitor;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.ContainerEvent;
import java.awt.event.ContainerListener;
import java.awt.event.WindowEvent;
import java.awt.event.WindowListener;
import java.awt.image.BufferedImage;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.PostConstruct;
import javax.imageio.ImageIO;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTabbedPane;
import javax.swing.JToolBar;
import javax.swing.JViewport;
import javax.swing.filechooser.FileFilter;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author cavca
 */
public class TrabajoTerminal2 implements ActionListener, ComponentListener{

    JFrame frame;
    JMenuBar menuBar;
    JMenu menu;
//    JMenuItem menuI1;
    JMenuItem menuI2;
    JMenuItem menuI3;
    JFileChooser choose;
    FileFilter imageFilter;
    JTabbedPane pestanias;
    JButton bot1;
    JPanel pan;
    JLabel jlImg;
    JSplitPane splitP;
    BufferedImage img;
    int width;
    int height;
    
    ArrayList<Imagen> imagenes = new ArrayList();

    public TrabajoTerminal2() {
        frame = new JFrame("TrabajoTerminal");
        
        menuBar = new JMenuBar();
        menu = new JMenu("Archivo");
//        menuI1 = new JMenuItem("Nueva Imagen");
        menuI2 = new JMenuItem("Abrir imagen");
        menuI3 = new JMenuItem("Guardar Imagen");
        pan = new JPanel();
        
//        menu.add(menuI1);
        menu.add(menuI2);
        menu.add(menuI3);
        menuBar.add(menu);
        
        width = Toolkit.getDefaultToolkit().getScreenSize().width;
        height = Toolkit.getDefaultToolkit().getScreenSize().height;
        
        frame.setMinimumSize(new Dimension(1000, 500));
        
        choose = new JFileChooser();
        imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
        bot1 = new JButton("Analizar");
        pestanias = new JTabbedPane();
        
        splitP = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,pestanias,pan);
        splitP.setOneTouchExpandable(false);
//        splitP.setDividerLocation((int)(Toolkit.getDefaultToolkit().getScreenSize().width*.7));
//        splitP.setDividerLocation((int)(frame.getWidth()*.7));
        splitP.setEnabled(false);
        
        bot1.setBounds((int)(width*.3)/2-(int)(width*.3/3/2), (int)(height*.7), (int)(width*.3/3), 80);
        
        pan.add(bot1);
        
        menuI3.setEnabled(false);
        bot1.setEnabled(false);
        
        frame.setJMenuBar(menuBar);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
        
        
        
        frame.add(splitP);

        frame.setVisible(true);
        
        frame.addComponentListener(this);
        
//        splitP.setDividerLocation((int)(frame.getWidth()*.7));
        
//        menuI1.addActionListener(this);
        menuI2.addActionListener(this);
        menuI3.addActionListener(this);
        bot1.addActionListener(this);
    }
    
    public static void main(String[] args) {
        TrabajoTerminal2 tt2 = new TrabajoTerminal2();
    }

    @Override
    public void actionPerformed(ActionEvent e) {
       
//        if (e.getSource() == menuI1) {
////            System.out.println("Hola");
//
//            JPanel pan;
//            JScrollPane panelPane;
//            BufferedImage bi = new BufferedImage(
//                    500,
//                    500,
//                    BufferedImage.TYPE_INT_RGB);
//
//            pan = new JPanel();
//            jlImg = new JLabel();
//            jlImg.setIcon(new ImageIcon(bi));
//            panelPane = new JScrollPane(jlImg, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
//            panelPane.setPreferredSize(new Dimension((int)(frame.getWidth()*.7), (int)(frame.getHeight())));
//            pan.add(panelPane);
//            pestanias.add("nueva.png", pan);
//            e.setSource(bot1);
//        }
        if (e.getSource() == menuI2) {
            System.out.println("Hola");
            
            choose.addChoosableFileFilter(imageFilter);
            choose.setAcceptAllFileFilterUsed(false);
            choose.showOpenDialog(null);
            File abre = choose.getSelectedFile();
            try {
                img = ImageIO.read(abre);
                System.out.println(abre.getAbsolutePath());
                choose.setCurrentDirectory(abre);
            } catch (IOException ex) {
                Logger.getLogger(TrabajoTerminal2.class.getName()).log(Level.SEVERE, null, ex);
            }
            JPanel pan = new JPanel();
            
            int imgW=(int)(frame.getWidth()*.7);
            int imgH=(int)(img.getHeight()*(frame.getWidth()*.7f)/img.getWidth());
            
            imgW/=4;
            imgH/=4;
            
            System.out.println((img.getHeight()+" * "+(frame.getWidth()*.7)+" / "+img.getWidth())+" = "+(img.getHeight()*(frame.getWidth()*.7)/img.getWidth()));
            Image imag = img.getScaledInstance(imgW, imgH, Image.SCALE_SMOOTH);
            
            img = new BufferedImage(imgW, imgH, img.getType());

            Graphics2D g2d = img.createGraphics();
            g2d.drawImage(imag, 0, 0, null);
            g2d.dispose();
            
            Imagen im = new Imagen(img);
            imagenes.add(im);
            jlImg = new JLabel();
            
            jlImg.setIcon(new ImageIcon(img));
            
            JScrollPane panelPane = new JScrollPane(jlImg, JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
            panelPane.setPreferredSize(new Dimension((int)(frame.getWidth()*.7), (int)(frame.getHeight())));
            pan.add(panelPane);

            boolean exist = false;
            int cont = 0;
            String nombre;
            do {
                exist = false;
                if (cont != 0) {
                    nombre = abre.getName() + "(" + cont + ")";
                } else {
                    nombre = abre.getName();
                }
                for (int i = 0; i < pestanias.getComponentCount(); i++) {
                    if (nombre.equals(pestanias.getTitleAt(i))) {
                        exist = true;
                    }
                    if (exist) {
                        cont++;
                        break;
                    }
                }
            } while (exist);

            pestanias.add(nombre, pan);

            pestanias.setSelectedIndex(pestanias.getComponentCount() - 1);
        }
        if (e.getSource() == menuI3) {
            
            BufferedImage bi = imagenes.get(pestanias.getSelectedIndex()).im;

            try {
//                JFileChooser choose = new JFileChooser();
                FileFilter imageFilter = new FileNameExtensionFilter("Image files", ImageIO.getReaderFileSuffixes());
                choose.setAcceptAllFileFilterUsed(false);
                choose.addChoosableFileFilter(imageFilter);
                choose.showSaveDialog(null);
//                File guardar=choose.getSelectedFile();

                String path;
                if (choose.getSelectedFile().getAbsolutePath().contains(".")) {
                    path = choose.getSelectedFile().getAbsolutePath().substring(0, choose.getSelectedFile().getAbsolutePath().indexOf("."));
//                    path += pestanias.getTitleAt(pestanias.getSelectedIndex()).substring(pestanias.getTitleAt(pestanias.getSelectedIndex()).indexOf("."));
                    path += ".bmp";
                } else {
                    System.out.println(pestanias.getTitleAt(pestanias.getSelectedIndex()));
//                    path = choose.getSelectedFile().getAbsolutePath() + pestanias.getTitleAt(pestanias.getSelectedIndex()).substring(pestanias.getTitleAt(pestanias.getSelectedIndex()).indexOf("."));
                    path = choose.getSelectedFile().getAbsolutePath() + ".bmp";
                }
                File guardar = new File(path);

                ImageIO.write(bi, pestanias.getTitleAt(pestanias.getSelectedIndex()).substring(pestanias.getTitleAt(pestanias.getSelectedIndex()).indexOf(".") + 1), guardar);
            } catch (Exception ex) {
                Logger.getLogger(TrabajoTerminal2.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if (e.getSource() == bot1) {
            jlImg.setIcon(new ImageIcon(imagenes.get(pestanias.getSelectedIndex()).analizar()));
        }
        
        
        if (pestanias.getComponentCount() > 0) {
            menuI3.setEnabled(true);
            
            bot1.setEnabled(true);
//            for (int i = 0; i < toolBar.getComponents().length; i++) {
//                toolBar.getComponent(i).setEnabled(true);
//            }
        }
    }

    public void abrirImagen(){
        
    }
    
    public void guardarImagen(){
    
    }
            
    
    
    
    
    
    
    
    
    
    
    
    
    
    @Override
    public void componentResized(ComponentEvent e) {
//        System.out.println("a");
        splitP.setDividerLocation((int)(frame.getWidth()*.7));
        width = Toolkit.getDefaultToolkit().getScreenSize().width;
        height = Toolkit.getDefaultToolkit().getScreenSize().height;
        bot1.setBounds((int)(width*.3)/2-(int)(width*.3/3/2), (int)(height*.7), (int)(width*.3/3), 80);
    }

    @Override
    public void componentMoved(ComponentEvent e) {
//        System.out.println("b");
//        splitP.setDividerLocation((int)(frame.getWidth()*.7));
    }

    @Override
    public void componentShown(ComponentEvent e) {
//        System.out.println("c");
    }

    @Override
    public void componentHidden(ComponentEvent e) {
//        System.out.println("d");
    }
}
