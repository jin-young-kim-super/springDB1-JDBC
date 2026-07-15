package hello.jdbc.connection;


import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

@Slf4j
public class ConnectionTest {

    @Test
    // 순수 JDBC만을 이용한 커넥션 획득
    void driverManager() throws SQLException {

        Connection connection1 = DriverManager.getConnection(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSSWORD);
        Connection connection2 = DriverManager.getConnection(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSSWORD);

        log.info("conncetion={}, class={}",connection1,connection1.getClass());
        log.info("conncetion={}, class={}",connection2,connection2.getClass());
    }

    @Test
    void dataSourceDriverManager() throws SQLException {
        // DriverManagerDataSource(스프링 제공) : DataSource 인터페이스를 상속 받은 구현체
        // -> DataSource 인터페이스에 의존하기 떄문에 커넥션 풀 구현체를 변경해도 클라이언트 코드에는 변경x
        // -> 또 다른 장점으로는 URL,USERNAME,PASSWORD를 객체 초기화 할 때 딱 1번만 해도 된다.
        // 이 점이 큰 차이를 만든다. 바로 "설정 영역과 사용 영역을 완전히 분리할 수가 있다는 것이다"
        // 여기는 설정 영역
        DataSource dataSource = new DriverManagerDataSource(ConnectionConst.URL, ConnectionConst.USERNAME, ConnectionConst.PASSSWORD);
        updateDataSource(dataSource);
    }

    // 사용 영역
    private void updateDataSource(DataSource dataSource) throws SQLException {
        Connection connection1 = dataSource.getConnection();
        Connection connection2 = dataSource.getConnection();
        log.info("conncetion={}, class={}",connection1,connection1.getClass());
        log.info("conncetion={}, class={}",connection2,connection2.getClass());
    }
}
