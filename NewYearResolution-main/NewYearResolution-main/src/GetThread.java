import java.io.*;
import java.net.Socket;

public class GetThread implements Runnable{

    private static final String DEFAULT_FILE_PATH = "index.html";

    private Socket socket;
    private String filePath;

    public GetThread(Socket clientSocket, String filePath){
        this.socket = clientSocket;
        this.filePath = filePath;
    }

    @Override
    public void run() {

        try(DataOutputStream dout = new DataOutputStream(socket.getOutputStream())){

            System.out.println("GET Thread : DataOutputStream Ready");
            System.out.println("실행 경로 = " + new java.io.File(".").getAbsolutePath());

            // "/" 요청이면 index.html 로 매핑
            if(filePath.equals("/")) {
                filePath = DEFAULT_FILE_PATH;
            } else {
                filePath = filePath.substring(1); // 앞 "/" 제거
            }

            // 파라미터 제거
            if(filePath.contains("?")){
                filePath = filePath.split("\\?")[0];
            }

            File file = new File(filePath);

            if(file.exists()){

                FileInputStream in = new FileInputStream(file);
                byte[] fBytes = new byte[(int)file.length()];
                in.read(fBytes);
                in.close();

                dout.writeBytes("HTTP/1.1 200 OK\r\n");
                dout.writeBytes("Content-Type: text/html; charset=UTF-8\r\n");
                dout.writeBytes("Content-Length: " + fBytes.length + "\r\n");
                dout.writeBytes("Connection: close\r\n");
                dout.writeBytes("\r\n");

                dout.write(fBytes);
                dout.flush();

                System.out.println("GET Thread : Print Web Page");

            }else{
                System.out.println("GET Thread : RequestFile is not Exist");
            }

            socket.close();
            System.out.printf("Client Closed %s:%d]\n",
                    socket.getInetAddress(), socket.getPort());

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

