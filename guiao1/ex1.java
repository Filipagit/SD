
class Increment implements Runnable {
    public void run() {
        final long I=100;
        //parametrizar:
        //final long I;
        //public Increment(long I){this.I=I};

        for (long i = 0; i < I; i++)
            System.out.println(i);
    }
}


class Main {
    public static void main(String[] args) throws InterruptedException{
        int I=100;
        int N=10;
        Thread[] a = new Thread[N];
        for(int i =0;i<N;i++)
            a[i]=new Thread(new Increment());
        //a[i]=new Thread(new Increment(I));
        //a[i] =new Thread(inc);<-nao fazer
        for(int i =0;i<N;i++)
            a[i].start();
        for(int i =0;i<N;i++)
            a[i].join();
        System.out.println("FIM");
    }
}