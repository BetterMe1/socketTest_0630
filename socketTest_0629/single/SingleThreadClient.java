package exercise.socketTest_0629.single;

import java.io.PrintStream;
import java.net.Socket;
import java.util.Scanner;

public class SingleThreadClient {
    public static void main(String[] args) throws Exception{
        Socket client = new Socket("127.0.0.1",8888);
        System.out.println("与服务器建立连接");

        PrintStream out = new PrintStream(client.getOutputStream());
        out.println("Hi,I am Client!");
        Scanner in = new Scanner(client.getInputStream());
        if(in.hasNextLine()){
            System.out.println("服务端发来的消息："+in.nextLine());
        }
        out.close();
        in.close();
        client.close();
    }
}
