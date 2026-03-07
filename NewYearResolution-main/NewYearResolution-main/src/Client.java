import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;

public class Client implements Runnable{
    private final Socket clientsocket;

    public Client(Socket socket) {
        this.clientsocket = socket;
    }

    @Override
    public void run() {
        try {
            BufferedReader reader = new BufferedReader(
                    new InputStreamReader(clientsocket.getInputStream(), StandardCharsets.UTF_8));

            String requestLine = reader.readLine();

            if(requestLine == null){
                clientsocket.close();
                return;
            }

            String[] request = requestLine.split(" ");
            System.out.println("Request "+ Arrays.toString(request));

            HashMap<String, String> header = new HashMap<>();
            String line;

            while((line = reader.readLine()) != null && line.length() != 0) {
                int temp = line.indexOf(":");
                if(temp > 0){
                    header.put(line.substring(0, temp), line.substring(temp+2));
                }
            }

            switch (request[0]) {
                case "GET" -> {
                    new Thread(new GetThread(clientsocket, request[1])).start();
                }
                case "POST" -> {
                    int leng = Integer.parseInt(header.get("Content-Length"));
                    char[] body = new char[leng];
                    reader.read(body,0,leng);
                    new Thread(new PostThread(clientsocket, request[1], body)).start();
                }
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}