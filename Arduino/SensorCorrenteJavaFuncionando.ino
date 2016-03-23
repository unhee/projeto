//Carrega as bibliotecas

#define limiarMin 0.1
#include "EmonLib.h"

EnergyMonitor emon1;

//Tensao da rede eletrica
int rede = 210.0;

//Pino do sensor SCT
int pino_sct = 5;

// pino do rele
int sinalparaorele = 7;


int byteEntrada = 0;

//variaveis novas
unsigned long tempoInicio = 0;
unsigned long tempoFim = 0;
unsigned long tempoPercorrido = 0;
unsigned long tempoAtual = 0;

void setup()
{
  Serial.begin(9600);
  //Pino, calibracao - Cur Const= Ratio/BurdenR. 1800/62 = 29.
  emon1.current(pino_sct, 26);

  //defini o pino como saída
  pinMode(sinalparaorele, OUTPUT);
  delay(10);
  ligaRele();

}
void ligaRele() {

  digitalWrite(sinalparaorele, HIGH); //Aciona o rel
  tempoInicio = millis();
  tempoFim = 0; //quando tiver 0, é pq ainda nao acabou

}

void desligaRele() {
  digitalWrite(sinalparaorele, LOW);//desliga o rele
  tempoFim = millis();

}

double potencia = 0;
void loop()
{
  if (Serial.available() > 0) {
    byteEntrada = Serial.read();
    if (byteEntrada == '1') {
      desligaRele();
    }
    if (byteEntrada == '2') {
      ligaRele();
    }
  }
  
  //Calcula a corrente
  double Irms = 0;
  int p = 0;
  int AMOSTRAGEM = 20;
  // Filtro de primeira ordem
  for (p = 0; p < AMOSTRAGEM; p++) {
    Irms += emon1.calcIrms(1480);
    delay(10);
  }
  Irms /= AMOSTRAGEM;
  if (Irms < limiarMin) {//se limear
    Irms = 0;
  }
  
  double potenciaAtual = Irms * rede;
  
  if (potencia == 0) {
    potencia = potenciaAtual;
  } else {
    potencia = potenciaAtual; 
  }

  if (tempoInicio == 0) {
    tempoPercorrido = 0;
  }
  else if (tempoFim == 0 ) {
    tempoPercorrido = (millis() - tempoInicio) / 1000; //valor em s

  }
  else {
    tempoPercorrido = (tempoFim - tempoInicio) / 1000; //valor em s
  }

  double reais = (potencia / 1000) * ((float) tempoPercorrido / 3600) * 0.55;

 /* Serial.print("Tempo Inicio: ");
  Serial.print(tempoInicio);
  Serial.print(", Tempo Fim: ");
  Serial.print(tempoFim);
  Serial.print(", Tempo atual: ");
  Serial.print(millis());
  Serial.print(", Potencia: ");
  Serial.println(potenciaAtual);
  Serial.print("-----------");*/

  
  // Imprime em CSV
    Serial.print(potenciaAtual);//0
    Serial.print(",");
    Serial.println(tempoInicio);//1
    Serial.print(",");
    Serial.println(tempoFim);//2
    Serial.print(",");
    Serial.println(millis());//3




  delay(300);
}

