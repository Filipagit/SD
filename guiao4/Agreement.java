import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.*;
import java.util.concurrent.locks.ReentrantLock;


class Agreement {
    Lock l=new ReentrantLock();
    Condition c=l.newCondition();
    int counter=0;

    final int N;
    private static class Stage{
        int max = Integer.MIN_VALUE;
    }
    Stage stage = new Stage();

    Agreement(int N){this.N=N;}

    int propose(int choice) throws InterruptedException{
        l.lock();
        try{
            Stage stage=this.stage;
            counter+=1;
            stage.max=Math.max(stage.max,choice);
            if(counter<N){
                while (stage==this.stage)
                c.await();
            }else{
                c.signalAll();
                counter=0;
                this.stage = new Stage();
            }
            return stage.max;
        }finally {
            l.unlock();
        }
    }

}
