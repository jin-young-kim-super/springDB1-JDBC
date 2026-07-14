package hello.jdbc.connection;


import lombok.extern.slf4j.Slf4j;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class DBConnectionUtil {
    //Connection : JDBC가 제공하는 표준 API
    public static Connection getConnection() {
        try {
            // DriverMangager.getConnection : 라이브러리의 드라이버 목록을 뒤져서 JDBC 인터페이스(정확히는 Connection 인터페이스)를 상속받은 H2Driver가 만든 connection 구현체 반환
            // -> 우리는 JDBC 인터페이스로만 개발하면 되니깐 데이터베이스가 변경되도 이 코드는 변경 필요x
            Connection connection = DriverManager.getConnection(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSSWORD);
            log.info("get Connection={}, class={}",connection,connection.getClass());
            return connection;
        } catch (SQLException e) {
            throw new IllegalArgumentException(e);
        }
    }



}
