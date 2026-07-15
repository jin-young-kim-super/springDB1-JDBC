package hello.jdbc.connection;


import com.zaxxer.hikari.HikariDataSource;
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
        Connection connection1 = dataSource.getConnection(); // 최초로 커넥션 획득 요청이 오면 커넥션 풀 커넥션이 생성된다.
        Connection connection2 = dataSource.getConnection();
        log.info("conncetion={}, class={}",connection1,connection1.getClass());
        log.info("conncetion={}, class={}",connection2,connection2.getClass());
    }

    @Test
    // 이번에는 커넥션 풀을 통해 커넥션을 획득
    void dateSourceConnectionPool() throws SQLException, InterruptedException {
        // 설정 영역
        HikariDataSource dataSource = new HikariDataSource(); // 스프링 부트가 Hikari를 기본 등록
        dataSource.setJdbcUrl(ConnectionConst.URL);
        dataSource.setUsername(ConnectionConst.USERNAME);
        dataSource.setPassword(ConnectionConst.PASSSWORD);

        dataSource.setMaximumPoolSize(10);
        dataSource.setPoolName("MyPool");

        // 사용 영역
        updateDataSource(dataSource); // 이 과정에서 Connectino pool에 커넥션들이 생성된다.
        Thread.sleep(1000); // 커넥션 풀 생성 시에는 애플리케이션의 뜨는 속도에 영향을 미치지 않게 하기 위해 별도의 쓰레드로 실행을 한다.
    }                            //  그래서 updateDataSource 메서드를 실행시키는 쓰레드가 실행 종료될 까지 기다리게 하기 위하여 이 코드를 넣었다.
                                 // 그래야 커넥션이 생성되고 있는 로그를 볼 수가 있끼 떄문이다.
                                 // 왜냐하면 애플리케이션 쓰레드가 종료되버리면 프로그램이 종료되니깐 로그가 도중에 짤려 버린다


}
