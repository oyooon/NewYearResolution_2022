import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

/**
 * main serversocket을 통해 들어온 정보를 직접 처리 후 thread로 넘기는 경우
 * 동시 접속시 끊긴다.
 */
public class main_backup1 {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8000)){
            System.out.println("[Server Start] Waiting......................");
            Thread getThread;
            Thread postThread;
            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.printf("Client Accept %s:%d\n",clientSocket.getInetAddress(), clientSocket.getPort());

                BufferedReader reader  = new BufferedReader(new InputStreamReader(clientSocket.getInputStream(),StandardCharsets.UTF_8));
                //Start Line 받기
                String[] request = reader.readLine().split(" ");
                System.out.println("Request "+ Arrays.toString(request));

                //Header 받기
                HashMap<String, String> header = new HashMap<>();
                String line;
                int temp;
                while((line = reader.readLine()).length() !=0) {
                    temp = line.indexOf(":");
                    header.put(line.substring(0, temp), line.substring(temp+2));
                }

                switch (request[0]) {
                    case "GET" -> {
                        getThread = new Thread(new GetThread(clientSocket, request[1]));
                        getThread.start();
                    }
                    case "POST" -> {
                        //Body 받기
                        int leng = Integer.parseInt(header.get("Content-Length"));
                        char[] body = new char[leng];
                        reader.read(body,0,leng);
                        postThread = new Thread(new PostThread(clientSocket, request[1], body));
                        postThread.start();
                    }
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}