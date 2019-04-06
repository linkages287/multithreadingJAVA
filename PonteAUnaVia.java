package ponteaunavia;


import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;
import ponteaunavia.Auto.Direzione;

public class PonteAUnaVia {
    public static void main(String[] args) {
     
        long           tempoMedioNordSud, tempoMedioSudNord; // calcolo delle medie
        boolean        direzioneNumVal;           // direzione casuale
        Direzione      dir_auto;                  // direzione automobile passata al costruttore
        int            numeroMacchine;            // numero macchine totali, dato inserito dall'utente
        int            numeroMacchineSulPonte;    // numero macchine insieme sul ponte, dato inserito dall'utente
        int            tempoAttesaArrivo;         // variabile di tempo di arrivo passata al costruttore
        BufferedReader in               = new BufferedReader(new InputStreamReader(System.in));
        String         stringaInputDati = null;
        Random         generatore       = new Random();
      
        // blocco inserimento dati dall'utente
        System.out.println("\nInserisci il numero di automobili totale:\n");

        try {
            stringaInputDati = in.readLine();
        } catch (IOException ex) {System.out.println("\nErrore inserimento dati!");}

       try {
            numeroMacchine = Integer.valueOf(stringaInputDati);
       } catch (NumberFormatException exc) {
       
         System.out.println("\nProseguo con dati di DEFAULT (numero auto=10)");
         numeroMacchine = 10;
       }
                        
        System.out.println("\nInserisci il numero di automobili che possono percorrere il ponte contemporaneamente:\n");

        try {
            stringaInputDati = in.readLine();
        } catch (IOException ex) {
            System.out.println("Errore inserimento dati!");
        }
        try{
        numeroMacchineSulPonte = Integer.valueOf(stringaInputDati);
        } catch (NumberFormatException ex) 
        {
         System.out.println("\nProseguo con dati di DEFAULT (numero massimo auto =3)");
         numeroMacchineSulPonte = 3;
        }
       
       if (numeroMacchine>0){
           if (numeroMacchineSulPonte>0){
       
        Auto  automobili[] = new Auto[numeroMacchine];// creazione del numero di macchine corrispondenti ai threads
        Coda  codaA        = new Coda(numeroMacchine);
        Ponte ponte        = new Ponte(numeroMacchine, numeroMacchineSulPonte, codaA);// definizione oggetto ponte condiviso con la limitazio

        for (int k = 0; k < numeroMacchine;
                k++)                                            // crea macchine
        {
            direzioneNumVal = generatore.nextBoolean(); // calcolo la direzione casuale

            if (direzioneNumVal) { dir_auto = Direzione.NORD_SUD;// assegno la direzione casuale alla macchina
            } else {  dir_auto = Direzione.SUD_NORD; }         

            tempoAttesaArrivo = generatore.nextInt(11) + 10;// calcolo valore casuale di attesa tra 10 ms  e 20 ms
            automobili[k]     = new Auto(codaA, ponte, k, dir_auto, tempoAttesaArrivo);
        }

        for (int k = 0; k < numeroMacchine; k++) {
            automobili[k].start();
        }    // start dei threads

        try {
            for (int k = 0; k < numeroMacchine; k++) {
                automobili[k].join();    // attesa che l'automobile finisca
            }
        } catch (InterruptedException ex) {}

        // controllo esistenza degli zeri nel calcolo della media per evitare gli errori di divisioni per 0
        if (ponte.nordSud == 0) {  ponte.nordSud = 1; }

        if (ponte.sudNord == 0) {   ponte.sudNord = 1; }

        tempoMedioNordSud = ponte.tempoAttesaNordSud / ponte.nordSud;    // calcolo media auto NORD_SUD
        tempoMedioSudNord = ponte.tempoAttesaSudNord / ponte.sudNord;    // calcolo media auto SUD_NORD
        
        System.out.println("\nTempo di attesa medio per le auto con direzione NORD_SUD " + tempoMedioNordSud);
        System.out.println("\nTempo di attesa medio per le auto con direzione SUD_NORD " + tempoMedioSudNord);
           } else  System.out.println("\n Sul ponte non possono passare macchine in questo momento!");
         
       } else System.out.println("\nNon sono presenti macchine in attesa"); 
       }}
