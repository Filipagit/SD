class Bank {

    private static class Account {
        private int balance;
        Account(int balance) { this.balance = balance; }
        int balance() { return balance; }
        boolean deposit(int value) {
            balance += value;
            return true;
        }
    }

    // Our single account, for now
    private Account savings = new Account(0);

    // Account balance
    public int balance() {
        return savings.balance();
    }

    // Deposit
    boolean deposit(int value) {
        return savings.deposit(value);
    }
}
class Depositor  implements Runnable {
        long I;
        final Bank b;
        public Depositor(long I,Bank b){this.I=I;this.b=b;}
        public void run(){
        for (long i = 0; i < I; i++)
            b.deposit(100);
    }
}

class Main{
    public static void main(String[] args) throws InterruptedException{
        int I=1000;
        int N=10;
        Thread[] a = new Thread[N];
        Bank b = new Bank();
        for(int i =0;i<N;i++)
            a[i]=new Thread(new Depositor(I,b));
        for(int i =0;i<N;i++)
            a[i].start();
        for(int i =0;i<N;i++)
            a[i].join();
        System.out.println(b.balance());
    }
}