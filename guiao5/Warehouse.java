import java.util.*;
import java.util.concurrent.locks.*;
class Warehouse {
    private Map<String, Product> map =  new HashMap<String, Product>();
    Lock l = new ReentrantLock();

    private class Product{
        int quantity=0;
        Condition c=l.newCondition();
    }

    private Product get(String item) {
        Product p = map.get(item);
        if (p != null) return p;
        p = new Product();
        map.put(item, p);
        return p;
    }

    public void supply(String item, int quantity) {
        l.lock();
        try {
            Product p = get(item);
            p.quantity += quantity;
            p.c.signalAll();
        }finally {
            l.unlock();
        }
    }

    // Errado se faltar algum produto... versão egoísta
    public void consume(Set<String> items) throws InterruptedException {
        l.lock();
        try {
            for (String s : items){
                Product p = get(s);
                    while(p.quantity==0)
                        p.c.await();
                      p.quantity--;
            }
        }finally {
            l.unlock();
        }
    }

    //versão cooperativa
    public void consume2(Set<String> items) throws InterruptedException {
        l.lock();
        try {
            int n = items.size();
            Product[] ps = new Product[items.size()];
            int i = 0;
            for (String s : items) {
                ps[i] = get(s);
                i += 1;
            }int i=0;
            while (i < n ) {
                Product p = ps[i];
                if (p.quantity == 0) {
                    p.c.await();
                    i = 0;
                } else {
                    i += 1;
                }
            }
            for (Product p : ps) {
                p.quantity--;
            }
        } finally {
            l.unlock();
        }
    }
}
