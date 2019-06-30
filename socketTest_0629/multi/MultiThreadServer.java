package exercise.socketTest_0629.multi;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Collection;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;


public class MultiThreadServer {
    //存储所有注册的客户端
    static  Map<String, Socket> map = new ConcurrentHashMap<>();

    private static class ExecuteClient implements Runnable{
        private Socket client;

        public ExecuteClient(Socket client) {
            this.client = client;
        }

        @Override
        public void run() {
            //获取客户端的输入流
            Scanner sc = null;
            String strFromCilent;
            try {
                sc = new Scanner(client.getInputStream());
            } catch (IOException e) {
                e.printStackTrace();
            }
            while(true){
                if(sc.hasNextLine()){
                    strFromCilent =sc.nextLine();
                    //注册： R：用户名
                    if(strFromCilent.startsWith("R")){
                        String userName = strFromCilent.split(String.valueOf(strFromCilent.charAt(1)))[1];
                        registerUser(userName,client);
                    }
                    //私聊： P：用户名-聊天信息
                    if(strFromCilent.startsWith("P")){
                        String userName = strFromCilent.split(String.valueOf(strFromCilent.charAt(1)))[1].split("-")[0];
                        String msg = strFromCilent.split("-")[1];
                        privateChat(userName,msg);
                    }
                    //群聊： G：聊天信息
                    if(strFromCilent.startsWith("G")){
                        String msg = strFromCilent.split(String.valueOf(strFromCilent.charAt(1)))[1];
                        groupChat(msg);
                    }
                    //退出：
                    if(strFromCilent.startsWith("bye")){
                        String userName = getUserName(client);
                        System.out.println("用户"+userName+"下线了。");
                        map.remove(userName);
                        System.out.println("当前聊天室"+map.size()+"人。");
                    }
                }
            }
        }

        // 注册
        public void registerUser(String userName,Socket client){
            map.put(userName,client);
            System.out.println("用户"+userName+"上线了。");
            System.out.println("当前聊天室"+map.size()+"人。");
            PrintStream out = null;
            try {
                out = new PrintStream(client.getOutputStream());
                out.println("用户注册成功");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public void groupChat(String msg){
            Collection<Socket> collection = map.values();
            for(Socket socket : collection){
                try {
                    PrintStream out = new PrintStream(socket.getOutputStream(),true,"UTF-8");
                    out.println("用户"+getUserName(client)+"发来的群聊消息:"+msg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        public void privateChat(String userName,String msg){
            Socket privateClient = map.get(userName);
            PrintStream out = null;
            try {
                out = new PrintStream(privateClient.getOutputStream(),true,"UTF-8");
                out.println("用户"+getUserName(client)+"发来的私聊消息:"+msg);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        public String getUserName(Socket client){
            for(String userName : map.keySet()){
                if(map.get(userName) == client){
                    return userName;
                }
            }
            return null;
        }
    }
    public static void main(String[] args) {
        ExecutorService executorService = Executors.newFixedThreadPool(20);
        try {
            ServerSocket serverSocket = new ServerSocket(8888);
            for(int i = 0; i<20; i++){
                System.out.println("等待客户端连接...");
                Socket client = serverSocket.accept();
                System.out.println("客户端连接成功，端口号为"+client.getPort());
                executorService.submit(new ExecuteClient(client));
            }
            executorService.shutdown();
            serverSocket.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
}
