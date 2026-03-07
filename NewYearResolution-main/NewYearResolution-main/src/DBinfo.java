public class DBinfo {

    // MongoDB 접속 주소
    public static String getDB_URI() {
        return "mongodb://localhost:27017";
    }

    // 데이터베이스 이름
    public static String getDB() {
        return "newyear";
    }

    // 컬렉션 이름
    public static String getDB_C() {
        return "books";
    }
}
