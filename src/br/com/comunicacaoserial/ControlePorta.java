package br.com.comunicacaoserial;

import gnu.io.CommPortIdentifier;
import gnu.io.NoSuchPortException;
import gnu.io.SerialPort;
import gnu.io.SerialPortEvent;
import gnu.io.SerialPortEventListener;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.TooManyListenersException;
import javax.swing.JOptionPane;
import br.com.comunicacaoserial.Calculo;

/**
 *
 * @author Un Hee Schiefelbein
 */
public class ControlePorta implements SerialPortEventListener {

    private OutputStream serialOut;
    private InputStream input = null;
    private int taxa;
    private String portaCOM;
    private SerialPort port;
    double preco = 0d;
    private int i = 1;
    private int cont = 1;
    private String sinalDesligaRele = "1";

    /**
     * Construtor da classe ControlePorta
     *
     * @param portaCOM - Porta COM que será utilizada para enviar os dados para
     * o arduino
     * @param taxa - Taxa de transferência da porta serial geralmente é 9600
     */
    public ControlePorta(String portaCOM, int taxa) {
        this.portaCOM = portaCOM;
        this.taxa = taxa;
        this.initialize();
    }

    public ControlePorta() {
    }

    /**
     * Médoto que verifica se a comunicação com a porta serial está ok
     */
    private void initialize() {
        try {
            //Define uma variável portId do tipo CommPortIdentifier para realizar a comunicação serial
            CommPortIdentifier portId = null;
            try {
                //Tenta verificar se a porta COM informada existe
                portId = CommPortIdentifier.getPortIdentifier(this.portaCOM);
            } catch (NoSuchPortException npe) {
                //Caso a porta COM não exista será exibido um erro 
                JOptionPane.showMessageDialog(null, "Porta COM não encontrada.",
                        "Porta COM", JOptionPane.PLAIN_MESSAGE);
            }
            //Abre a porta COM 
            port = (SerialPort) portId.open("Comunicação serial", this.taxa);
            serialOut = port.getOutputStream();
            input = port.getInputStream();
            port.setSerialPortParams(this.taxa, //taxa de transferência da porta serial 
                    SerialPort.DATABITS_8, //taxa de 10 bits 8 (envio)
                    SerialPort.STOPBITS_1, //taxa de 10 bits 1 (recebimento)
                    SerialPort.PARITY_NONE); //receber e enviar dados

            initListener();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Método que fecha a comunicação com a porta serial
     */
    public void close() {
        try {
            serialOut.close();
        } catch (IOException e) {
            JOptionPane.showMessageDialog(null, "Não foi possível fechar porta COM.",
                    "Fechar porta COM", JOptionPane.PLAIN_MESSAGE);
        }
    }

    /**
     * @param opcao - Valor a ser enviado pela porta serial
     */
    public void enviaDados(String opcao) {
        try {
            serialOut.write(opcao.getBytes());//escreve o valor na porta serial para ser enviado
        } catch (IOException ex) {
            JOptionPane.showMessageDialog(null, "Não foi possível enviar o dado. ",
                    "Enviar dados", JOptionPane.PLAIN_MESSAGE);
        }
    }

    final static int SPACE_ASCII = 32;
    final static int DASH_ASCII = 45;
    final static int NEW_LINE_ASCII = 10;

    private void initListener() {
        try {
            port.addEventListener(this);
            port.notifyOnDataAvailable(true);
            System.out.println("Coloquei o listener! SSOK");
        } catch (TooManyListenersException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void serialEvent(SerialPortEvent evt) {
        String dados = "";
        char singleData = 0;
        if (evt.getEventType() == SerialPortEvent.DATA_AVAILABLE) {
            try {
                Thread.sleep(100);

                while (input.available() > 0) {
                    singleData = (char) input.read();
                    if (singleData != NEW_LINE_ASCII) {
                        dados += singleData;
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        System.out.println("------------------------------");
        System.out.println(dados);

        String[] parts = dados.split(",");

        System.out.println("Potência: " + parts[0]);
        System.out.println("Tempo Inicio: " + parts[1]);
        System.out.println("Tempo fIM: " + parts[2]);
        System.out.println("Tempo atual: " + parts[3]);
        System.out.println("------------------------------");
        
        float somaPot = 0;
        float potMedia;
        float consumo = 0;
        float array[] = new float[15];
        boolean desligar = false;

        if (i < 15) {
            desligar = Calculo.recebeDados(parts[0], i, cont, parts[1], parts[3]); //metodo estatico
        }
        if (desligar) {
            this.enviaDados(Arduino.sinalDesligaRele);
        }
        if (i == 15) {
            this.enviaDados(Arduino.sinalLigaRele);
            i = 1;
            desligar = Calculo.recebeDados(parts[0], i, cont, parts[1], parts[3]);
        }
        System.out.println("VEZES QUE O PROGRAMA RODOU= " + cont);
        cont++;

        i++;
        /*        System.out.println("Tempo Percorrido: " + parts[0]);
         System.out.println("Tensão da Rede: " + parts[1]);
         System.out.println("Corrente: " + parts[2]);
         System.out.println("Potência Média: " + parts[3]);
         preco = Double.parseDouble(parts[4]);
         System.out.println("Valor em Reais: " + preco);

         System.out.println("------------------------------");*/

    }

    public double getPreco() {
        return preco;
    }

    public void teste() {

    }

}
