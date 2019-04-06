package ponteaunavia;

public class Auto extends Thread {  
    public Direzione dir               = null;
    long             attesa            = 0;
    public int       ordineDiArrivo    = 0;
    public int       tempoAttesaArrivo = 0;    // tempo di attesa randomico allo start del thread
    public long      tempoStart        = 0;    // calcolo tempi attesa Start
    Coda             codaAuto;                                                            
    Ponte            ponte;        // oggetto ponte condiviso

    public enum Direzione { SUD_NORD, NORD_SUD }; // direzione di marcia dell'auto
                                                                                      
    public Auto(Coda coda, Ponte ponte, int numAuto, Direzione direzione, int tempoA) {   
        super("Automobile_" + numAuto);    // assegna il nome al thread
        this.codaAuto          = coda;
        this.ponte             = ponte;        // assegna l'oggetto ponte condiviso
        this.dir               = direzione;    // assegna la direzione randomica
        this.tempoAttesaArrivo = tempoA;       // assegna il tempo iniziale tra 10ms e 20 ms
    }

    @Override
    public void run() {
        try {
            Thread.sleep(this.tempoAttesaArrivo);    // simulo il tempo attesa arrivo casuale al bordo del ponte
            
            this.ponte.entra(this);    // effettuo la richiesta di entrare sul ponte
            
            this.ponte.esci(this);  // notifico alle altre auto la fine dell'attraversamento
            
            } catch (InterruptedException ex) {}
    }

    
    // metodi usati per definire i tempi di attesa

    public void inizioTempoAttesa() { this.tempoStart = System.currentTimeMillis() ;   }

    public void fineTempoAttesa() { this.attesa= (System.currentTimeMillis() - this.tempoStart); }
    }


