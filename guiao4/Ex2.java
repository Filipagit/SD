import java.util.concurrent.locks.*;

class Barrier {
    Lock l = new ReentrantLock();
    Condition c = l.newCondition();

    private int contador = 0;
    final int N;
    private int etapa=0;

    Barrier(int N) {
        this.N = N;
    }

    void await() throws InterruptedException {
        l.lock();
        try {
            int etapa=this.etapa;
            contador +=1;
            if (contador <N) {
                while (etapa==this.etapa)
                    c.await();
            } else {
                c.signalAll();
                contador=0;
                this.etapa+=1;
        }

        } finally {
            l.unlock();
        }
    }
}
