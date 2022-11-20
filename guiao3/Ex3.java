import java.util.concurrent.locks.*;
import java.util.*;

class Bank {

    private static class Account {
        private Lock l = new ReentrantLock();
        private int balance;

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

    private Map<Integer, Account> map = new HashMap<Integer, Account>();
    private ReentrantReadWriteLock l = new ReentrantReadWriteLock();
    Lock rl=l.readLock();
    Lock wl =l.writeLock();
    private int nextId = 0;

    // create account and return account id
    public int createAccount(int balance) {
        Account c = new Account(balance);
        wl.lock();
        try {
            int id = nextId;
            nextId += 1;
            map.put(id, c);
            return id;
        } finally {
            wl.unlock();
        }
    }

    // close account and return balance, or 0 if no such account
    public int closeAccount(int id) {
        Account c;
        wl.lock();
        try {
            c = map.remove(id);
            if (c == null)
                return 0;
            c.l.lock();
        } finally {
            wl.unlock();
        }
        try {
            return c.balance();
        } finally {
            c.l.unlock();
        }
    }


    // account balance; 0 if no such account
    public int balance(int id) {
        Account c ;
        rl.lock();
        try {
            c= map.get(id);
            if (c == null)
                return 0;
        } finally {
            rl.unlock();
        }
        try {
            return c.balance();
        } finally {
            c.l.unlock();
        }

    }

    // deposit; fails if no such account
    public boolean deposit(int id, int value) {
        Account c ;
        rl.lock();
        try {
            c=map.get(id);
            if (c == null)
                return false;
        } finally {
            rl.unlock();
        }
        try {
            return c.deposit(value);
        }finally {
            c.l.unlock();
        }


    }

    // withdraw; fails if no such account or insufficient balance
    public boolean withdraw(int id, int value) {
        Account c ;
        rl.lock();
        try {
            c= map.get(id);
            if (c == null)
                return false;
        }finally {
            rl.unlock();
        }
        try {
            return c.withdraw(value);
        } finally {
            c.l.unlock();
        }

    }

    // transfer value between accounts;
    // fails if either account does not exist or insufficient balance
    public boolean transfer(int from, int to, int value) {
        Account cfrom, cto;
        rl.lock();
        try {
            cfrom = map.get(from);
            cto = map.get(to);
            if (cfrom == null || cto == null)
                return false;
            if(from<to){
                cfrom.l.lock();
                cto.l.lock();
            }
            else{
                cto.l.lock();
                cfrom.l.lock();
            }
        } finally {
            rl.unlock();
        }
        try {
            try{
                boolean b = cfrom.withdraw(value);
                if(!b)
                    return false;
            } finally {
                cfrom.l.unlock();
            }
            return cto.deposit(value);
        }finally {
            cto.l.unlock();
        }
    }

    // sum of balances in set of accounts; 0 if some does not exist falta acabar!!!!!!!!!!!
    public int totalBalance(int[] ids) {
        ids=ids.clone();
        Arrays.sort(ids);
        Account acs[]= new Account[ids.length];
        rl.lock();
        try{
            for (int i=0;i < ids.length;i++) {
                acs[i] = map.get(ids[i]);
                if (acs[i] == null)
                    return 0;
            }
            for(Account c : acs){
                c.l.lock();
            }
        }finally {
            rl.unlock();
        }
        int total=0;
        for(Account c : acs){
            total+=c.balance();
            c.l.unlock();
        }
        return total;
    }

}
