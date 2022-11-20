import java.util.concurrent.locks.*;

class Bank {

    private static class Account {
        private int balance;
        private Lock l = new ReentrantLock();

        Account(int balance) {
            this.balance = balance;
        }

        int balance() {
            return balance;
        }

        boolean deposit(int value) {
            balance += value;
            return true;
        }

        boolean withdraw(int value) {
            if (value > balance)
                return false;
            balance -= value;
            return true;
        }
    }

    // Bank slots and vector of accounts
    private final int slots; //constante
    private Account[] av;


    public Bank(int n) {
        slots = n;
        av = new Account[slots];
        for (int i = 0; i < slots; i++) av[i] = new Account(0);
    }

    // Account balance
    public int balance(int id) {
        if (id < 0 || id >= slots)
            return 0;
        Account c = av[id];
        c.l.lock();
        try {
            return av[id].balance();
        } finally {
            c.l.unlock();
        }

    }


    // Deposit
    boolean deposit(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        Account c = av[id];
        c.l.lock();
        try {
            return av[id].deposit(value);
        } finally {
            c.l.unlock();
        }
    }

    // Withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        if (id < 0 || id >= slots)
            return false;
        Account c = av[id];
        c.l.lock();
        try {
            return av[id].withdraw(value);
        } finally {
            c.l.unlock();
        }

    }

    public boolean transfer(int from, int to, int value) {
        if (from < 0 || from >= slots || to < 0 || to >= slots)
            return false;
        Account cfrom = av[from];
        Account cto = av[to];
        if (from < to) {
            cfrom.l.lock();
            cto.l.lock();
        } else {
            cto.l.lock();
            cfrom.l.lock();
        } try{
        try {
            //boolean b = withdraw(from,value);
            boolean b = cfrom.withdraw(value);
            if (!b) return false;
        } finally {
            cfrom.l.unlock();
        }
        return cto.deposit(value);
    }finally

    {
        cto.l.unlock();
    }

}

    public  int totalBalance() {

        int total = 0;
        for (int i = 0; i < slots; i++)
            av[i].l.lock();
            for (int i = 0; i < slots; i++) {
                total += av[i].balance();
                av[i].l.unlock();
            }
        return total;
    }
}
