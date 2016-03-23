/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package br.com.comunicacaoserial;

import br.com.comunicacaoserial.ControlePorta;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;

/**
 *
 * @author politecnico
 */
public class Calculo {

    private float array[] = new float[15];
    static float somaPot = 0;
    static float potMedia;
    static float consumo = 0;
    static float preco;
    static float valor;

    //int x = 0;
    // ControlePorta c =   new ControlePorta();
    public static boolean recebeDados(String potencia,
            int x, int cont, String tempoInicio, String tempoAtual) {
     
        
        if (x < 15) {
            somaPot = somaPot + (float) Double.parseDouble(potencia);
            System.out.println("CONTADOR = " + x);
            System.out.println("SOMA POTENCIA = " + somaPot);
        }
        if (x == 14) {
            potMedia = somaPot / cont;
            System.out.println("CHEGOU NO 14, Potencia Media" + potMedia);
            valor = calculaConsumo(potMedia, tempoAtual, tempoInicio);
            System.out.println("PRECO DO COSUMO" + valor);
            return true;
        }
        return false;

    }
    public static float calculaConsumo(float potMedia, String tempoInicio, String tempoAtual) {

        float tempoIn = Float.parseFloat(tempoInicio);
        float tempoAtu = Float.parseFloat(tempoAtual);
        float tempoPercorrido = ((tempoAtu - tempoIn)/ 1000); //Millisegundos para Segundos/1000
        float reais = (float) ((potMedia / 1000) * (tempoPercorrido / 3600) * 0.55); //segundos pra hora
        return reais;
    }

    public Calculo() {
    }

}
