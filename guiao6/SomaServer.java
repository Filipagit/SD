import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.locks.*;

class ClientHandler extends Thread{
    private Socket socket;
    private Register register;
    ClientHandler(Socket socket,Register register){
        this.socket=socket;
        this.register=register;
    }
    public void run(){
        try{
            BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            PrintWriter out = new PrintWriter(socket.getOutputStream());
            int soma=0;
            int n=0;
            String line;
            while ((line = in.readLine()) != null) {
                n+=1;
                int num=Integer.parseInt(line);
                soma+=num;
                register.add(num);
                out.println(soma);
                out.flush();
            }
            int media= register.media();
            out.println(media);
            out.flush();
            socket.close();
        }catch (IOException e){}

    }
}
class Register{
    Lock l = new ReentrantLock();
    int soma=0;
    int n=0;
    void add(int num){
        l.lock();
        try {
            soma+=num;
            n+=1;
        }finally {
            l.unlock();
        }

    }
    int media(){
        l.lock();
        try {
            return soma / n;
        } finally {
            l.unlock();
        }
        }
}
public class SomaServer {

    public static void main(String[] args) {
        try {
            ServerSocket ss = new ServerSocket(12345);
            Register register= new Register();
            while (true) {
                Socket socket = ss.accept();
                new ClientHandler(socket,register).start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}