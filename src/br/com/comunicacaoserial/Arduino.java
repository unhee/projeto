package br.com.comunicacaoserial;

import javax.swing.JButton;

/**
 *
 * @author politecnico
 */
public class Arduino {

    public  static String sinalLigaRele = "2";
    public static String sinalDesligaRele = "1";
    
    private static ControlePorta arduino;


    public static void main(String args[]) {
        arduino = new ControlePorta("COM3", 9600);//Windows - porta e taxa de transmissão
        // arduino2 = new ControlePorta("COM6", 9600);/mais de um arduino

        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                arduino.enviaDados(Arduino.sinalLigaRele);
            }
        });

    }

    public Arduino() {

    }

}
