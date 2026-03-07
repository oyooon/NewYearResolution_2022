import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.bson.types.ObjectId;
import org.json.simple.JSONObject;

import java.io.*;
import java.net.Socket;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.regex.Pattern;

import static com.mongodb.client.model.Filters.*;

public class PostThread implements Runnable{
    private static final String DEFAULT_FILE_PATH = "index.html";
    private final Socket socket;
    private String filePath;
    private final Map<String, String> content;
    private final MongoClient mongoClient = new MongoClient( new MongoClientURI(DBinfo.getDB_URI()));
    private final MongoDatabase mongoDB = mongoClient.getDatabase(DBinfo.getDB());
    private final MongoCollection<Document> books = mongoDB.getCollection(DBinfo.getDB_C());
    public PostThread(Socket clientSocket, String filePath, char[] body) throws IOException {
        this.socket = clientSocket;
        this.filePath = filePath;
        this.content = new LinkedHashMap<String, String>();

        //URL 형식으로 데이터가 날아 오기 때문에 URL Decoding 해줘야 함 안하면 한글 깨짐
        String decodeData = URLDecoder.decode(String.valueOf(body), StandardCharsets.UTF_8);
        //쪼개기
        String[] Data = decodeData.split("&");
        for (String datum : Data) {
            String[] temp = datum.split("=");
            if (temp.length == 1) content.put(temp[0], "");
            else content.put(temp[0], temp[1]);

        }
    }

    @Override
    public void run() {
        try(DataOutputStream dout = new DataOutputStream(socket.getOutputStream())){
            System.out.println("POST Thread : DataOutputStream Ready");
            //파일명 정리 (getThread와 중복 나중에 정리할 것)
            String[] path_arr = filePath.split("/");
            String[] param_arr = null;
            File file = null;
            int FileLength =0;
            if(path_arr.length==0){ filePath=DEFAULT_FILE_PATH;}
            else {
                filePath = path_arr[path_arr.length-1];
                if(filePath.contains("?")){
                    param_arr = filePath.split("\\?");
                    filePath = param_arr[0];
                }
            }

            byte[] fData = null;
            if(filePath.equals("Select")){
                FindIterable<Document> document = null;
                //param값이 있다. 상세보기 페이지 | 게시판 검색
                if(param_arr!=null){
                    String[] param = param_arr[1].split("=");
                    if(param[0].equals("id")){
                        document = books.find(eq("_id", new ObjectId(param[1])));
                    }else if(param[0].equals("message")){
                        String dec_msg = URLDecoder.decode(param[1], StandardCharsets.UTF_8);
                        System.out.println(dec_msg);
                        document = books.find(or(in("subject", Pattern.compile(dec_msg)),in("user_name",Pattern.compile(dec_msg))));
                    }
                }else{
                    document = books.find();
                }
                MongoCursor<Document> cursor = document.iterator();
                JSONObject object;
                ArrayList<JSONObject> list = new ArrayList<>();
                //json parsing이 안되던 이유 -> db의 object id가 string 형태가 아니라서 **********************
                while(cursor.hasNext()){
                    object = new JSONObject(cursor.next());
                    String temp =object.get("_id").toString();
                    object.replace("_id",temp);
                    list.add(object);
                }
                fData = list.toString().getBytes(StandardCharsets.UTF_8);
                dout.writeBytes("HTTP/1.1 200 OK \r\n");
                dout.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
                dout.writeBytes("Content-Length: " + fData.length + "\r\n");
                dout.writeBytes("\r\n");
                dout.write(fData,0,fData.length);
                dout.writeBytes("\r\n");
                dout.flush();

            }else if(filePath.equals("Insert")){
                Document doc = new Document();
                for(String key : content.keySet()){
                    doc.append(key,content.get(key));
                }
                books.insertOne(doc);
                ObjectId id = (ObjectId)doc.get("_id");
                //insert 후 show 페이지로 이동시키기
                System.out.println("POST Thread : Data insert");
                dout.writeBytes("HTTP/1.1 302 Found \r\n");
                dout.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
                dout.writeBytes("Location: "+"http://localhost:8000/show.html?id="+id+" \r\n");
                dout.writeBytes("\r\n");
                dout.writeBytes("\r\n");
                dout.flush();
            }else if(filePath.equals("Delete")){
                books.findOneAndDelete(eq("_id", new ObjectId(param_arr[1])));
                System.out.println("POST Thread : Data Delete");
                dout.writeBytes("HTTP/1.1 302 Found \r\n");
                dout.writeBytes("Content-Type: text/html;charset=utf-8\r\n");
                dout.writeBytes("Location: "+"http://localhost:8000/"+" \r\n");
                dout.writeBytes("\r\n");
                dout.writeBytes("\r\n");
                dout.flush();
            }
            System.out.println("POST Thread : Print DB Data");
            socket.close();
            System.out.printf("Client Closed %s:%d]\n",socket.getInetAddress(), socket.getPort());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
}
