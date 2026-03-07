import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

/**
 * Main을 스레드로 만든 경우
 * main thread에서 bufferedreader에서 socket이 close되면서 정지한다.
 */
public class main_backup2 implements Runnable {
    private static Socket clientSocket;
    public main_backup2(Socket clientSocket) { // 멀티 쓰레드 환경을 구축하기 위한 생성자. 각각의 개별 쓰레드를 생성
        this.clientSocket = clientSocket;
    }
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8000)){
            System.out.println("[Server Start] Waiting......................");
            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.printf("Client Accept %s:%d\n",clientSocket.getInetAddress(), clientSocket.getPort());

                main_backup2 ser = new main_backup2(clientSocket);
                new Thread(ser).start();

            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void run() {
        try {
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
                    Thread getThread;
                    getThread = new Thread(new GetThread(clientSocket, request[1]));
                    getThread.start();
                }
                case "POST" -> {
                    //Body 받기
                    int leng = Integer.parseInt(header.get("Content-Length"));
                    char[] body = new char[leng];
                    reader.read(body,0,leng);
                    Thread postThread;
                    postThread = new Thread(new PostThread(clientSocket, request[1], body));
                    postThread.start();
                }
            }


        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}