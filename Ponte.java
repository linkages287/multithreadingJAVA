package ponteaunavia;


import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import ponteaunavia.Auto.Direzione;

public class Ponte   {
    final int        TEMPO_ATTESA         = 100;
    int              autoSulPonte         = 0;// numero di auto che possono stare insieme sul ponte andando nella stessa direzione
    int              autoinAttesa         = 0;// auto in attesa di passare il ponte
    int              autoTotali           = 0;
    public Direzione direzioneRemainder   = null;
    long             tempoAttesaNordSud   = 0,
                     tempoAttesaSudNord   = 0;
    public int       nordSud              = 0,
                     sudNord              = 0;         
    Lock             accessoVariabile     = new ReentrantLock(true);    // lock sulle variabili contatore fairness impostata a true
    Condition attesaCondizionale[];  // array di attese condizionali servono per gestire la FIFO
    int              autoLimiteSulPonte;    // numero di auto limite sul ponte da modificare con un input //
    Coda             codaAuto;

    public Ponte(int autoTotali, int numero, Coda coda)    
            // al ponte viene passata la coda in uso ed il numero
            // di macchine che possono stare contemporaneamente sul ponte e la coda 
            // da cui deve acquisire i dati
    {
        this.autoLimiteSulPonte = numero;
        this.codaAuto           = coda;
        this.autoTotali         = autoTotali;
        this.attesaCondizionale = new Condition[autoTotali];   

        for (int i = 0; i < autoTotali; i++) { this.attesaCondizionale[i] = this.accessoVariabile.newCondition(); }
    }

        public void entra(Auto autoInCoda) {// metodo di entrata bloccante
       
          
            
            try { codaAuto.inserisci(autoInCoda);} catch (InterruptedException ex) {}
        // simulo l'arrivo al bordo del ponte inserendo le auto in una coda in ordine di arrivo
        // la mutua esclusione sull'inserimento è già garantita dal lock nel metodo inserisci()
        // nella classe Coda ad ogni auto viene inoltre assegnato un ordine di arrivo -> auto.ordineDiArrivo
        
        this.accessoVariabile.lock();    // inizio mutua esclusione sul controllo e sull'accesso delle variabili monitor

        Auto auto = codaAuto.esaminaAutoInOrdinde();// viene prelevata sequenzialmente l'auto
                                                    // che in ordine di arrivo chiederà al'accesso al ponte
        
        auto.inizioTempoAttesa();// inizio tempo attesa
             
            while (!((this.autoinAttesa == 0) && (this.autoSulPonte < this.autoLimiteSulPonte)
                  && ((this.direzioneRemainder == null) || (auto.dir == this.direzioneRemainder)))) 
            {   // POLICY DI PASSAGGIO
                // l'auto può entrare sul ponte se rispetta la policy:
                // non ci devono essere altre auto in attesa prima di questa auto
                // non deve superare il limite massimo impostato sul ponte
                // deve seguire la direzione di percorrenza del ponte nell'istante della richiesta
                // se non ce' nessuna auto sul ponte allora puo' entrare anche se la direzione è nulla       
                
                this.autoinAttesa++; // incremento il numero di auto in attesa             
                
                try {this.attesaCondizionale[auto.ordineDiArrivo].await();} catch (InterruptedException ex) {} 
                
              
                // attesa del thread sull'array di conditions.
                
            } // fine while(), fine attesa dell'auto al bordo del ponte
                     
          
            //-------  le auto autorizzate passano sul ponte  ------//
            this.direzioneRemainder = auto.dir; // la direzione di percorrenza del ponte ora è quella dell'auto
            this.autoSulPonte++; // contatore delle auto presenti sul ponte
                            
            
            auto.fineTempoAttesa(); // prima di fare ingresso sul ponte l'auto salva il suo tempo di attesa in auto.attesa
                  
            
                 
            try { this.attesaCondizionale[auto.ordineDiArrivo].await(TEMPO_ATTESA, TimeUnit.MILLISECONDS ); }  catch (InterruptedException ex) {} 
           //l'auto attende 100 ms durante l'attesa viene rilasciato il lock e vengono analizzate le auto a seguire
           // che possono tentare di passare sul ponte se rispettano le policy
             
           // calcolo variabili gestione delle medie
           if (auto.dir == Direzione.NORD_SUD) { 
                nordSud++;
                this.tempoAttesaNordSud = this.tempoAttesaNordSud + auto.attesa;
            } else {
                sudNord++;
                this.tempoAttesaSudNord = this.tempoAttesaSudNord + auto.attesa;
            }
           //------------------------------     
          
                
        }

        public void esci(Auto auto)    // uscita dal ponte
        {
            auto = codaAuto.rimuovi();    // rimuove l'auto dalla coda delle auto seguendo l'ordine di arrivo            
                   
        System.out.println("\nIl tempo di attesa dell'auto " + auto.getName() + " con direzione " + auto.dir + " è di " + auto.attesa + " ARRIVO " + auto.tempoAttesaArrivo );   
            
            this.autoSulPonte--; // decremento il numero di auto che occupano il ponte

            if (this.autoSulPonte == 0) // il ponte è libero se le auto sul ponte sono ==0 
            {
                this.direzioneRemainder = null;    // ponte vuoto, reset della direzione di marcia del ponte
              
              while (this.autoinAttesa!=0)
                {
                    this.autoinAttesa--;
                     
                     this.attesaCondizionale[(this.autoTotali - this.autoinAttesa -1)].signal();
                     
                    // l'ultima auto del gruppo che esce dal ponte effettua il
                    // risveglio FIFO delle auto in attesa tramite un contatore che punta all'ultima auto nota posta
                    // in attesa, infatti  conoscendo il numero di auto ancora in attesa e l'ultima auto posta in attesa
                    // posso sapere anche il blocco di auto da risvegliare in ordine di arrivo.
                }         
                }

         this.accessoVariabile.unlock(); // unlock e fine monitor
    }

}

