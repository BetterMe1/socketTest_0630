package exercise.socketTest_0629.multi;

import java.io.IOException;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

class ReadFromServer implements  Runnable{
    private Socket client;

    public ReadFromServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //获取客户端输入流
        Scanner sc = null;
        try {
            sc = new Scanner(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        while(true){
            if(client.isClosed()){
                System.out.println("客户端已关闭");
                break;
            }
            if(sc.hasNextLine()){
                System.out.println("客户端发来的消息："+sc.nextLine());
            }
        }
        sc.close();
    }
}
class WriteToServer implements Runnable{
    private Socket client;

    public WriteToServer(Socket client) {
        this.client = client;
    }

    @Override
    public void run() {
        //获取键盘输入
        Scanner sc = new Scanner(System.in);
        //获取客户端输出流
        PrintStream out;
        try {
            out = new PrintStream(client.getOutputStream());
            while(true){
                Thread.sleep(100);
                System.out.println("请输入要发送的信息：");
                String strToServer;
                if(sc.hasNextLine()){
                    strToServer = sc.nextLine().trim();
                    out.println(strToServer);
                    if(strToServer.contains("bye")){
                        System.out.println("关闭客户端");
                        client.close();
                        sc.close();
                        out.close();
                        break;
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
public class MultiThreadClient {
    public static void main(String[] args){
        Socket client = null;
        try {
            client = new Socket("127.0.0.1",8888);
            Thread readFromServerThread = new Thread(new ReadFromServer(client));
            Thread writeToServerThread = new Thread(new WriteToServer(client));
            readFromServerThread.start();
            writeToServerThread.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

