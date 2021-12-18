package parking_lot;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQL_Connection {
    public static Connection Connect() throws Exception{ // 连接MySQL服务器

        String username= "root";
        String password = "";
        String connectionUrl = "jdbc:mysql://localhost:3306/parking_lot" +
                "?useUnicode=true&characterEncoding=UTF-8" +
                "&zeroDateTimeBehavior=convertToNull&transformedBitIsBoolean=true";

        Connection conn = DriverManager.getConnection(connectionUrl, username, password);
        return conn;
    }
}
