import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * main serversocket을 통해 들어온 정보 client thread를 생성하여 넘기는 경우
 */
public class Main {
    public static void main(String[] args) {
        try(ServerSocket serverSocket = new ServerSocket(8000)){
            System.out.println("[Server Start] Waiting......................");
            while(true) {
                Socket clientSocket = serverSocket.accept();
                System.out.printf("Client Accept %s:%d\n",clientSocket.getInetAddress(), clientSocket.getPort());

                Thread main2_thread = new Thread(new Client(clientSocket));
                main2_thread.start();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
