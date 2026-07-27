package hello.jdbc.exception;


import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;

import java.net.ConnectException;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

/**
 * NetworkClient, Repository에서 언체크(런타임) 예외를 던졌으므로 서비스/컨트롤러에서 더이상 try-catch/throws 하는 코드가 사라졌다
 * -> 만약 구현 기술이 변경돼, 예외가 변경돼 던져지는 언체크(런타임) 예외가 변경돼도 서비스/컨트롤러의 코드 변경은 없다.
 * 그리고 언체크(런타임) 예외의 또 좋은 점은 만약 잡고 싶으면 얼마든지 try-catch로 잡을 수도 있다. 잡고 싶지만 않으면 그냥 던져지게 놔두면 되는 거고...
 * ※체크 예외의 문제로 인해 스프링에서는 대부분 언체크 예외가 대부분이고, 최근 라이브러리도 대부분 언체크(런타임) 예외를 던진다.
 * 그러나 언체크 예외는 모르고 지나치기 쉬우니깐 문서화(주석)가 중요하다
 */

@Slf4j
public class UnCheckedAppTest {

    static class Controller {
        Service service = new Service();

        public void request() {
            service.logic();
        }
    }

    static class Service {

        Repository repository = new Repository();
        NetworkClient networkClient = new NetworkClient();

        public void logic() {
            repository.call();
            networkClient.call();
        }
    }

    static class NetworkClient {
        public void call() {
            throw new RuntimeConncectException("연결 실패"); // 언체크(런타임) 예외이기에 throw나 try-catch 안해도 됨
        }
    }

    static class Repository {
        public void call()  {
            try {
                runSQL();
            }catch (SQLException e) {
                throw new RuntimeSQLException(e); // e : RuntimeSQLException 예외 발생 시, 그 이전 예외인 SQLException 예외까지 집어 넣음
            }
        }

        public void runSQL() throws SQLException {
            throw new SQLException("ex");
        }
    }

    // 언체크(런타임) 예외
    static class RuntimeConncectException extends RuntimeException {
        public RuntimeConncectException(String message) {
            super(message);
        }
    }

    // 언체크(런타임) 예외
    static class RuntimeSQLException extends RuntimeException {
        public RuntimeSQLException(Throwable cause) { // Thrownable : 그 이전 예외까지 다 집어 넣음
            super(cause);
        }
    }

    @Test
    void unChecked() {
        Controller controller = new Controller();
        assertThatThrownBy(() -> controller.request())
                .isInstanceOf(Exception.class);
    }
}
