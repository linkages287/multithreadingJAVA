package ponteaunavia;


import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Coda {
    private int             contatoreAlBuffer = 0;
    private int             contatoreInt      = 0;
    private Lock            lock              = new ReentrantLock(true);
    private final Condition nonPieno          = lock.newCondition();
    private final Condition nonVuoto          = lock.newCondition();
    public Auto             buffer[];
    private int             dim;
    private int             primo;
    private int             ultimo;

    public Coda(int dimensione) {
        this.dim               = dimensione;// dimensione della coda
        this.buffer            = new Auto[dimensione];
        this.primo             = 0;
        this.ultimo            = 0;
        this.contatoreAlBuffer = 0;
    }

    public void inserisci(Auto auto) throws InterruptedException {
        lock.lock();

        try {
            buffer[ultimo]      = auto;    // inserisci l'auto nel posto corretto del buffer
            auto.ordineDiArrivo = ultimo;  // assegno all'auto un ordine di arrivo in coda
            ultimo++;
            contatoreAlBuffer++;           // indica gli elementi presenti nella coda
            nonVuoto.signal();
        } finally {
            lock.unlock();
        }
    }

    public Auto rimuovi() {
        lock.lock();

        try {
            while (contatoreAlBuffer == 0) {
                try 
                {
                  nonVuoto.await();
                } catch (InterruptedException ex) {}
            }

            contatoreAlBuffer--;     // indica gli elementi presenti nella coda
            nonPieno.signal();       // segnala al processso in attesa

            return buffer[primo++];  // prende l'elemento della FIFO
        } finally {
            lock.unlock();
        }
    }

    public Auto esaminaAutoInOrdinde()  // esami l'auto in ordine di arrivo il passaggio può essere
                                        // effettuato una volta sola
    {
        lock.lock();
        try {
            while (contatoreAlBuffer == 0) 
            {   
              try { nonVuoto.await();} catch (InterruptedException ex) {}
            }
            if (contatoreInt < this.dim) { return buffer[contatoreInt++];
            } else {   return null;  }
        } finally {
            lock.unlock();
        }       
        }}


