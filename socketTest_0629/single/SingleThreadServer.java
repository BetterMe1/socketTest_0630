package exercise.socketTest_0629.single;

import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadServer {
    public static void main(String[] args) throws Exception{
        ServerSocket serverSocket = new ServerSocket(8888);
        System.out.println("等待客户端连接...");
        Socket client = serverSocket.accept();
        System.out.println("连接成功,端口号为"+client.getPort());

        Scanner in = new Scanner(client.getInputStream());
        if(in.hasNextLine()){
            System.out.println("客户端发来的消息："+in.nextLine());
        }
        PrintStream out = new PrintStream(client.getOutputStream());
        out.println("Hi,I am Server!");
        out.close();
        in.close();
        serverSocket.close();
    }
}
