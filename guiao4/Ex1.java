import java.util.concurrent.locks.*;

class Barreira {
    Lock l = new ReentrantLock();
    Condition c = l.newCondition();

    private int contador = 0;
    final int N;

    Barreira(int N) {
        this.N = N;
    }

    void await() throws InterruptedException {
        l.lock();
        contador++;
        if (contador < N) {
            while (contador < N)
                c.await();
        } else {
            c.signalAll();
        }
        l.unlock();
    }
}
